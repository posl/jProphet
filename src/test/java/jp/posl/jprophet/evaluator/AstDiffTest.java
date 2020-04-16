package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.assertThat;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;

import org.junit.Test;

import difflib.Delta;

import java.util.List;

import jp.posl.jprophet.NodeUtility;

public class AstDiffTest {
    /**
     * クラス名のみ異なるノードリスト間の差分を算出できているかテスト
     */
    @Test public void testAstDiff() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    public void a() {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class B {\n\n")
            .append("    public void a() {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final AstDiff astDiff = new AstDiff();
        final List<Delta<Node>> actualAstDelta = astDiff.diff(originalNodes.get(0), revisedNodes.get(0));

        assertThat(actualAstDelta.size()).isEqualTo(1);
        assertThat(actualAstDelta.get(0).getRevised().getLines().size()).isEqualTo(1);
        assertThat(actualAstDelta.get(0).getOriginal().getLines().size()).isEqualTo(1);
        assertThat(actualAstDelta.get(0).getRevised().getLines().get(0)).isInstanceOf(SimpleName.class);
        assertThat(actualAstDelta.get(0).getOriginal().getLines().get(0)).isInstanceOf(SimpleName.class);
    }

    @Test public void testCreateRevisedAstWithDiffType() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    public void a() {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class B {\n\n")
            .append("    public void a() {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final AstDiff astDiff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = astDiff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        assertThat(nodeWithDiffType).isNotNull();
    }
        
}