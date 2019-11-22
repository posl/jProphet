package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.BlockStmt;

import jp.posl.jprophet.NodeUtility;

/**
 * 対象ステートメントの以前に現れているステートメントを，
 * 対象ステートメントの直前に挿入し，さらに置換操作(ValueReplacementOperation)を適用する．
 */
public class CopyReplaceOperation implements AstOperation{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CompilationUnit> exec(Node targetNode){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        if (targetNode instanceof Statement && !(targetNode instanceof BlockStmt)){
            //修正対象のステートメントの属するメソッドノードを取得
            //メソッド内のステートメント(修正対象のステートメントより前のもの)を収集
            List<Statement> statements = collectLocalStatementsBeforeTarget((Statement)targetNode);
            for (Statement statement : statements){
                candidates.addAll(copyAndPasteReplacedStatementToBeforeTarget(statement, targetNode));
            }
        }
        //修正対象のステートメントの直前に,収集したステートメントのNodeを追加
        return candidates;
    }

    /**
     * beforeThisStatementを含むメソッド内のステートメントで,beforeThisStatementよりも上の行にあるものを収集
     * @param beforeThisStatement 修正対象のノード
     * @return 収集したステートメントのリスト
     */
    private List<Statement> collectLocalStatementsBeforeTarget(Statement targetStatement){
        MethodDeclaration methodNode;
        try {
            methodNode =  targetStatement.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<Statement>();
        }

        List<Statement> localStatements = methodNode.findAll(Statement.class);

        //targetStatementを含むそれより後ろの行の要素を全て消す
        //BlockStmtを全て除外する
        return localStatements.stream()
            .filter(s -> getEndLineNumber(s).orElseThrow() < getBeginLineNumber(targetStatement).orElseThrow())
            .filter(s -> (s instanceof BlockStmt) == false)
            .collect(Collectors.toList());
    }

    /**
     * targetNodeの直前にstatementをコピペしてVariableReplacementで変数を置換してできたcompilationUnitを返す
     * @param statement コピペするステートメント
     * @param targetNode statementがコピペされる直後の行のノード
     * @return compilationUnitのリスト
     */
    private List<CompilationUnit> copyAndPasteReplacedStatementToBeforeTarget(Statement statement, Node targetNode){
        Node copiedNode = NodeUtility.insertNodeWithNewLine(statement, targetNode);
        List<Node> copiedNodeDescendants = NodeUtility.getAllDescendantNodes(copiedNode);

        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        for (Node descendant : copiedNodeDescendants){
            VariableReplacementOperation vr = new VariableReplacementOperation();
            List<CompilationUnit> copiedNodeList = vr.exec(descendant);
            candidates.addAll(copiedNodeList);
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
}