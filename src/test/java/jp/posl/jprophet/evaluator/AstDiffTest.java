package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.assertThat;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.SimpleName;

import org.junit.Test;

import difflib.Delta;

import java.util.List;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;

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
        final List<Delta<String>> actualAstDelta = astDiff.diff(originalNodes.get(0), revisedNodes.get(0));

        assertThat(actualAstDelta.size()).isEqualTo(1);
        assertThat(actualAstDelta.get(0).getRevised().getLines().size()).isEqualTo(2);
        assertThat(actualAstDelta.get(0).getOriginal().getLines().size()).isEqualTo(2);
        assertThat(actualAstDelta.get(0).getType()).isEqualTo(difflib.Delta.TYPE.CHANGE);
    }

    /**
     * Diffの種類付きのASTの木構造が構成されているかテスト
     */
    @Test public void testCreateRevisedAstWithDiffType() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class B {\n\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final AstDiff astDiff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = astDiff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        assertThat(nodeWithDiffType.getChildNodes().size()).isEqualTo(1);
        assertThat(nodeWithDiffType.getNode()).isInstanceOf(ClassOrInterfaceDeclaration.class);
        assertThat(nodeWithDiffType.getDiffType()).isEqualTo(TYPE.CHANGE);

        assertThat(nodeWithDiffType.getChildNodes().get(0).getChildNodes().size()).isEqualTo(0);
        assertThat(nodeWithDiffType.getChildNodes().get(0).getNode()).isInstanceOf(SimpleName.class);
        assertThat(nodeWithDiffType.getChildNodes().get(0).getDiffType()).isEqualTo(TYPE.CHANGE);
    }
}