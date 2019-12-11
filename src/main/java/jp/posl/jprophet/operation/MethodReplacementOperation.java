package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ThisExpr;


import jp.posl.jprophet.NodeUtility;

/**
 * 対象ステートメント中の変数を別のもので置き換える操作を行う
 */
public class MethodReplacementOperation implements AstOperation {
    /**
     * <h4>メソッドの置換操作及び置換後に引数の置換を行い修正パッチ候補を生成する</h4>
     * <p>MethodCallExprをtargetNodeとして受け取った場合に置換が行われる</p>
     * @return 生成された修正後のCompilationUnitのリスト
     */
    public List<CompilationUnit> exec(Node targetNode) {
        if (!(targetNode instanceof MethodCallExpr)) return new ArrayList<>(); 
        final MethodCallExpr targetMethodCallExpr = (MethodCallExpr) targetNode;
        final boolean hasScopeOfThis = targetMethodCallExpr.getScope().map(s -> s instanceof ThisExpr).orElse(true);
        if(!hasScopeOfThis) return new ArrayList<>();

        final List<String> methodNameCandidates = this.collectMethodNames(targetNode).stream()
            .filter(name -> !targetMethodCallExpr.getNameAsString().equals(name))
            .collect(Collectors.toList());

        final List<MethodCallExpr> methodCallExprWithReplacedMethodName = new ArrayList<MethodCallExpr>();
        methodNameCandidates.stream()
            .map(name -> new MethodCallExpr(new ThisExpr(), name, targetMethodCallExpr.getArguments()))
            .forEach(methodCallExpr -> {
                final Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);
                NodeUtility.replaceNode(methodCallExpr, copiedTargetNode)
                    .map(node -> (MethodCallExpr)node)
                    .ifPresent(methodCallExprWithReplacedMethodName::add);
            });

        final List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        methodCallExprWithReplacedMethodName.stream()
            .forEach(methodCallExpr -> methodCallExpr.findCompilationUnit()
            .ifPresent(candidates::add));
        methodCallExprWithReplacedMethodName.stream()
            .flatMap(methodCallExpr -> new VariableReplacementOperation().exec(methodCallExpr).stream())
            .forEach(candidates::add);

        return candidates;
    }

    /**
     * ノードが存在するクラス内のメソッドの名前を集める
     * @param node 対象ノード
     * @return メソッド名のリスト
     */
    private List<String> collectMethodNames(Node node) {
        return node.findRootNode().findAll(MethodDeclaration.class).stream()
            .map(m -> m.getNameAsString())
            .collect(Collectors.toList());
    }
}