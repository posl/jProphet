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
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final Map<ProgramChank, ModFeatureVec> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        // BreakStmtがInsertStmtとして加算されている
        final ModFeatureVec expectedModFeature = new ModFeatureVec(2, 0, 0, 0, 0, 2);
        final ProgramChank expectedChank = new ProgramChank(3, 6);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
    }

    /**
     * 制御文(return, break)に対してifガードを挿入したパッチがInsertControlと判定されない
     */
    @Test public void testModFeatureForInsertGuardWithPreExistingControl() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       return;\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
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
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final Map<ProgramChank, ModFeatureVec> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeatureVec expectedModFeature = new ModFeatureVec(0, 1, 0, 0, 0, 0);
        final ProgramChank expectedChank = new ProgramChank(3, 3);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
        return;
    }

    /**
     * InsertGuardパターンの修正を判定できるかテスト
     */
    @Test public void testModFeatureForInsertGuard() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       hoge();\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       if(fuga)\n")
            .append("           hoge();\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final Map<ProgramChank, ModFeatureVec> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeatureVec expectedModFeature = new ModFeatureVec(0, 1, 0, 0, 0, 0);
        final ProgramChank expectedChank = new ProgramChank(3, 3);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
        return;
    }

    /**
     * ReplaceCondパターンの修正を判定できるかテスト
     */
    @Test public void testModFeatureForReplaceCond() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       if(foo)\n")
            .append("           hoge();\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       if(bar)\n")
            .append("           hoge();\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final Map<ProgramChank, ModFeatureVec> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        // 条件式内部が変数のためReplaceVarも判定される
        final ModFeatureVec expectedModFeature = new ModFeatureVec(0, 0, 1, 1, 0, 0);
        final ProgramChank expectedChank = new ProgramChank(3, 3);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
        return;
    }

    /**
     * ReplaceVarパターンの修正を判定できるかテスト
     */
    @Test public void testModFeatureForReplaceVar() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       hoge = foo;\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       hoge = bar;\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final Map<ProgramChank, ModFeatureVec> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeatureVec expectedModFeature = new ModFeatureVec(0, 0, 0, 1, 0, 0);
        final ProgramChank expectedChank = new ProgramChank(3, 3);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
        return;
    }

    /**
     * ReplaceMethodパターンの修正を判定できるかテスト
     */
    @Test public void testModFeatureForReplaceMethod() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       hoge();\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       fuga();\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final Map<ProgramChank, ModFeatureVec> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeatureVec expectedModFeature = new ModFeatureVec(0, 0, 0, 0, 1, 0);
        final ProgramChank expectedChank = new ProgramChank(3, 3);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
        return;
    }

    /**
     * InsertStmtパターンの修正を判定できるかテスト
     */
    @Test public void testModFeatureForInsertStmt() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       hoge();\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final Map<ProgramChank, ModFeatureVec> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeatureVec expectedModFeature = new ModFeatureVec(0, 0, 0, 0, 0, 1);
        final ProgramChank expectedChank = new ProgramChank(3, 3);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
        return;
    }

}