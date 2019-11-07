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
            int targetIndex = nodeList.indexOf(targetNode);
            if(targetNode instanceof Statement && targetIndex != -1){
                //statementをclone()してそれのrangeを先に変えてaddBeforeする
                Statement newStatement = statement.clone();
                setStatementRange(newStatement, targetNode);

                NodeList<Statement> statementInsertedNodeList = nodeList.addBefore(newStatement, (Statement)targetNode);
                RepairUnit newCandidate = RepairUnit.deepCopy(repairUnit);
                //taergetNodeの親ノード(多分BlockStmt)を丸ごと置き換えることでコピペしている
                //BlockStmtのsetStatements(NodeList)がreplaceの代わりにならないか?
                newCandidate.getTargetNode().getParentNode().orElseThrow().replace(statementInsertedNodeList.get(targetIndex).getParentNode().orElseThrow());
                //ここでrangeの値を変える
                Range range = statement.getRange().orElseThrow();
                changeRange(newCandidate, range, targetIndex);
                //candidates.add(newCandidate);
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

    private void changeRange(RepairUnit repairUnit, Range range, int targetIndex){
        List<RepairUnit> repairUnits = getAllRepairUnit(repairUnit.getCompilationUnit());
        int width = range.end.line - range.begin.line + 1;
        /*
        //streamで書くと横長になりそうだからとりあえずfor文で
        repairUnits.stream()
            .filter(unit -> getBeginLineNumber(unit.getTargetNode()).orElseThrow() >= targetIndex)
            .forEach(unit -> unit.getTargetNode().setRange(unit.getTargetNode().getRange().orElseThrow()));
        */

        //TODO statementがブロック内の時はendだけ増やさないといけない
        //TODO コピペする前も後もrangeが増えてるのをなんとかする

        for (RepairUnit unit : repairUnits){
            if (getBeginLineNumber(unit.getTargetNode()).orElseThrow() >= targetIndex){
                int beginLine = getBeginLineNumber(unit.getTargetNode()).orElseThrow();
                int beginColumn = getBeginColumnNumber(unit.getTargetNode()).orElseThrow();
                int endLine = getEndLineNumber(unit.getTargetNode()).orElseThrow();
                int endColumn = getEndColumnNumber(unit.getTargetNode()).orElseThrow();
                unit.getTargetNode().setRange(new Range(new Position(beginLine + width, beginColumn), new Position(endLine + width, endColumn)));
            }
        }
    }

    private void setStatementRange(Statement statement, Node targetNode){
        int width = getBeginLineNumber(targetNode).orElseThrow() - getBeginLineNumber(statement).orElseThrow();
        int beginLine = getBeginLineNumber(statement).orElseThrow();
        int beginColumn = getBeginColumnNumber(statement).orElseThrow();
        int endLine = getEndLineNumber(statement).orElseThrow();
        int endColumn = getEndColumnNumber(statement).orElseThrow();
        statement.setRange(new Range(new Position(beginLine + width, beginColumn), new Position(endLine + width, endColumn)));
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
}