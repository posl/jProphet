package jp.posl.jprophet.operation;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

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
        final DeclarationCollector collector = new DeclarationCollector();

        //TODO: 変数名を集める処理は他にもあるので共通メソッドにした方がいいかも...？
        final List<String> fieldNames = collector.collectFileds(targetNode)
            .stream().map(var -> var.getName().asString()).collect(Collectors.toList());
        final List<String> localVarNames = collector.collectLocalVarsDeclared(targetNode)
            .stream().map(var -> var.getName().asString()).collect(Collectors.toList());
        final List<String> parameterNames = collector.collectParameters(targetNode)
            .stream().map(var -> var.getName().asString()).collect(Collectors.toList());

        final VariableReplacer replacer = new VariableReplacer();
        final List<Node> replacedNodes = replacer.replaceAllVariables(targetNode, fieldNames, localVarNames, parameterNames);
        List<OperationDiff> candidates = replacedNodes.stream()
            .map(node -> new OperationDiff(ModifyType.CHANGE, targetNode, node)).collect(Collectors.toList());

        return candidates;
    }
}