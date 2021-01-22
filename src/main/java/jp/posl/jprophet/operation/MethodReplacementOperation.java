package jp.posl.jprophet.operation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;

import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;


/**
 * 対象ステートメント中の関数を別のもので置き換える操作を行う
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

        if (!this.findMethodType(targetMethodCallExpr).isPresent()) {
            return Collections.emptyList();
        }
        List<MethodDeclaration> methodCandidates;
        final String targetMethodType = this.findMethodType(targetMethodCallExpr).orElseThrow();
        if (targetMethodType.equals("void")) {
            methodCandidates = this.collectMethods(targetNode).stream()
                .filter(method -> !targetMethodCallExpr.getNameAsString().equals(method.getNameAsString()))
                .collect(Collectors.toList());
        }
        else {
            methodCandidates = this.collectMethodsWithTypeFilter(targetNode, targetMethodType).stream()
                .filter(method -> !targetMethodCallExpr.getNameAsString().equals(method.getNameAsString()))
                .collect(Collectors.toList());
        }

        final List<MethodCallExpr> replacedMethods = this.replaceMethodName(targetMethodCallExpr, methodCandidates);

        final List<Node> replacedArgmentsMethods = replaceArgments(replacedMethods, targetNode);

        final List<OperationDiff> operationDiffs = replacedArgmentsMethods.stream()
            .map(node -> new OperationDiff(ModifyType.CHANGE, targetNode, node))
            .collect(Collectors.toList());

        return operationDiffs;
    }

    private Optional<String> findMethodType(MethodCallExpr methodCallExpr) {
        return methodCallExpr.findRootNode().findAll(MethodDeclaration.class).stream()
            .filter(m -> m.getNameAsString().equals(methodCallExpr.getNameAsString()))
            .map(m -> m.getTypeAsString())
            .findAny();
    }

    /**
     * ノードが存在するクラス内のメソッドの名前を集める
     * 
     * @param node 対象ノード
     * @return メソッド名のリスト
     */
    private List<MethodDeclaration> collectMethods(Node node) {
        return node.findRootNode().findAll(MethodDeclaration.class);
    }

    /**
     * ノードが存在するクラス内のメソッドの内，与えられた型と一致する型の返り値を持つメソッドを集める
     * 
     * @param node 対象ノード
     * @param type 収集するメソッドの型
     * @return メソッド名のリスト
     */
    private List<MethodDeclaration> collectMethodsWithTypeFilter(Node node, String type) {
        return node.findRootNode().findAll(MethodDeclaration.class).stream()
            .filter(m -> m.getType().asString().equals(type))
            .collect(Collectors.toList());
    }

    /**
     * メソッド名を置換する
     * 
     * @param targetExpr  置換対象のMethodCallExpr
     * @param methodNames 新しいメソッド名のリスト
     * @return 置換後のMethodCallExprのリスト
     */
    private List<MethodCallExpr> replaceMethodName(MethodCallExpr targetExpr, List<MethodDeclaration> methods) {
        final DeclarationCollector collector = new DeclarationCollector();
        final Map<String, String> fieldNameToType = collector.collectFileds(targetExpr).stream()
            .collect(Collectors.toMap(
                var -> var.getName().toString(),
                var -> var.getTypeAsString()
            ));
        final Map<String, String> localVarNameToType = collector.collectLocalVarsDeclared(targetExpr).stream()
            .collect(Collectors.toMap(
                var -> var.getName().toString(),
                var -> var.getTypeAsString()
            ));
        final Map<String, String> parameterNameToType = collector.collectParameters(targetExpr).stream()
            .collect(Collectors.toMap(
                var -> var.getName().toString(),
                var -> var.getTypeAsString()
            ));
        final Map<String, String> allVarNameToType = new HashMap<String, String>();
        allVarNameToType.putAll(fieldNameToType);
        allVarNameToType.putAll(localVarNameToType);
        allVarNameToType.putAll(parameterNameToType);
        final List<MethodCallExpr> newMethods = new ArrayList<MethodCallExpr>();
        for (MethodDeclaration method: methods) {
            final List<String> parameterTypes = method.getParameters().stream()
                .map(p -> p.getTypeAsString())
                .collect(Collectors.toList());
            final NodeList<Expression> typeMatchedVars = new NodeList<Expression>();
            for (String parameterType: parameterTypes) {
                allVarNameToType.entrySet().stream()
                    .filter(e -> e.getValue().equals(parameterType))
                    .map(e -> e.getKey())
                    .findFirst()
                    .map(varName -> new NameExpr(varName))
                    .ifPresent(varExpr -> typeMatchedVars.add(varExpr));
            }
            if (typeMatchedVars.size() != parameterTypes.size()) {
                break;
            }
            newMethods.add(new MethodCallExpr(new ThisExpr(), method.getNameAsString(), typeMatchedVars));
        }
        return newMethods;
    }


    private List<Node> replaceArgments(List<MethodCallExpr> targetMethods, Node scope) {
        final DeclarationCollector collector = new DeclarationCollector();
        final Map<String, String> fieldNameToType = collector.collectFileds(scope).stream()
            .collect(Collectors.toMap(
                var -> var.getName().toString(),
                var -> var.getTypeAsString()
            ));
        final Map<String, String> localVarNameToType = collector.collectLocalVarsDeclared(scope).stream()
            .collect(Collectors.toMap(
                var -> var.getName().toString(),
                var -> var.getTypeAsString()
            ));
        final Map<String, String> parameterNameToType = collector.collectParameters(scope).stream()
            .collect(Collectors.toMap(
                var -> var.getName().toString(),
                var -> var.getTypeAsString()
            ));

        final VariableReplacer replacer = new VariableReplacer();

        final List<Node> replacedArgmentsMethods = new ArrayList<Node>();
        replacedArgmentsMethods.addAll(targetMethods);
        targetMethods.stream()
            .map(node -> replacer.replaceAllVariables(node, fieldNameToType, localVarNameToType, parameterNameToType))
            .forEach(replacedNodes -> replacedArgmentsMethods.addAll(replacedNodes));

        return replacedArgmentsMethods;
    }

}