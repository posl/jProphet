package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.Position;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.expr.Expression;

import jp.posl.jprophet.NodeUtility;

/**
 * 対象ステートメントの以前に現れているステートメントを，
 * 対象ステートメントの直前に挿入し，さらに置換操作(ValueReplacementOperation)を適用する．
 */
public class CopyReplaceOperation implements AstOperation{


    @Override
    public List<CompilationUnit> exec(Node targetNode){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        Node newTargetNode = NodeUtility.deepCopy(targetNode);
        if (targetNode instanceof Statement && !(targetNode instanceof BlockStmt)){
            //修正対象のステートメントの属するメソッドノードを取得
            //メソッド内のステートメント(修正対象のステートメントより前のもの)を収集
            List<Statement> statements = collectLocalStatements(newTargetNode);
            if (statements.size() != 0){
                //candidates = copyStatementBeforeTarget(statements, newRepairUnit);
                for (Statement statement : statements){
                    candidates.addAll(copyStatementBeforeTarget(statement, newTargetNode));
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
    private List<CompilationUnit> copyStatementBeforeTarget(Statement statement, Node node){
        Node targetNode = NodeUtility.deepCopy(node);
        final int beginLineOfTargetNode = getBeginLineNumber(targetNode).orElseThrow();
        final int endLineOfTargetNode = getEndLineNumber(targetNode).orElseThrow();
        final int beginLineOfStatement = getBeginLineNumber(statement).orElseThrow();
        final int endLineOfStatement = getEndLineNumber(statement).orElseThrow();

        List<Node> nodes = NodeUtility.getAllDescendantNodes(targetNode.findCompilationUnit().orElseThrow());
        List<Node> statements = nodes.stream()
            .filter(n -> n instanceof Statement)
            .filter(n -> getBeginLineNumber(n).orElseThrow() == beginLineOfStatement)
            .collect(Collectors.toList());

        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

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
            Statement newStatement = (Statement)statements.get(0);
            Statement copiedStatement = newStatement.clone();

            
            //テスト用
            Range statementRange = new Range(new Position(endLineOfStatement - beginLineOfStatement + 1, 0), new Position(endLineOfStatement - beginLineOfStatement + 1, 0));
            //addNodeTokenRange(copiedStatement, statementRange, statementRange);
            copiedStatement = copiedStatement.clone();
            //copiedStatement = JavaParser.parseStatement(copiedStatement.toString());
            

            //ここでaddBeforeする前にnewStatementのrangeを書き換える
            //changeRangeOfCopyStatement(copiedStatement, targetNode);
            nodeList.add(targetIndex, (Statement)copiedStatement);
            
            //ここでstatementInsertedNodeListの要素に対してgetAllUnderNode()をして,そのノードのrangeを変える
            //changeRangeOfBlockStmt(nodeList, copiedStatement, beginLineOfTargetNode);
            
            Node parent = nodeList.getParentNode().orElseThrow();
            //ここのparentのchildの並び順と,もとのnodeListの並び順が違う
            //cloneしたら順番は治るがrangeが元に戻る
            //addNodeRange(parent.findCompilationUnit().orElseThrow(), new Range(new Position(0, 0), new Position(endLineOfStatement - beginLineOfStatement + 1, 0)));

            List<Node> newRepairUnits = NodeUtility.getAllDescendantNodes(parent.findCompilationUnit().orElseThrow().clone());
            //List<Node> newRepairUnits = NodeUtility.getAllDescendantNodes(parent.clone().findCompilationUnit().orElseThrow().clone());
            //ここでrangeを変える

            //changeRangeAfterClone(newRepairUnits, beginLineOfTargetNode, endLineOfTargetNode, beginLineOfStatement, endLineOfStatement);
            //changeTokenRangeAfterClone(newRepairUnits, beginLineOfTargetNode, endLineOfTargetNode, beginLineOfStatement, endLineOfStatement);

            

            List<Node> expressionNodeRepairUnits = newRepairUnits.stream()
                .filter(unit -> unit instanceof Expression)
                .filter(unit -> getBeginLineNumber(unit).orElseThrow() == beginLineOfTargetNode)
                .collect(Collectors.toList());
            
            
            if (expressionNodeRepairUnits.size() >= 1){
                for (Node unit : expressionNodeRepairUnits){
                    //unit.findCompilationUnit().orElseThrow().recalculatePositions();
                    VariableReplacementOperation vr = new VariableReplacementOperation();
                    List<CompilationUnit> copiedNodeList = vr.exec(unit);
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

    private void changeRangeAfterClone(List<Node> newRepairUnits, int beginLineOfTargetNode, int endLineOfTargetNode, int beginLineOfStatement, int endLineOfStatement){
        Range otherRange = new Range(new Position(endLineOfStatement - beginLineOfStatement + 1, 0), new Position(endLineOfStatement - beginLineOfStatement + 1, 0));
        newRepairUnits.stream()
            .filter(unit -> getBeginLineNumber(unit).orElseThrow() >= endLineOfTargetNode || (getBeginLineNumber(unit).orElseThrow() == beginLineOfTargetNode && getEndLineNumber(unit).orElseThrow() == endLineOfTargetNode))
            .forEach(unit -> addNodeRange(unit, otherRange));

        Range statementRange = new Range(new Position(beginLineOfTargetNode - beginLineOfStatement, 0), new Position(beginLineOfTargetNode - beginLineOfStatement, 0));
        List<Node> statementNodes = newRepairUnits.stream()
            .filter(unit -> unit instanceof Statement)
            .filter(unit -> getBeginLineNumber(unit).orElseThrow() == beginLineOfStatement)
            .collect(Collectors.toList());
        if (statementNodes.size() == 2){
            addNodeRange(statementNodes.get(1), statementRange);
            NodeUtility.getAllDescendantNodes(statementNodes.get(1)).stream()
                .forEach(n -> addNodeRange(n, statementRange));
        }

        Range blockRange = new Range(new Position(0, 0), new Position(endLineOfStatement - beginLineOfStatement + 1, 0));
        newRepairUnits.stream()
            .filter(unit -> getBeginLineNumber(unit).orElseThrow() < beginLineOfTargetNode && getEndLineNumber(unit).orElseThrow() > endLineOfTargetNode)
            .forEach(unit -> addNodeRange(unit, blockRange));
    }

    /**
     * ノードのrangeを増やす
     * @param node ノード
     * @param addRange 増やしたいrange
     */
    private void addNodeTokenRange(Node node, Range addBeginRange, Range addEndRange){

        int beginLineOfBegin = node.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow().begin.line;
        int endLineOfBegin = node.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow().end.line;
        int beginColumnOfBegin = node.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow().begin.column;
        int endColumnOfBegin = node.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow().end.column;
        node.getTokenRange().orElseThrow().getBegin().setRange(new Range(new Position(beginLineOfBegin + addBeginRange.begin.line, beginColumnOfBegin + addBeginRange.begin.column), new Position(endLineOfBegin + addBeginRange.end.line, endColumnOfBegin + addBeginRange.end.column)));

        int beginLineOfEnd = node.getTokenRange().orElseThrow().getEnd().getRange().orElseThrow().begin.line;
        int endLineOfEnd = node.getTokenRange().orElseThrow().getEnd().getRange().orElseThrow().end.line;
        int beginColumnOfEnd = node.getTokenRange().orElseThrow().getEnd().getRange().orElseThrow().begin.column;
        int endColumnOfEnd = node.getTokenRange().orElseThrow().getEnd().getRange().orElseThrow().end.column;
        node.getTokenRange().orElseThrow().getEnd().setRange(new Range(new Position(beginLineOfEnd + addEndRange.begin.line, beginColumnOfEnd + addEndRange.begin.column), new Position(endLineOfEnd + addEndRange.end.line, endColumnOfEnd + addEndRange.end.column)));
    }

    private void changeTokenRangeAfterClone(List<Node> newRepairUnits, int beginLineOfTargetNode, int endLineOfTargetNode, int beginLineOfStatement, int endLineOfStatement){
        final Range otherBeginRange = new Range(new Position(endLineOfStatement - beginLineOfStatement + 1, 0), new Position(endLineOfStatement - beginLineOfStatement + 1, 0));
        final Range otherEndRange = new Range(new Position(endLineOfStatement - beginLineOfStatement + 1, 0), new Position(endLineOfStatement - beginLineOfStatement + 1, 0));
        newRepairUnits.stream()
            .filter(unit -> unit.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow().begin.line > endLineOfTargetNode || (unit.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow().begin.line == beginLineOfTargetNode && unit.getTokenRange().orElseThrow().getEnd().getRange().orElseThrow().end.line == endLineOfTargetNode))
            .forEach(unit -> addNodeTokenRange(unit, otherBeginRange, otherEndRange));

        
        Range statementRange = new Range(new Position(beginLineOfTargetNode - beginLineOfStatement, 0), new Position(beginLineOfTargetNode - beginLineOfStatement, 0));
        List<Node> statementNodes = newRepairUnits.stream()
            .filter(unit -> unit instanceof Statement)
            .filter(unit -> unit.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow().begin.line == beginLineOfStatement)
            .collect(Collectors.toList());
        if (statementNodes.size() == 2){
            addNodeRange(statementNodes.get(1), statementRange);
            NodeUtility.getAllDescendantNodes(statementNodes.get(1)).stream()
                .forEach(n -> addNodeTokenRange(n, statementRange, statementRange));
        }

        Range blockBeginRange = new Range(new Position(0, 0), new Position(0, 0));
        Range blockEndRange = new Range(new Position(endLineOfStatement - beginLineOfStatement + 1, 0), new Position(endLineOfStatement - beginLineOfStatement + 1, 0));
        newRepairUnits.stream()
            .filter(unit -> unit.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow().begin.line < beginLineOfTargetNode && unit.getTokenRange().orElseThrow().getEnd().getRange().orElseThrow().end.line > endLineOfTargetNode)
            .forEach(unit -> addNodeTokenRange(unit, blockBeginRange, blockEndRange));
        
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