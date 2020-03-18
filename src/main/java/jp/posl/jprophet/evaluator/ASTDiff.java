package jp.posl.jprophet.evaluator;

import java.util.List;

import com.github.javaparser.ast.Node;

public class ASTDiff {
    public ASTDelta diff(Node original, Node revised) {
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
