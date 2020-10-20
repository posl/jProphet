package jp.posl.jprophet.evaluator.extractor;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.extractor.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.extractor.StatementKindExtractor.StatementKind;
import jp.posl.jprophet.evaluator.extractor.ModKinds.ModKind;
import jp.posl.jprophet.evaluator.extractor.VariableCharacteristics.VarChar;
import jp.posl.jprophet.operation.MethodReplacementOperation;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.LearningPatch;
import jp.posl.jprophet.patch.Patch;

public class FeatureExtractorTest {

    /**
     * パッチが一つのチャックの場合
     */
    @Test public void testOneChunkPatch() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       int hoge;\n")
            .append("       hoge = 0;\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       int hoge;\n")
            .append("       hoge = 0;\n")
            .append("       print(hoge);\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        //final Patch patchCandidate = new PatchCandidate(originalNodes.get(0), revisedNodes.get(0).findCompilationUnit().get(), "", "", MethodReplacementOperation.class, 0);
        final Patch patchCandidate = new LearningPatch(originalSource, revisedSource);
        final FeatureExtractor extractor = new FeatureExtractor();
        final FeatureVector featureVector = extractor.extract(patchCandidate);
        final List<Boolean> binaryVector = featureVector.asBooleanList();
        final FeatureVector expectedVector = new FeatureVector();
        final ModKind modType = ModKind.INSERT_STMT;
        expectedVector.add(modType);

        expectedVector.add(StatementPos.PREV, StatementKind.ASSIGN, modType);
        expectedVector.add(StatementPos.TARGET, StatementKind.METHOD_CALL, modType);

        expectedVector.add(StatementPos.PREV, VarChar.LOCAL, VarChar.LOCAL);
        expectedVector.add(StatementPos.PREV, VarChar.LOCAL, VarChar.NUM);
        expectedVector.add(StatementPos.PREV, VarChar.LOCAL, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarChar.NUM, VarChar.NUM);
        expectedVector.add(StatementPos.PREV, VarChar.NUM, VarChar.LOCAL);
        expectedVector.add(StatementPos.PREV, VarChar.NUM, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarChar.IN_ASSIGN_STMT, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarChar.IN_ASSIGN_STMT, VarChar.LOCAL);
        expectedVector.add(StatementPos.PREV, VarChar.IN_ASSIGN_STMT, VarChar.NUM);

        expectedVector.add(StatementPos.TARGET, VarChar.LOCAL, VarChar.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarChar.LOCAL, VarChar.NUM);
        expectedVector.add(StatementPos.TARGET, VarChar.LOCAL, VarChar.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarChar.NUM, VarChar.NUM);
        expectedVector.add(StatementPos.TARGET, VarChar.NUM, VarChar.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarChar.NUM, VarChar.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarChar.IN_ASSIGN_STMT, VarChar.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarChar.IN_ASSIGN_STMT, VarChar.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarChar.IN_ASSIGN_STMT, VarChar.NUM);
        assertThat(expectedVector.asBooleanList()).containsExactlyElementsOf(binaryVector);
    }

    /**
     * パッチが複数のチャンクの場合
     */
    @Test public void testMultiChunkPatch() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       int hoge;\n")
            .append("       int fuga;\n")
            .append("       hoge = 0;\n")
            .append("       print(fuga);\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       int hoge;\n")
            .append("       int fuga;\n")
            .append("       hoge = 1;\n")
            .append("       hoge = 0;\n")
            .append("       print(hoge);\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        //final Patch patchCandidate = new PatchCandidate(originalNodes.get(0), revisedNodes.get(0).findCompilationUnit().get(), "", "", MethodReplacementOperation.class, 0);
        final Patch patchCandidate = new LearningPatch(originalSource, revisedSource);
        final FeatureExtractor extractor = new FeatureExtractor();
        final FeatureVector featureVector = extractor.extract(patchCandidate);
        final List<Boolean> binaryVector = featureVector.asBooleanList();
        final FeatureVector expectedVector = new FeatureVector();

        final ModKind modType1 = ModKind.INSERT_STMT;
        expectedVector.add(modType1);
        expectedVector.add(StatementPos.TARGET, StatementKind.ASSIGN, modType1);
        expectedVector.add(StatementPos.NEXT, StatementKind.ASSIGN, modType1);
        expectedVector.add(StatementPos.NEXT, StatementKind.METHOD_CALL, modType1);

        expectedVector.add(StatementPos.TARGET, VarChar.LOCAL, VarChar.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarChar.LOCAL, VarChar.NUM);
        expectedVector.add(StatementPos.TARGET, VarChar.LOCAL, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.TARGET, VarChar.NUM, VarChar.NUM);
        expectedVector.add(StatementPos.TARGET, VarChar.NUM, VarChar.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarChar.NUM, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.TARGET, VarChar.IN_ASSIGN_STMT, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.TARGET, VarChar.IN_ASSIGN_STMT, VarChar.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarChar.IN_ASSIGN_STMT, VarChar.NUM);

        expectedVector.add(StatementPos.NEXT, VarChar.LOCAL, VarChar.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarChar.LOCAL, VarChar.NUM);
        expectedVector.add(StatementPos.NEXT, VarChar.LOCAL, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.NEXT, VarChar.NUM, VarChar.NUM);
        expectedVector.add(StatementPos.NEXT, VarChar.NUM, VarChar.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarChar.NUM, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.NEXT, VarChar.IN_ASSIGN_STMT, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.NEXT, VarChar.IN_ASSIGN_STMT, VarChar.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarChar.IN_ASSIGN_STMT, VarChar.NUM);

        expectedVector.add(StatementPos.NEXT, VarChar.LOCAL, VarChar.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarChar.LOCAL, VarChar.NUM);
        expectedVector.add(StatementPos.NEXT, VarChar.LOCAL, VarChar.PARAMETER);
        expectedVector.add(StatementPos.NEXT, VarChar.NUM, VarChar.NUM);
        expectedVector.add(StatementPos.NEXT, VarChar.NUM, VarChar.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarChar.NUM, VarChar.PARAMETER);
        expectedVector.add(StatementPos.NEXT, VarChar.IN_ASSIGN_STMT, VarChar.PARAMETER);
        expectedVector.add(StatementPos.NEXT, VarChar.IN_ASSIGN_STMT, VarChar.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarChar.IN_ASSIGN_STMT, VarChar.NUM);


        final ModKind modType2 = ModKind.REPLACE_VAR;
        expectedVector.add(modType2);
        expectedVector.add(StatementPos.PREV, StatementKind.ASSIGN, modType2);
        expectedVector.add(StatementPos.PREV, StatementKind.ASSIGN, modType2);
        expectedVector.add(StatementPos.TARGET, StatementKind.METHOD_CALL, modType2);

        expectedVector.add(StatementPos.PREV, VarChar.LOCAL, VarChar.LOCAL);
        expectedVector.add(StatementPos.PREV, VarChar.LOCAL, VarChar.NUM);
        expectedVector.add(StatementPos.PREV, VarChar.LOCAL, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarChar.NUM, VarChar.NUM);
        expectedVector.add(StatementPos.PREV, VarChar.NUM, VarChar.LOCAL);
        expectedVector.add(StatementPos.PREV, VarChar.NUM, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarChar.IN_ASSIGN_STMT, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarChar.IN_ASSIGN_STMT, VarChar.LOCAL);
        expectedVector.add(StatementPos.PREV, VarChar.IN_ASSIGN_STMT, VarChar.NUM);

        expectedVector.add(StatementPos.PREV, VarChar.LOCAL, VarChar.LOCAL);
        expectedVector.add(StatementPos.PREV, VarChar.LOCAL, VarChar.NUM);
        expectedVector.add(StatementPos.PREV, VarChar.LOCAL, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarChar.NUM, VarChar.NUM);
        expectedVector.add(StatementPos.PREV, VarChar.NUM, VarChar.LOCAL);
        expectedVector.add(StatementPos.PREV, VarChar.NUM, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarChar.IN_ASSIGN_STMT, VarChar.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarChar.IN_ASSIGN_STMT, VarChar.LOCAL);
        expectedVector.add(StatementPos.PREV, VarChar.IN_ASSIGN_STMT, VarChar.NUM);

        expectedVector.add(StatementPos.TARGET, VarChar.LOCAL, VarChar.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarChar.LOCAL, VarChar.NUM);
        expectedVector.add(StatementPos.TARGET, VarChar.LOCAL, VarChar.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarChar.NUM, VarChar.NUM);
        expectedVector.add(StatementPos.TARGET, VarChar.NUM, VarChar.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarChar.NUM, VarChar.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarChar.IN_ASSIGN_STMT, VarChar.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarChar.IN_ASSIGN_STMT, VarChar.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarChar.IN_ASSIGN_STMT, VarChar.NUM);

        assertThat(expectedVector.asBooleanList()).containsExactlyElementsOf(binaryVector);
    }
}