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
        List<Node> originalNodes = NodeUtility.getAllDescendantNodes(original);
        List<Node> revisedNodes = NodeUtility.getAllDescendantNodes(revised);

	    Patch<Node> diff = DiffUtils.diff(originalNodes, revisedNodes);
        List<Delta<Node>> deltas = diff.getDeltas();

        List<AstDelta> astDeltas = deltas.stream().map((delta) -> {
            Chunk<Node> originalNode = delta.getOriginal();
            Chunk<Node> fixedNode = delta.getRevised();
            return new AstDelta(originalNode.getLines(), fixedNode.getLines());
        }).collect(Collectors.toList());

        return astDeltas;
    }

    /**
     * ASTの差分内容を示すクラス 
     */
    public static class AstDelta {
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
}
