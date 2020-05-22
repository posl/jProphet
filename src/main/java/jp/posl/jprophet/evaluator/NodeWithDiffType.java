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

    /**
     * ASTを走査し全ての{@code diffType}のnodeを返す
     * @param diffType 探したいdiffの種類
     * @return diffTypeが一致するノードのリスト
     */
    public List<NodeWithDiffType> findAll(TYPE diffType) {
        final List<NodeWithDiffType> nodes = new ArrayList<NodeWithDiffType>();
        if(this.diffType == diffType) {
            nodes.add(this);
        }
        for(NodeWithDiffType child: this.childNodes) {
            nodes.addAll(child.findAll(diffType));
        }
        return nodes;
    }

    /**
     * ASTを走査し全ての{@code nodeType}のnodeを返す
     * @param nodeType 探したいnodeのクラス
     * @return nodeTypeが一致するノードのリスト
     */
    public <T extends Node> List<NodeWithDiffType> findAll(Class<T> nodeType) {
        final List<NodeWithDiffType> nodes = new ArrayList<NodeWithDiffType>();
        if(this.node.getClass() == nodeType) {
            nodes.add(this);
        }
        for(NodeWithDiffType child: this.childNodes) {
            nodes.addAll(child.findAll(nodeType));
        }
        return nodes;
    }

    /**
     * 差分情報を元に連続する変更されたコードの位置を特定する 
     * @return プログラムチャンクのリスト
     */
    public List<ProgramChank> identifyModifiedProgramChanks() {
        final List<NodeWithDiffType> nodesWithDiffType = this.getAllNodeInSourceCodeOrder();
        final List<ProgramChank> chanks = new ArrayList<ProgramChank>();
        int beginLine = 0;
        int previousLine = 0;
        boolean counting = false;
        for(NodeWithDiffType node: nodesWithDiffType) {
            final int line = node.getNode().getRange().get().begin.line;
            if(!counting && node.getDiffType() != TYPE.SAME) {
                beginLine = line;
                counting = true;
            }
            if(counting && node.getDiffType() == TYPE.SAME && line != previousLine) {;
                chanks.add(new ProgramChank(beginLine, previousLine));
                counting = false;
            }
            previousLine = line;
        }
        if(counting) {
            chanks.add(new ProgramChank(beginLine, previousLine));
        }
        return chanks;
    }

    /**
     * 木構造に含まれる全てのNodeWithDiffTypeを，元のソースコードに登場する順番で取得する
     * @return NodeWithDiffTypeのリスト
     */
    private List<NodeWithDiffType> getAllNodeInSourceCodeOrder() {
        final List<NodeWithDiffType> descendantNodes = new ArrayList<NodeWithDiffType>();
        descendantNodes.add(this);
        this.getChildNodes().stream()
            .map(childNode -> childNode.getAllNodeInSourceCodeOrder())
            .forEach(descendantNodes::addAll);
        return descendantNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.node.toString();
    }
}