package jp.posl.jprophet.evaluator;

import java.util.List;

import com.github.javaparser.ast.Node;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import jp.posl.jprophet.NodeUtility;

public class PatchFeatureTest {
    @Test public void testModFeatureForInsertStmt() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       hoge();\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final PatchFeature patchFeature = new PatchFeature();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extractModFeature(nodeWithDiffType);
        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 0, 0, 0, 1);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }
    @Test public void testModFeatureForInsertGuard() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       hoge();\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       if(fuga)\n\n")
            .append("           hoge();\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final PatchFeature patchFeature = new PatchFeature();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extractModFeature(nodeWithDiffType);
        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 1, 0, 0, 0);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }

    @Test public void testModFeatureForInsertControl() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       if(fuga) {\n\n")
            .append("           break;\n\n")
            .append("       }\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final PatchFeature patchFeature = new PatchFeature();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extractModFeature(nodeWithDiffType);
        final ModFeatureVec expectModFeature = new ModFeatureVec(1, 0, 0, 0, 2);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }

    @Test public void testModFeatureForReplaceCond() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       if(foo)\n\n")
            .append("           hoge();\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       if(bar)\n\n")
            .append("           hoge();\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final PatchFeature patchFeature = new PatchFeature();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extractModFeature(nodeWithDiffType);
        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 0, 1, 0, 0);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }

    @Test public void testModFeatureForReplaceStmt() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       hoge();\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       fuga();\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final PatchFeature patchFeature = new PatchFeature();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extractModFeature(nodeWithDiffType);
        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 0, 0, 1, 0);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }
}