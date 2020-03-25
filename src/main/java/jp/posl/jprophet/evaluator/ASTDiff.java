package jp.posl.jprophet.evaluator;

import java.util.List;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.Delta.TYPE;

public class ASTDiff {
    public ASTDelta diff(Node original, Node revised) {
        List<Node> originalNodes = NodeUtility.getAllDescendantNodes(original);
        List<Node> revisedNodes = NodeUtility.getAllDescendantNodes(revised);

	    Patch<Node> diff = DiffUtils.diff(originalNodes, revisedNodes);
        List<Delta<Node>> deltas = diff.getDeltas();
	    for (Delta<Node> delta : deltas) {
            TYPE type = delta.getType();
            System.out.println(type);
            Chunk<Node> originalNode = delta.getOriginal();
            Chunk<Node> fixedNode = delta.getRevised();
            System.out.printf("del: position=%d, lines=%s%n", originalNode.getPosition(), originalNode.getLines());
            System.out.printf("add: position=%d, lines=%s%n", fixedNode.getPosition(), fixedNode.getLines());
        }
        return null;
    }

    public static class ASTDelta {
        final private List<Node> removedNodes;
        final private List<Node> addedNodes;

        public ASTDelta(List<Node> removedNodes, List<Node> addedNodes) {
            this.removedNodes = removedNodes;
            this.addedNodes = addedNodes;
        }

        public List<Node> getRemovedNodes() {
            return this.removedNodes;
        }

        public List<Node> getAddedNodes() {
            return this.addedNodes;
        }
    }
}
