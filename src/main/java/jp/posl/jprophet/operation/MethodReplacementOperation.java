package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * {@inheritDoc}
     */
    public List<CompilationUnit> exec(Node targetNode) {
        if (!(targetNode instanceof MethodCallExpr)) return new ArrayList<>(); 
        final MethodCallExpr targetMethodCallExpr = (MethodCallExpr) targetNode;
        final boolean hasScopeOfThis = targetMethodCallExpr.getScope().map(s -> s instanceof ThisExpr).orElse(true);
        if(!hasScopeOfThis) return new ArrayList<>();

        final List<String> methodNameCandidates = this.collectMethodNames(targetNode).stream()
            .filter(name -> !targetMethodCallExpr.getNameAsString().equals(name))
            .collect(Collectors.toList());

        final List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        methodNameCandidates.stream()
            .forEach(name -> {
                Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);
                NodeUtility.replaceNode(new MethodCallExpr(new ThisExpr(), name, targetMethodCallExpr.getArguments()), copiedTargetNode)
                    .ifPresent(n -> n.findCompilationUnit().ifPresent(candidates::add));
            });
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