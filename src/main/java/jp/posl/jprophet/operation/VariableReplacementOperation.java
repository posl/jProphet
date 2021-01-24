package jp.posl.jprophet.operation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;

/**
 * 対象ステートメント中の変数を別のもので置き換える操作を行う
 */
public class VariableReplacementOperation implements AstOperation {
    /**
     * <h4>変数の置換操作を行い修正パッチ候補を生成する</h4>
     * <p>
     * 代入文の右辺の値と，メソッド呼び出しの引数を対象に，クラスのメンバ変数及びメソッドのローカル変数，仮引数で置換を行う
     * 一つの修正パッチ候補につき一箇所の置換
     * </p>
     * <p>
     * MethodCallExprとAssignExprをtargetNodeとして受け取った場合に置換が行われる
     * </p>
     * 
     * @return 生成された修正後のCompilationUnitのリスト
     */
    public List<OperationDiff> exec(Node targetNode) {
        if (!(targetNode.findParent(Statement.class).isPresent()))
            return Collections.emptyList();
        final DeclarationCollector collector = new DeclarationCollector();

        //TODO: 変数名を集める処理は他にもあるので共通メソッドにした方がいいかも...？
        final Map<String, String> fieldNameToType = collector.collectFileds(targetNode).stream()
            .collect(Collectors.toMap(
                var -> var.getName().toString(),
                var -> var.getTypeAsString()
            ));
        final Map<String, String> localVarNameToType = collector.collectLocalVarsDeclared(targetNode).stream()
            .collect(Collectors.toMap(
                var -> var.getName().toString(),
                var -> var.getTypeAsString()
            ));
        final Map<String, String> parameterNameToType = collector.collectParameters(targetNode).stream()
            .collect(Collectors.toMap(
                var -> var.getName().toString(),
                var -> var.getTypeAsString()
            ));

        final VariableReplacer replacer = new VariableReplacer();
        final List<Node> replacedNodes = replacer.replaceAllVariables(targetNode, fieldNameToType, localVarNameToType, parameterNameToType);
        List<OperationDiff> candidates = replacedNodes.stream()
            .map(replacedNode -> {
                final Node copiedTargetNode = NodeUtility.deepCopy(targetNode);
                final Node replacedTargetNode = NodeUtility.replaceNode(replacedNode, copiedTargetNode).orElseThrow();
                if (replacedTargetNode instanceof Statement) {
                    return new OperationDiff(ModifyType.CHANGE, targetNode, replacedTargetNode);
                }
                final Statement replacedTargetStmt = replacedTargetNode.findParent(Statement.class).orElseThrow();
                final Statement targetStmt = targetNode.findParent(Statement.class).orElseThrow();
                return new OperationDiff(ModifyType.CHANGE, targetStmt, replacedTargetStmt);
            }).collect(Collectors.toList());

        return candidates;
    }
}