package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;

/**
 * 差分の種類をNode型に付与したクラス．
 */
public class NodeWithDiffType {
    final private Node node;
    final private TYPE diffType;
    final private List<NodeWithDiffType> childNodes = new ArrayList<NodeWithDiffType>();
    
    /**
     * Specifies the type of the delta.
     * DiffUtilsのTYPEを流用．SAMEを追加
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

    /**
     * @param node ノード
     * @param diffType 差分の種類
     */
    public NodeWithDiffType(Node node, TYPE diffType) {
        this.node = node;
        this.diffType = diffType;
    }

    /**
     * @return {@code Node}型のノード
     */
    public Node getNode() {
        return this.node;
    }

    /**
     * @return 子ノードのリスト
     */
    public List<NodeWithDiffType> getChildNodes() {
        return this.childNodes;
    }

    /**
     * 子ノードを追加する
     * @param childNodes 追加する子ノードのリスト
     */
    public void addChildNodes(List<NodeWithDiffType> childNodes) {
        this.childNodes.addAll(childNodes);
    }

    /**
     * @return 差分の種類
     */
    public TYPE getDiffType() {
        return this.diffType;
    }
}