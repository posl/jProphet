package jp.posl.jprophet.evaluator;

import java.util.List;

import com.github.javaparser.ast.Node;

/**
 * ASTの差分内容を示すクラス 
 */
public class AstDelta {
    final private List<Node> deleteNodes;
    final private List<Node> addNodes;

    public AstDelta(List<Node> deleteNodes, List<Node> addNodes) {
        this.deleteNodes = deleteNodes;
        this.addNodes = addNodes;
    }

    /**
     *  @return 削除されたノード 
     */
    public List<Node> getDeleteNodes() {
        return this.deleteNodes;
    }

    /**
     * @return 追加されたノード
     */
    public List<Node> getAddNodes() {
        return this.addNodes;
    }
}