package jp.posl.jprophet.evaluator.extractor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.Node;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.AstDiff;
import jp.posl.jprophet.evaluator.NodeWithDiffType;
import jp.posl.jprophet.evaluator.ProgramChunk;
import jp.posl.jprophet.evaluator.extractor.ModKinds;
import jp.posl.jprophet.evaluator.extractor.ModKinds.ModKind;

public class ModKindExtractorTest {

    /**
     * InsertControlパターンの修正を判定できるかテスト
     */
    @Test public void testModKindForInsertControl() {
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

        final ModKindExtractor extractor = new ModKindExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final Map<ProgramChunk, ModKinds> actualMap = extractor.extract(nodeWithDiffType, chunks);

        // BreakStmtがInsertStmtとして加算されている
        final ModKinds expectedModKind = new ModKinds(Set.of(ModKind.INSERT_CONTROL, ModKind.INSERT_STMT));
        final ProgramChunk expectedChunk = new ProgramChunk(3, 8);

        assertThat(actualMap.get(expectedChunk)).isEqualToComparingFieldByField(expectedModKind);
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

        final ModKindExtractor extractor = new ModKindExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final Map<ProgramChunk, ModKinds> actualMap = extractor.extract(nodeWithDiffType, chunks);

        final ModKinds expectedModKindOfIf = new ModKinds(Set.of(ModKind.INSERT_CONTROL, ModKind.INSERT_GUARD, ModKind.INSERT_STMT));
        final ProgramChunk expectedChunkOfIf = new ProgramChunk(3, 3);
        final ModKinds expectedModKindOfReturn = new ModKinds(Set.of(ModKind.INSERT_STMT));
        final ProgramChunk expectedChunkOfReturn = new ProgramChunk(5, 5);

        assertThat(actualMap.get(expectedChunkOfIf)).isEqualToComparingFieldByField(expectedModKindOfIf);
        assertThat(actualMap.get(expectedChunkOfReturn)).isEqualToComparingFieldByField(expectedModKindOfReturn);
        return;
    }

    /**
     * 制御文(return, break)に対してifガードを挿入したパッチがInsertControlと判定されない
     */
    @Test public void testModKindForInsertGuardWithPreExistingControl() {
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

        final ModKindExtractor extractor = new ModKindExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final Map<ProgramChunk, ModKinds> actualMap = extractor.extract(nodeWithDiffType, chunks);

        final ModKinds expectedModKind = new ModKinds(Set.of(ModKind.INSERT_GUARD));
        final ProgramChunk expectedChunk = new ProgramChunk(3, 3);

        assertThat(actualMap.get(expectedChunk)).isEqualToComparingFieldByField(expectedModKind);
        return;
    }

    /**
     * InsertGuardパターンの修正を判定できるかテスト
     */
    @Test public void testModKindForInsertGuard() {
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

        final ModKindExtractor extractor = new ModKindExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final Map<ProgramChunk, ModKinds> actualMap = extractor.extract(nodeWithDiffType, chunks);

        final ModKinds expectedModKind = new ModKinds(Set.of(ModKind.INSERT_GUARD));
        final ProgramChunk expectedChunk = new ProgramChunk(3, 3);

        assertThat(actualMap.get(expectedChunk)).isEqualToComparingFieldByField(expectedModKind);
        return;
    }

    /**
     * ReplaceCondパターンの修正を判定できるかテスト
     */
    @Test public void testModKindForReplaceCond() {
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

        final ModKindExtractor extractor = new ModKindExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final Map<ProgramChunk, ModKinds> actualMap = extractor.extract(nodeWithDiffType, chunks);

        // 条件式内部が変数のためReplaceVarも判定される
        final ModKinds expectedModKind = new ModKinds(Set.of(ModKind.REPLACE_COND, ModKind.REPLACE_VAR));
        final ProgramChunk expectedChunk = new ProgramChunk(3, 3);

        assertThat(actualMap.get(expectedChunk)).isEqualToComparingFieldByField(expectedModKind);
        return;
    }

    /**
     * ReplaceVarパターンの修正を判定できるかテスト
     */
    @Test public void testModKindForReplaceVar() {
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

        final ModKindExtractor extractor = new ModKindExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final Map<ProgramChunk, ModKinds> actualMap = extractor.extract(nodeWithDiffType, chunks);

        final ModKinds expectedModKind = new ModKinds(Set.of(ModKind.REPLACE_VAR));
        final ProgramChunk expectedChunk = new ProgramChunk(3, 3);

        assertThat(actualMap.get(expectedChunk)).isEqualToComparingFieldByField(expectedModKind);
        return;
    }

    /**
     * メソッドの引数としてのReplaceVarパターンの修正を判定できるかテスト
     */
    @Test public void testModKindForReplaceVarAsParameter() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       hoge(foo);\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       hoge(bar);\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final ModKindExtractor extractor = new ModKindExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final Map<ProgramChunk, ModKinds> actualMap = extractor.extract(nodeWithDiffType, chunks);

        final ModKinds expectedModKind = new ModKinds(Set.of(ModKind.REPLACE_VAR));
        final ProgramChunk expectedChunk = new ProgramChunk(3, 3);

        assertThat(actualMap.get(expectedChunk)).isEqualToComparingFieldByField(expectedModKind);
        return;
    }

    /**
     * ReplaceMethodパターンの修正を判定できるかテスト
     */
    @Test public void testModKindForReplaceMethod() {
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

        final ModKindExtractor extractor = new ModKindExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final Map<ProgramChunk, ModKinds> actualMap = extractor.extract(nodeWithDiffType, chunks);

        final ModKinds expectedModKind = new ModKinds(Set.of(ModKind.REPLACE_METHOD));
        final ProgramChunk expectedChunk = new ProgramChunk(3, 3);

        assertThat(actualMap.get(expectedChunk)).isEqualToComparingFieldByField(expectedModKind);
        return;
    }

    /**
     * InsertStmtパターンの修正を判定できるかテスト
     */
    @Test public void testModKindForInsertStmt() {
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

        final ModKindExtractor extractor = new ModKindExtractor();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalNodes.get(0), revisedNodes.get(0));
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final Map<ProgramChunk, ModKinds> actualMap = extractor.extract(nodeWithDiffType, chunks);

        final ModKinds expectedModKind = new ModKinds(Set.of(ModKind.INSERT_STMT));
        final ProgramChunk expectedChunk = new ProgramChunk(3, 3);

        assertThat(actualMap.get(expectedChunk)).isEqualToComparingFieldByField(expectedModKind);
        return;
    }
}