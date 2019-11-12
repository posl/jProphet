package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        RepairUnit newRepairUnit = RepairUnit.deepCopy(repairUnit);
        Node targetNode = newRepairUnit.getTargetNode();
        if (targetNode instanceof Statement){
            //修正対象のステートメントの属するメソッドノードを取得
            //メソッド内のステートメント(修正対象のステートメントより前のもの)を収集
            List<Statement> statements = collectLocalStatements(targetNode);
            if (statements.size() != 0){
                //candidates = copyStatementBeforeTarget(statements, newRepairUnit);
                for (Statement statement : statements){
                    candidates.addAll(copyStatementBeforeTarget2(statement, newRepairUnit));
                }
            }
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
        int beginLineOfTargetNode = getBeginLineNumber(targetNode).orElseThrow();
        int endLineOfTargetNode = getEndLineNumber(targetNode).orElseThrow();
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        BlockStmt blockStatement;
        try {
            blockStatement =  targetNode.findParent(BlockStmt.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return candidates;
        }
        
        for (Statement statement : statements){            
            NodeList<Statement> nodeList = blockStatement.getStatements();
            int targetIndex = nodeList.indexOf(targetNode);
            if(targetNode instanceof Statement && targetIndex != -1){
                //newStatementのparentNodeが消えている
                Statement newStatement = statement.clone();
                //ここでaddBeforeする前にnewStatementのrangeを書き換える
                changeRangeOfCopyStatement(newStatement, targetNode);
                nodeList.add(targetIndex, newStatement);
                //ここでstatementInsertedNodeListの要素に対してgetAllUnderNode()をして,そのノードのrangeを変える
                changeRangeOfBlockStmt(nodeList, newStatement, beginLineOfTargetNode);
                Node parent = nodeList.getParentNode().orElseThrow();
                List<RepairUnit> repairUnits = getAllRepairUnit(parent.findCompilationUnit().orElseThrow());
                
                //TODO この辺の処理をgetExpression()でなんとかする?
                //TODO getExpression()だとstatementからRepairUnitが作れなさそう
                List<RepairUnit> expressionNodeRepairUnits = repairUnits.stream()
                    .filter(unit -> unit.getTargetNode() instanceof Expression)
                    .filter(unit -> getBeginLineNumber(unit.getTargetNode()).orElseThrow() == beginLineOfTargetNode)
                    .collect(Collectors.toList());
                
                if (expressionNodeRepairUnits.size() >= 1){
                    for (RepairUnit unit : expressionNodeRepairUnits){
                        VariableReplacementOperation vr = new VariableReplacementOperation();
                        List<RepairUnit> copiedNodeList = vr.exec(unit);
                        candidates.addAll(copiedNodeList);
                    }
                }
            
            }
        }
        return candidates;
    }

    private List<RepairUnit> copyStatementBeforeTarget2(Statement statement, RepairUnit repairUnit){
        RepairUnit newRepairUnit = RepairUnit.deepCopy(repairUnit);
        Node targetNode = newRepairUnit.getTargetNode();
        int beginLineOfTargetNode = getBeginLineNumber(targetNode).orElseThrow();
        int endLineOfTargetNode = getEndLineNumber(targetNode).orElseThrow();
        int beginLineOfStatement = getBeginLineNumber(statement).orElseThrow();
        int endLineOfStatement = getEndLineNumber(statement).orElseThrow();

        List<RepairUnit> repairUnits = getAllRepairUnit(targetNode.findCompilationUnit().orElseThrow());
        List<RepairUnit> statements = repairUnits.stream()
            .filter(unit -> unit.getTargetNode() instanceof Statement)
            .filter(unit -> getBeginLineNumber(unit.getTargetNode()).orElseThrow() == beginLineOfStatement)
            .collect(Collectors.toList());

        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        BlockStmt blockStatement;
        try {
            blockStatement =  targetNode.findParent(BlockStmt.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return candidates;
        }
                   
        NodeList<Statement> nodeList = blockStatement.getStatements();
        int targetIndex = nodeList.indexOf(targetNode);
        if (targetNode instanceof Statement && targetIndex != -1 && statements.size() != 0){
            Statement newStatement = (Statement)statements.get(0).getTargetNode();
            Statement copiedStatement = newStatement.clone();
            //ここでaddBeforeする前にnewStatementのrangeを書き換える
            changeRangeOfCopyStatement(copiedStatement, targetNode);
            nodeList.add(targetIndex, copiedStatement);
            //ここでstatementInsertedNodeListの要素に対してgetAllUnderNode()をして,そのノードのrangeを変える
            changeRangeOfBlockStmt(nodeList, copiedStatement, beginLineOfTargetNode);
            
            Node parent = nodeList.getParentNode().orElseThrow();
            //ここのparentのchildの並び順と,もとのnodeListの並び順が違う
            //cloneしたら順番は治るがrangeが元に戻る

            List<RepairUnit> newRepairUnits = getAllRepairUnit(parent.findCompilationUnit().orElseThrow().clone());
            //ここでrangeを変えればいける?
            

            List<RepairUnit> expressionNodeRepairUnits = newRepairUnits.stream()
                .filter(unit -> unit.getTargetNode() instanceof Expression)
                .filter(unit -> getBeginLineNumber(unit.getTargetNode()).orElseThrow() == beginLineOfTargetNode)
                .collect(Collectors.toList());
            
            
            if (expressionNodeRepairUnits.size() >= 1){
                for (RepairUnit unit : expressionNodeRepairUnits){
                    VariableReplacementOperation vr = new VariableReplacementOperation();
                    List<RepairUnit> copiedNodeList = vr.exec(unit);
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

    /**
     * コピペするノードのrangeをペーストする前に書き換える
     * @param statement コピペ対象のノード
     * @param targetNode コピペされる行にあるノード
     */
    private void changeRangeOfCopyStatement(Statement statement, Node targetNode){
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(statement);
        nodes.addAll(getAllUnderNode(statement));
        int distance = getBeginLineNumber(targetNode).orElseThrow() - getBeginLineNumber(statement).orElseThrow();
        Range range = new Range(new Position(distance, 0), new Position(distance, 0));
        nodes.forEach(s -> addNodeRange(s, range));
    }

    /**
     * NodeListより下にいるノードのrangeを変える
     * @param nodeList
     * @param statement
     * @param targetNode
     */
    private void changeRangeOfBlockStmt(NodeList<Statement> nodeList, Statement statement, int beginLineOfTargetNode){
        List<Node> nodes = new ArrayList<Node>();
        nodeList.forEach(s -> nodes.add(s));
        nodeList.forEach(s -> nodes.addAll(getAllUnderNode(s)));
        List<Node> statementChildren = getAllUnderNode(statement);
        statementChildren.add(statement);
        int width = getEndLineNumber(statement).orElseThrow() - getBeginLineNumber(statement).orElseThrow() + 1;

        Range range = new Range(new Position(width, 0), new Position(width, 0));
        nodes.stream()
            .filter(s -> !statementChildren.contains(s))
            .filter(s -> s.getRange().orElseThrow().begin.line >= beginLineOfTargetNode)
            .forEach(s -> addNodeRange(s, range));
        
    }

    /**
     * bloclStmtより後ろの行のノードのrangeを変える
     * @param newCandidate
     * @param width
     */
    private void changeRangeAfterBlockStmt(RepairUnit newCandidate, int width){
        //newCandidateのtargetUnitのBlockStmtを取得(getParent()?)してrangeをとってそれより後ろのNodeのrangeを変える
        final int blockEndLine = getEndLineNumber(newCandidate.getTargetNode().getParentNode().orElseThrow()).orElseThrow();
        List<RepairUnit> repairUnits = getAllRepairUnit(newCandidate.getCompilationUnit());
        final Range range = new Range(new Position(width, 0), new Position(width, 0));
        repairUnits.stream()
            .filter(s -> getBeginLineNumber(s.getTargetNode()).orElseThrow() >= blockEndLine)
            .forEach(s -> addNodeRange(s.getTargetNode(), range));
    }

    private void changeRangeBlockStmtBySpreading(RepairUnit newCandidate, int width){
        final Range blockRange = new Range(new Position(0, 0), new Position(width, 0));
        List<RepairUnit> repairUnits = getAllRepairUnit(newCandidate.getCompilationUnit());
        repairUnits.stream()
            .filter(s -> getBeginLineNumber(s.getTargetNode()).orElseThrow() < getBeginLineNumber(newCandidate.getTargetNode()).orElseThrow())
            .filter(s -> getEndLineNumber(s.getTargetNode()).orElseThrow() > getEndLineNumber(newCandidate.getTargetNode()).orElseThrow())
            .forEach(s -> addNodeRange(s.getTargetNode(), blockRange));
    }

    /**
     * compileUnitから全てのASTノードを抽出し，修正単位であるRepairUnitを取得する.
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
     * 再帰的によ自分より下にあるnodeを全て集める
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