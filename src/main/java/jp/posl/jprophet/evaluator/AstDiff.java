package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class AstDiff {
    private int countAstPreOrderIndex = 0;

    /**
     * <p>
     * 変更前のASTを構成するノードと変更後のASTを構成するノードのリストの間の差分を計算する．
     * difflibを用いて実装．
     * </p>
     * 差分はノードの{@code toString()}の1行目を元に算出（ソースコード上の1行にあたる）．
     * @param original 変更前のAST
     * @param revised  変更後のAST
     * @return 変更差分
     */
    public List<Delta<String>> diff(Node original, Node revised) {
        final List<Node> originalNodes = NodeUtility.getAllNodesInDepthFirstOrder(original);
        final List<Node> revisedNodes = NodeUtility.getAllNodesInDepthFirstOrder(revised);

        Function<Node, String> takeFirstLine = n -> {
            final String[] lines = n.toString().split("\n");
            if (lines.length > 0) {
                return lines[0];
            }
            else {
                return "";
            }
        };
        final List<String> originalLines = originalNodes.stream()
            .map(takeFirstLine)
            .collect(Collectors.toList());
        final List<String> revisedLines = revisedNodes.stream()
            .map(takeFirstLine)
            .collect(Collectors.toList());

        final Patch<String> diffString = DiffUtils.diff(originalLines, revisedLines);
        final List<Delta<String>> deltas = diffString.getDeltas();

        return deltas;
    }

    /**
     * <p>
     * 変更前のASTと変更後のASTの差分を算出し，差分情報付きの変更後のASTを生成 
     * 変更後のASTに含まれるノードの情報しか保持しないため，削除されたノードの情報などは持たない <br>
     * </p>
     * 将来的にメソッドを追加する
     * @param originala 変更前のAST
     * @param revised 変更後のAST
     * @return 差分情報付きの変更後のAST
     */
    public NodeWithDiffType createRevisedAstWithDiffType(Node original, Node revised) {
        final List<Delta<String>> deltas = diff(original, revised);
        this.countAstPreOrderIndex = 0;
        final NodeWithDiffType revisedAstWithDiffType = createRevisedAstWithDiffType(revised, deltas);
        this.countAstPreOrderIndex = 0;
        return revisedAstWithDiffType;
    }

    /**
     * 差分情報のリストを元に差分情報付きのASTの木構造を構成する
     * @param targetNode 差分情報を付与したいAST
     * @param astDeltas 差分情報のリスト
     * @return 差分情報付きリスト
     */
    private NodeWithDiffType createRevisedAstWithDiffType(Node targetNode, List<Delta<String>> astDeltas) {
        TYPE type = TYPE.SAME;
        for(Delta<String> astDelta: astDeltas) {
            final int pos = astDelta.getRevised().getPosition();
            if(pos <= this.countAstPreOrderIndex &&
               this.countAstPreOrderIndex <= pos + astDelta.getRevised().size() - 1) {
                // Delta.TYPEからNodeWithDiffTypeへ変換
                type = TYPE.values()[astDelta.getType().ordinal()];
            }
        }
        this.countAstPreOrderIndex++;
        final NodeWithDiffType nodeWithDiffType = new NodeWithDiffType(targetNode, type);
        final List<NodeWithDiffType> childNodesWithDiffTypes = targetNode.getChildNodes().stream()
            .map(childNode -> createRevisedAstWithDiffType(childNode, astDeltas))
            .collect(Collectors.toList());
        nodeWithDiffType.addChildNodes(childNodesWithDiffTypes);
        return nodeWithDiffType;
    }
}
