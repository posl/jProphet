package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class AstDiff {

    /**
     * オリジナルのASTを構成するノードと変更後のASTを構成するノードのリストの間の 差分を計算する difflibを用いて実装
     * 
     * @param original オリジナルのAST
     * @param revised  変更後のAST
     * @return 変更差分
     */
    public List<Delta<Node>> diff(Node original, Node revised) {
        final List<Node> originalNodes = NodeUtility.getAllDescendantNodes(original);
        final List<Node> revisedNodes = NodeUtility.getAllDescendantNodes(revised);

	    final Patch<Node> diff = DiffUtils.diff(originalNodes, revisedNodes);
        final List<Delta<Node>> deltas = diff.getDeltas();

        return deltas;
    }

    public NodeWithDiffType createRevisedAstWithDiffType(Node original, Node revised) {
        List<Delta<Node>> deltas = diff(original, revised);
        return createAstWithDiffType(revised, deltas);
    }

    private NodeWithDiffType createAstWithDiffType(Node targetNode, List<Delta<Node>> astDeltas) {
        TYPE type = TYPE.SAME;
        for(Delta<Node> astDelta: astDeltas) {
            List<Node> diffNodes = new ArrayList<Node>(astDelta.getOriginal().getLines());
            diffNodes.addAll(new ArrayList<Node>(astDelta.getRevised().getLines()));
            for(Node diffNode: diffNodes) {
                if(diffNode.equals(targetNode) && diffNode.getRange().equals(targetNode.getRange())) {
                    type = TYPE.values()[astDelta.getType().ordinal()];
                }
            }
        }
        final List<NodeWithDiffType> childNodesWithDiffTypes = targetNode.getChildNodes().stream()
            .map(childNode -> createAstWithDiffType(childNode, astDeltas))
            .collect(Collectors.toList());
        final NodeWithDiffType nodeWithDiffType = new NodeWithDiffType(targetNode, type);
        nodeWithDiffType.addChildNodes(childNodesWithDiffTypes);
        return nodeWithDiffType;
    }
}
