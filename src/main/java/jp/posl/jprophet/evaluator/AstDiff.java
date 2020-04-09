package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class AstDiff {
    /**
     * オリジナルのASTを構成するノードと変更後のASTを構成するノードのリストの間の
     * 差分を計算する
     * difflibを用いて実装
     * @param original オリジナルのAST
     * @param revised 変更後のAST
     * @return 変更差分
     */
    public List<AstDelta> diff(Node original, Node revised) {
        final List<Node> originalNodes = NodeUtility.getAllDescendantNodes(original);
        final List<Node> revisedNodes = NodeUtility.getAllDescendantNodes(revised);

	    final Patch<Node> diff = DiffUtils.diff(originalNodes, revisedNodes);
        final List<Delta<Node>> deltas = diff.getDeltas();

        final List<AstDelta> astDeltas = deltas.stream().map((delta) -> {
            final Chunk<Node> originalNode = delta.getOriginal();
            final Chunk<Node> fixedNode = delta.getRevised();
            return new AstDelta(originalNode.getLines(), fixedNode.getLines());
        }).collect(Collectors.toList());

        return astDeltas;
    }
}
