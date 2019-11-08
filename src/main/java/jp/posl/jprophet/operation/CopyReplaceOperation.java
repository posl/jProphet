package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.expr.Expression;

import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.RepairUnit;

/**
 * 対象ステートメントの以前に現れているステートメントを，
 * 対象ステートメントの直前に挿入し，さらに置換操作(ValueReplacementOperation)を適用する．
 */
public class CopyReplaceOperation implements AstOperation{


    @Override
    public List<RepairUnit> exec(RepairUnit repairUnit){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        Node targetNode = repairUnit.getTargetNode();
        //修正対象のステートメントの属するメソッドノードを取得
        //メソッド内のステートメント(修正対象のステートメントより前のもの)を収集
        List<Statement> statements = collectLocalStatements(targetNode);
        if (statements.size() != 0){
            candidates = copyStatementBeforeTarget(statements, repairUnit);
        }
        //修正対象のステートメントの直前に,収集したステートメントのNodeを追加
        return candidates;
    }

    /**
     * targetNodeを含むメソッド内のステートメントで,targetNodeよりも上にあるものを取得
     * @param targetNode ターゲットノード
     * @return ステートメントのリスト
     */
    private List<Statement> collectLocalStatements(Node targetNode){
        MethodDeclaration methodNode;
        try {
            methodNode =  targetNode.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<Statement>();
        }

        List<Statement> localStatements = methodNode.findAll(Statement.class);

        //targetStatementを含むそれより後ろの行の要素を全て消す
        //BlockStmtを全て除外する
        return localStatements.stream()
            .filter(s -> getEndLineNumber(s).orElseThrow() < getBeginLineNumber(targetNode).orElseThrow())
            .filter(s -> (s instanceof BlockStmt) == false)
            .collect(Collectors.toList());
    }

    /**
     * targetNodeの直前にstatementをコピペしてrepairUnitを生成してそのリストを返す
     * @param statements コピペするステートメントのリスト
     * @param repairUnit targetNodeを含むrepairUnit
     * @return repairUnitのリスト
     */
    private List<RepairUnit> copyStatementBeforeTarget(List<Statement> statements, RepairUnit repairUnit){
        Node targetNode = repairUnit.getTargetNode();
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        BlockStmt blockStatement;
        try {
            blockStatement =  targetNode.findParent(BlockStmt.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return candidates;
        }
        
        for (Statement statement : statements){            
            NodeList<Statement> nodeList = blockStatement.clone().getStatements();
            //coloneしているのにtargetNodeは同じものとして認識されている?
            int targetIndex = nodeList.indexOf(targetNode);
            if(targetNode instanceof Statement && targetIndex != -1){
                Statement newStatement = statement.clone();
                //ここでaddBeforeする前にnewStatementのrangeを書き換える
                changeRangeOfCopyStatement(newStatement, targetNode);
                NodeList<Statement> statementInsertedNodeList = nodeList.addBefore(newStatement, (Statement)targetNode);
                //ここでstatementInsertedNodeListの要素に対してgetAllUnderNode()をして,そのノードのrangeを変える
                changeRangeOfBlockStmt(statementInsertedNodeList, newStatement, targetNode);

                RepairUnit newCandidate = RepairUnit.deepCopy(repairUnit);
                //ここでコピー後のtargetNodeを含むblockStmtより後ろのrangeを持つNodeのrangeを変える
                int width = getEndLineNumber(statement).orElseThrow() - getBeginLineNumber(statement).orElseThrow() + 1;
                changeRangeAfterBlockStmt(newCandidate, width);

                //taergetNodeの親ノード(多分BlockStmt)を丸ごと置き換えることでコピペしている
                //BlockStmtのsetStatements(NodeList)がreplaceの代わりにならないか?
                ((BlockStmt)newCandidate.getTargetNode().getParentNode().orElseThrow()).setStatements(statementInsertedNodeList);
                //newCandidate.getTargetNode().getParentNode().orElseThrow().replace(statementInsertedNodeList.get(targetIndex).getParentNode().orElseThrow());
                //candidates.add(newCandidate);
                //Range range = statement.getRange().orElseThrow();
                //changeRange(newCandidate, range, targetNode, newStatement);
                List<RepairUnit> repairUnits = getAllRepairUnit(newCandidate.getCompilationUnit());

                //Nodeをコピペして任意の場所に入れると,コピペ前のNodeの情報がそのまま入るので行番号がおかしくなる
                //compilationUnitのtoString()ではちゃんとステートメントが意図した場所にコピペされている
                //compilationUnitをtoString()してAstGeneratorにかけてcompilationUnitを作り直す
                //問題点 : compilationUnitをtoString()すると改行や空白が入ってしまうので元のコードと形が変わる
                //        行数が変わるからVariableReplacementにかけたい行がわからない
                
                //TODO この辺の処理をgetExpression()でなんとかする?
                //TODO getExpression()だとstatementからRepairUnitが作れなさそう
                AstGenerator astGenerator = new AstGenerator();
                //List<RepairUnit> repairUnits = astGenerator.getAllRepairUnit(newCandidate.getCompilationUnit().toString());
                List<RepairUnit> expressionNodeRepairUnits = repairUnits.stream()
                    .filter(unit -> unit.getTargetNode() instanceof Expression)
                    //.filter(unit -> (unit.toString() + ";").equals(statement.toString()))
                    .filter(unit -> getBeginLineNumber(unit.getTargetNode()).orElseThrow() == (getBeginLineNumber(targetNode).orElseThrow()))
                    .collect(Collectors.toList());
                
                if (expressionNodeRepairUnits.size() >= 1){
                    //行番号で置換する場所を指定できればexpressionNodeRepairUnitsのsizeが1になるのでget(0)
                    RepairUnit leunit = expressionNodeRepairUnits.get(0);
                    VariableReplacementOperation vr = new VariableReplacementOperation();
                    List<RepairUnit> copiedNodeList = vr.exec(leunit);
                    candidates.addAll(copiedNodeList);
                }
                
            }
        }
        return candidates;
    }

    /**
     * ノードの始まりの行番号を取得する
     * @param node ノード
     * @return ノードの始まりの行番号
     */
    private Optional<Integer> getBeginLineNumber(Node node) {
        try {
            Range range = node.getRange().orElseThrow();        
            return Optional.of(range.begin.line);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * ノードの終わりの行番号を取得
     * @param node ノード
     * @return ノードの終わりの行番号
     */
    private Optional<Integer> getEndLineNumber(Node node) {
        try {
            Range range = node.getRange().orElseThrow();        
            return Optional.of(range.end.line);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * ノードの始まりのコラム番号を取得する
     * @param node ノード
     * @return ノードの始まりのコラム番号
     */
    private Optional<Integer> getBeginColumnNumber(Node node) {
        try {
            Range range = node.getRange().orElseThrow();        
            return Optional.of(range.begin.column);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * ノードの終わりのコラム番号を取得する
     * @param node ノード
     * @return ノードの終わりのコラム番号
     */
    private Optional<Integer> getEndColumnNumber(Node node) {
        try {
            Range range = node.getRange().orElseThrow();        
            return Optional.of(range.end.column);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void changeRange(RepairUnit repairUnit, Range range, Node targetNode, Statement newStatement){
        List<RepairUnit> repairUnits = getAllRepairUnit(repairUnit.getCompilationUnit());
        int width = range.end.line - range.begin.line + 1;
        final int targetBeginLine = getBeginLineNumber(targetNode).orElseThrow();
        final int targetEndLine = getEndLineNumber(targetNode).orElseThrow();
        List<Node> childNodes = getAllUnderNode(newStatement);
        childNodes.add(newStatement);


        //TODO statementがブロック内の時はendだけ増やさないといけない
        //TODO コピペする前も後もrangeが増えてるのをなんとかする

        for (RepairUnit unit : repairUnits){
            if (childNodes.contains(unit.getTargetNode())){
                //うまく動いていない
                int beginLine = getBeginLineNumber(unit.getTargetNode()).orElseThrow();
                int beginColumn = getBeginColumnNumber(unit.getTargetNode()).orElseThrow();
                int endLine = getEndLineNumber(unit.getTargetNode()).orElseThrow();
                int endColumn = getEndColumnNumber(unit.getTargetNode()).orElseThrow();
                int width2 = getBeginLineNumber(targetNode).orElseThrow() - getBeginLineNumber(unit.getTargetNode()).orElseThrow();
                unit.getTargetNode().setRange(new Range(new Position(beginLine + width2, beginColumn), new Position(endLine + width2, endColumn)));
            }else if (getBeginLineNumber(unit.getTargetNode()).orElseThrow() >= targetBeginLine && getEndLineNumber(unit.getTargetNode()).orElseThrow() >= targetEndLine){
                int beginLine = getBeginLineNumber(unit.getTargetNode()).orElseThrow();
                int beginColumn = getBeginColumnNumber(unit.getTargetNode()).orElseThrow();
                int endLine = getEndLineNumber(unit.getTargetNode()).orElseThrow();
                int endColumn = getEndColumnNumber(unit.getTargetNode()).orElseThrow();
                unit.getTargetNode().setRange(new Range(new Position(beginLine + width, beginColumn), new Position(endLine + width, endColumn)));
            }
        }
    }

    /**
     * ノードのrangeを増やす
     * @param node ノード
     * @param addRange 増やしたいrange
     */
    private void addNodeRange(Node node, Range addRange){
        int beginLine = getBeginLineNumber(node).orElseThrow();
        int beginColumn = getBeginColumnNumber(node).orElseThrow();
        int endLine = getEndLineNumber(node).orElseThrow();
        int endColumn = getEndColumnNumber(node).orElseThrow();
        node.setRange(new Range(new Position(beginLine + addRange.begin.line, beginColumn + addRange.begin.column), new Position(endLine + addRange.end.line, endColumn + addRange.end.column)));
    }

    private void changeRangeOfCopyStatement(Statement statement, Node targetNode){
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(statement);
        nodes.addAll(getAllUnderNode(statement));
        int distance = getBeginLineNumber(targetNode).orElseThrow() - getBeginLineNumber(statement).orElseThrow();
        Range range = new Range(new Position(distance, 0), new Position(distance, 0));
        nodes.forEach(s -> addNodeRange(s, range));
    }

    private void changeRangeOfBlockStmt(NodeList<Statement> nodeList, Statement statement, Node targetNode){
        List<Node> nodes = new ArrayList<Node>();
        nodeList.forEach(s -> nodes.add(s));
        nodeList.forEach(s -> nodes.addAll(getAllUnderNode(s)));
        List<Node> statementChildren = getAllUnderNode(statement);
        statementChildren.add(statement);
        int width = getEndLineNumber(statement).orElseThrow() - getBeginLineNumber(statement).orElseThrow() + 1;

        Range range = new Range(new Position(width, 0), new Position(width, 0));
        nodes.stream()
            .filter(s -> !statementChildren.contains(s))
            .filter(s -> s.getRange().orElseThrow().begin.line >= targetNode.getRange().orElseThrow().end.line)
            .forEach(s -> addNodeRange(s, range));
        
    }

    private void changeRangeAfterBlockStmt(RepairUnit newCandidate, int width){
        //newCandidateのtargetUnitのBlockStmtを取得(getParent()?)してrangeをとってそれより後ろのNodeのrangeを変える
        final int blockEndLine = getEndLineNumber(newCandidate.getTargetNode().getParentNode().orElseThrow()).orElseThrow();
        List<RepairUnit> repairUnits = getAllRepairUnit(newCandidate.getCompilationUnit());
        Range range = new Range(new Position(width, 0), new Position(width, 0));
        repairUnits.stream()
            .filter(s -> getBeginLineNumber(s.getTargetNode()).orElseThrow() >= blockEndLine)
            .forEach(s -> addNodeRange(s.getTargetNode(), range));
    }


    /**
     * compileUnitから全てのASTノードを抽出し，修正単位であるRepairUnitを取得する.
     * 
     * @param compilationUnit AST抽出対象のソースコード
     * @return 修正対象のASTノードとコンパイルユニットを持った修正単位であるRepairUnitのリスト
     */
    private List<RepairUnit> getAllRepairUnit(CompilationUnit compilationUnit){
        List<RepairUnit> repairUnits = new ArrayList<RepairUnit>();
        for(int i = 0;/*終了条件なし*/; i++){
            CompilationUnit newCompilationUnit;   //RepairUnitごとに新しいインスタンスの生成
            newCompilationUnit = compilationUnit;
            // なくなるまで順にASTノードを取り出す
            try {
                Node node = AstGenerator.findByLevelOrderIndex(newCompilationUnit.findRootNode(), i).orElseThrow(); 
                repairUnits.add(new RepairUnit(node, i, compilationUnit)); 
            } catch (NoSuchElementException e) {
                return repairUnits;
            }
        }
    }

    /**
     * 再帰的によじぶんより下にあるnodeを全て集める
     * @param node
     * @return ノードのリスト
     */
    private List<Node> getAllUnderNode(Node node){
        List<Node> nodes = new ArrayList<Node>();
        List<Node> childNodes = node.getChildNodes();
        nodes.addAll(childNodes);
        childNodes.forEach(s -> nodes.addAll(getAllUnderNode(s)));
        return nodes;
    }
}