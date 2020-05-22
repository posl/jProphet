package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Node;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import jp.posl.jprophet.NodeUtility;

public class ModFeatureExtractorTest {

    /**
     * InsertControlパターンの修正を判定できるかテスト
     */
    @Test public void testModFeatureForInsertControl() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       if(fuga)\n")
            .append("           break;\n")
            .append("       if(fuga)\n")
            .append("           return;\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChank> chanks = patchFeature.identifyModifiedProgramChank(nodeWithDiffType);

        // BreakStmtがInsertStmtとして加算されている
        final ModFeatureVec expectedModFeature = new ModFeatureVec(2, 0, 0, 0, 0, 2);
        final ProgramChank expectedChank = new ProgramChank(3, 6);

        final Map<ProgramChank, ModFeatureVec> actualMap = patchFeature.extract2(nodeWithDiffType, chanks);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
    }

    /**
     * 制御文(return, break)に対してifガードを挿入したパッチがInsertControlと判定されない
     */
    @Test public void testModFeatureForInsertGuardWithPreExistingControl() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       return;\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       if(fuga)\n\n")
            .append("           return;\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extract(nodeWithDiffType);

        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 1, 0, 0, 0, 0);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }

    /**
     * InsertGuardパターンの修正を判定できるかテスト
     */
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

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extract(nodeWithDiffType);
        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 1, 0, 0, 0, 0);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }

    /**
     * ReplaceCondパターンの修正を判定できるかテスト
     */
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

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extract(nodeWithDiffType);
        // 条件式内部が変数のためReplaceVarも判定される
        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 0, 1, 1, 0, 0);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }

    /**
     * ReplaceVarパターンの修正を判定できるかテスト
     */
    @Test public void testModFeatureForReplaceVar() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       hoge = foo;\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       hoge = bar;\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extract(nodeWithDiffType);
        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 0, 0, 1, 0, 0);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }

    /**
     * ReplaceMethodパターンの修正を判定できるかテスト
     */
    @Test public void testModFeatureForReplaceMethod() {
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

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extract(nodeWithDiffType);
        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 0, 0, 0, 1, 0);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }

    /**
     * InsertStmtパターンの修正を判定できるかテスト
     */
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

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));

        final ModFeatureVec actualFeatureVec = patchFeature.extract(nodeWithDiffType);
        final ModFeatureVec expectModFeature = new ModFeatureVec(0, 0, 0, 0, 0, 1);

        assertThat(actualFeatureVec).isEqualToComparingFieldByField(expectModFeature);
        return;
    }

}