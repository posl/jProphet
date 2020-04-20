package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;

public class NodeWithDiffType {
    final private Node node;
    final private TYPE diffType;
    final private List<NodeWithDiffType> childNodes = new ArrayList<NodeWithDiffType>();
    
    /**
     * Specifies the type of the delta.
     *
     */
    public enum TYPE {
    	/** A change in the original. */
        CHANGE, 
        /** A delete from the original. */
        DELETE, 
        /** An insert into the original. */
        INSERT,
    	/** A same as the original. */
        SAME
    }

    public NodeWithDiffType(Node node, TYPE diffType) {
        this.node = node;
        this.diffType = diffType;
    }

    public Node asNode() {
        return this.node;
    }

    public List<NodeWithDiffType> getChildNodes() {
        return this.childNodes;
    }

    public void addChildNodes(List<NodeWithDiffType> childNodes) {
        this.childNodes.addAll(childNodes);
    }

    public TYPE getDiffType() {
        return this.diffType;
    }
}