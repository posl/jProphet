package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.BlockStmt;

import jp.posl.jprophet.patch.DiffWithType;
import jp.posl.jprophet.patch.DiffWithType.ModifyType;

/**
 * 対象ステートメントの以前に現れているステートメントを，
 * 対象ステートメントの直前に挿入し，さらに置換操作(ValueReplacementOperation)を適用する．
 */
public class CopyReplaceOperation implements AstOperation{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DiffWithType> exec(Node targetNode){
        if (!(targetNode instanceof Statement) || targetNode instanceof BlockStmt || targetNode instanceof SwitchEntryStmt) {
            return new ArrayList<DiffWithType>();
        }
        //修正対象のステートメントの属するメソッドノードを取得
        //メソッド内のステートメント(修正対象のステートメントより前のもの)を収集
        final List<Statement> statements = collectLocalStatementsBeforeTarget((Statement)targetNode);
        final List<Node> replacedVarStatements = copyAndPasteReplacedStatementToBeforeTarget(statements, targetNode);
        
        //修正対象のステートメントの直前に,収集したステートメントのNodeを追加
        final List<DiffWithType> diffWithTypes = replacedVarStatements.stream()
            .map(node -> new DiffWithType(ModifyType.INSERT, targetNode, node))
            .collect(Collectors.toList());
        return diffWithTypes;
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
        //BlockStmt, SwitchStmt, SwitchEntryStmtを全て除外する
        //TODO 除外していないfor文やif文は,コピペされた後,ブロックの中身のExpressionが置換される
        //TODO このようなコピペが必要でないならforStmtやifStmt,whileStmtにもfilterをかける
        return localStatements.stream()
            .filter(s -> getEndLineNumber(s).orElseThrow() < getBeginLineNumber(targetStatement).orElseThrow())
            .filter(s -> (s instanceof BlockStmt) == false)
            .filter(s -> (s instanceof SwitchStmt) == false)
            .filter(s -> (s instanceof SwitchEntryStmt) == false)
            .collect(Collectors.toList());
    }

    /**
     * targetNodeの直前にstatementをコピペしてVariableReplacementで変数を置換してできたcompilationUnitを返す
     * @param statement コピペされるステートメント
     * @param targetNode コピペ先の次の行のノード
     * @return compilationUnitのリスト
     */
    private List<Node> copyAndPasteReplacedStatementToBeforeTarget(List<Statement> statements, Node scope){
        final DeclarationCollector collector = new DeclarationCollector();
        final List<String> fieldNames = collector.collectFileds(scope)
            .stream().map(var -> var.getName().asString()).collect(Collectors.toList());
        final List<String> localVarNames = collector.collectLocalVarsDeclared(scope)
            .stream().map(var -> var.getName().asString()).collect(Collectors.toList());
        final List<String> parameterNames = collector.collectParameters(scope)
            .stream().map(var -> var.getName().asString()).collect(Collectors.toList());

        VariableReplacer replacer = new VariableReplacer();

        final List<Node> replacedVarStatements = new ArrayList<Node>();
        statements.stream()
            .map(statement -> replacer.replaceAllVariablesForStatement(statement, fieldNames, localVarNames, parameterNames))
            .forEach(replacedNodes -> replacedVarStatements.addAll(replacedNodes));

        return replacedVarStatements;
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