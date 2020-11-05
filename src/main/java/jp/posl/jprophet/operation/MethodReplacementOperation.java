package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ThisExpr;

import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;


/**
 * 対象ステートメント中の変数を別のもので置き換える操作を行う
 */
public class MethodReplacementOperation implements AstOperation {
    /**
     * <h4>メソッドの置換操作及び置換後に引数の置換を行い修正パッチ候補を生成する</h4>
     * <p>
     * MethodCallExprをtargetNodeとして受け取った場合に置換が行われる
     * </p>
     * 
     * @return 生成された修正後のCompilationUnitのリスト
     */
    public List<OperationDiff> exec(Node targetNode) {
        if (!(targetNode instanceof MethodCallExpr))
            return new ArrayList<>();
        final MethodCallExpr targetMethodCallExpr = (MethodCallExpr) targetNode;
        final boolean hasScopeOfThis = targetMethodCallExpr.getScope().map(s -> s instanceof ThisExpr).orElse(true);
        if (!hasScopeOfThis)
            return new ArrayList<>();

        final List<String> methodNameCandidates = this.collectMethodNames(targetNode).stream()
                .filter(name -> !targetMethodCallExpr.getNameAsString().equals(name)).collect(Collectors.toList());


        final List<Node> replacedMethods =
            this.replaceMethodName(targetMethodCallExpr, methodNameCandidates);

        final List<Node> replacedArgmentsMethods = replaceArgments(replacedMethods, targetNode);

        final List<OperationDiff> operationDiffs = replacedArgmentsMethods.stream()
            .map(node -> new OperationDiff(ModifyType.CHANGE, targetNode, node))
            .collect(Collectors.toList());
            
        return operationDiffs;
    }

    /**
     * ノードが存在するクラス内のメソッドの名前を集める
     * 
     * @param node 対象ノード
     * @return メソッド名のリスト
     */
    private List<String> collectMethodNames(Node node) {
        return node.findRootNode().findAll(MethodDeclaration.class).stream().map(m -> m.getNameAsString())
                .collect(Collectors.toList());
    }

    /**
     * メソッド名を置換する
     * 
     * @param targetExpr  置換対象のMethodCallExpr
     * @param methodNames 新しいメソッド名のリスト
     * @return 置換後のMethodCallExprのリスト
     */
    private List<Node> replaceMethodName(MethodCallExpr targetExpr, List<String> methodNames) {
        List<Node> replacedMethods = methodNames.stream()
                .map(name -> new MethodCallExpr(new ThisExpr(), name, targetExpr.getArguments()))
                .collect(Collectors.toList());
        return replacedMethods;
    }

    private List<Node> replaceArgments(List<Node> targetMethods, Node scope) {
        final DeclarationCollector collector = new DeclarationCollector();
        final List<String> fieldNames = collector.collectFileds(scope)
            .stream().map(var -> var.getName().asString()).collect(Collectors.toList());
        final List<String> localVarNames = collector.collectLocalVarsDeclared(scope)
            .stream().map(var -> var.getName().asString()).collect(Collectors.toList());
        final List<String> parameterNames = collector.collectParameters(scope)
            .stream().map(var -> var.getName().asString()).collect(Collectors.toList());

        VariableReplacer replacer = new VariableReplacer();

        final List<Node> replacedArgmentsMethods = new ArrayList<Node>();
        replacedArgmentsMethods.addAll(targetMethods);
        targetMethods.stream()
            .map(node -> replacer.replaceAllVariables(node, fieldNames, localVarNames, parameterNames))
            .forEach(replacedNodes -> replacedArgmentsMethods.addAll(replacedNodes));
            return replacedArgmentsMethods;
    }

}