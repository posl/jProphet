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
            .append("       if(fuga)\n")
            .append("           continue;\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final Map<ProgramChank, ModFeature> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        // BreakStmtがInsertStmtとして加算されている
        final ModFeature expectedModFeature = new ModFeature(3, 0, 0, 0, 0, 3);
        final ProgramChank expectedChank = new ProgramChank(3, 8);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
    }

    /**
     * InsertControlとして判定されたIfStmtとReturn文の間にSAMEノードがあり，チャンクが分かれている場合，
     * InsertControlはIfStmtがあるチャンクでのみ判定される
     */
    @Test public void testForInsertControlWithSameNodeAsOriginal() {
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
            .append("       if(fuga) {\n")
            .append("           hoge();\n")
            .append("           return;\n")
            .append("       }\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModFeatureExtractor patchFeature = new ModFeatureExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final Map<ProgramChank, ModFeature> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeature expectedModFeatureOfIf = new ModFeature(1, 1, 0, 0, 0, 1);
        final ProgramChank expectedChankOfIf = new ProgramChank(3, 3);
        final ModFeature expectedModFeatureOfReturn = new ModFeature(0, 0, 0, 0, 0, 1);
        final ProgramChank expectedChankOfReturn = new ProgramChank(5, 5);

        assertThat(actualMap.get(expectedChankOfIf)).isEqualToComparingFieldByField(expectedModFeatureOfIf);
        assertThat(actualMap.get(expectedChankOfReturn)).isEqualToComparingFieldByField(expectedModFeatureOfReturn);
        return;
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

        final Map<ProgramChank, ModFeature> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeature expectedModFeature = new ModFeature(0, 1, 0, 0, 0, 0);
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

        final Map<ProgramChank, ModFeature> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeature expectedModFeature = new ModFeature(0, 1, 0, 0, 0, 0);
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

        final Map<ProgramChank, ModFeature> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        // 条件式内部が変数のためReplaceVarも判定される
        final ModFeature expectedModFeature = new ModFeature(0, 0, 1, 1, 0, 0);
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

        final Map<ProgramChank, ModFeature> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeature expectedModFeature = new ModFeature(0, 0, 0, 1, 0, 0);
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

        final Map<ProgramChank, ModFeature> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeature expectedModFeature = new ModFeature(0, 0, 0, 0, 1, 0);
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

        final Map<ProgramChank, ModFeature> actualMap = patchFeature.extract(nodeWithDiffType, chanks);

        final ModFeature expectedModFeature = new ModFeature(0, 0, 0, 0, 0, 1);
        final ProgramChank expectedChank = new ProgramChank(3, 3);

        assertThat(actualMap.get(expectedChank)).isEqualToComparingFieldByField(expectedModFeature);
        return;
    }
}