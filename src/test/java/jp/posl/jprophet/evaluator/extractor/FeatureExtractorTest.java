package jp.posl.jprophet.evaluator.extractor;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.extractor.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.extractor.StatementKindExtractor.StatementType;
import jp.posl.jprophet.evaluator.extractor.feature.ModKinds.ModKind;
import jp.posl.jprophet.evaluator.extractor.feature.VariableKinds.VarKind;
import jp.posl.jprophet.operation.MethodReplacementOperation;
import jp.posl.jprophet.patch.DefaultPatchCandidate;
import jp.posl.jprophet.patch.PatchCandidate;

public class FeatureExtractorTest {

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

        final PatchCandidate patchCandidate = new DefaultPatchCandidate(originalNodes.get(0), revisedNodes.get(0).findCompilationUnit().get(), "", "", MethodReplacementOperation.class, 0);
        final FeatureExtractor extractor = new FeatureExtractor();
        FeatureVector featureVector = extractor.extract(patchCandidate);
        List<Boolean> binaryVector = featureVector.get();
        final FeatureVector expectedVector = new FeatureVector();
        final ModKind modType = ModKind.INSERT_STMT;
        expectedVector.add(modType);

        expectedVector.add(StatementPos.PREV, StatementType.ASSIGN, modType);
        expectedVector.add(StatementPos.TARGET, StatementType.METHOD_CALL, modType);

        expectedVector.add(StatementPos.PREV, VarKind.LOCAL, VarKind.LOCAL);
        expectedVector.add(StatementPos.PREV, VarKind.LOCAL, VarKind.NUM);
        expectedVector.add(StatementPos.PREV, VarKind.LOCAL, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarKind.NUM, VarKind.NUM);
        expectedVector.add(StatementPos.PREV, VarKind.NUM, VarKind.LOCAL);
        expectedVector.add(StatementPos.PREV, VarKind.NUM, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarKind.IN_ASSIGN_STMT, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarKind.IN_ASSIGN_STMT, VarKind.LOCAL);
        expectedVector.add(StatementPos.PREV, VarKind.IN_ASSIGN_STMT, VarKind.NUM);

        expectedVector.add(StatementPos.TARGET, VarKind.LOCAL, VarKind.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarKind.LOCAL, VarKind.NUM);
        expectedVector.add(StatementPos.TARGET, VarKind.LOCAL, VarKind.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarKind.NUM, VarKind.NUM);
        expectedVector.add(StatementPos.TARGET, VarKind.NUM, VarKind.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarKind.NUM, VarKind.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarKind.IN_ASSIGN_STMT, VarKind.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarKind.IN_ASSIGN_STMT, VarKind.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarKind.IN_ASSIGN_STMT, VarKind.NUM);
        assertThat(expectedVector.get()).containsExactlyElementsOf(binaryVector);
    }

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

        final PatchCandidate patchCandidate = new DefaultPatchCandidate(originalNodes.get(0), revisedNodes.get(0).findCompilationUnit().get(), "", "", MethodReplacementOperation.class, 0);
        final FeatureExtractor extractor = new FeatureExtractor();
        FeatureVector featureVector = extractor.extract(patchCandidate);
        List<Boolean> binaryVector = featureVector.get();
        final FeatureVector expectedVector = new FeatureVector();

        final ModKind modType1 = ModKind.INSERT_STMT;
        expectedVector.add(modType1);
        expectedVector.add(StatementPos.TARGET, StatementType.ASSIGN, modType1);
        expectedVector.add(StatementPos.NEXT, StatementType.ASSIGN, modType1);
        expectedVector.add(StatementPos.NEXT, StatementType.METHOD_CALL, modType1);

        expectedVector.add(StatementPos.TARGET, VarKind.LOCAL, VarKind.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarKind.LOCAL, VarKind.NUM);
        expectedVector.add(StatementPos.TARGET, VarKind.LOCAL, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.TARGET, VarKind.NUM, VarKind.NUM);
        expectedVector.add(StatementPos.TARGET, VarKind.NUM, VarKind.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarKind.NUM, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.TARGET, VarKind.IN_ASSIGN_STMT, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.TARGET, VarKind.IN_ASSIGN_STMT, VarKind.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarKind.IN_ASSIGN_STMT, VarKind.NUM);

        expectedVector.add(StatementPos.NEXT, VarKind.LOCAL, VarKind.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarKind.LOCAL, VarKind.NUM);
        expectedVector.add(StatementPos.NEXT, VarKind.LOCAL, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.NEXT, VarKind.NUM, VarKind.NUM);
        expectedVector.add(StatementPos.NEXT, VarKind.NUM, VarKind.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarKind.NUM, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.NEXT, VarKind.IN_ASSIGN_STMT, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.NEXT, VarKind.IN_ASSIGN_STMT, VarKind.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarKind.IN_ASSIGN_STMT, VarKind.NUM);

        expectedVector.add(StatementPos.NEXT, VarKind.LOCAL, VarKind.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarKind.LOCAL, VarKind.NUM);
        expectedVector.add(StatementPos.NEXT, VarKind.LOCAL, VarKind.PARAMETER);
        expectedVector.add(StatementPos.NEXT, VarKind.NUM, VarKind.NUM);
        expectedVector.add(StatementPos.NEXT, VarKind.NUM, VarKind.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarKind.NUM, VarKind.PARAMETER);
        expectedVector.add(StatementPos.NEXT, VarKind.IN_ASSIGN_STMT, VarKind.PARAMETER);
        expectedVector.add(StatementPos.NEXT, VarKind.IN_ASSIGN_STMT, VarKind.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarKind.IN_ASSIGN_STMT, VarKind.NUM);


        final ModKind modType2 = ModKind.REPLACE_VAR;
        expectedVector.add(modType2);
        expectedVector.add(StatementPos.PREV, StatementType.ASSIGN, modType2);
        expectedVector.add(StatementPos.PREV, StatementType.ASSIGN, modType2);
        expectedVector.add(StatementPos.TARGET, StatementType.METHOD_CALL, modType2);

        expectedVector.add(StatementPos.PREV, VarKind.LOCAL, VarKind.LOCAL);
        expectedVector.add(StatementPos.PREV, VarKind.LOCAL, VarKind.NUM);
        expectedVector.add(StatementPos.PREV, VarKind.LOCAL, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarKind.NUM, VarKind.NUM);
        expectedVector.add(StatementPos.PREV, VarKind.NUM, VarKind.LOCAL);
        expectedVector.add(StatementPos.PREV, VarKind.NUM, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarKind.IN_ASSIGN_STMT, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarKind.IN_ASSIGN_STMT, VarKind.LOCAL);
        expectedVector.add(StatementPos.PREV, VarKind.IN_ASSIGN_STMT, VarKind.NUM);

        expectedVector.add(StatementPos.PREV, VarKind.LOCAL, VarKind.LOCAL);
        expectedVector.add(StatementPos.PREV, VarKind.LOCAL, VarKind.NUM);
        expectedVector.add(StatementPos.PREV, VarKind.LOCAL, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarKind.NUM, VarKind.NUM);
        expectedVector.add(StatementPos.PREV, VarKind.NUM, VarKind.LOCAL);
        expectedVector.add(StatementPos.PREV, VarKind.NUM, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarKind.IN_ASSIGN_STMT, VarKind.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarKind.IN_ASSIGN_STMT, VarKind.LOCAL);
        expectedVector.add(StatementPos.PREV, VarKind.IN_ASSIGN_STMT, VarKind.NUM);

        expectedVector.add(StatementPos.TARGET, VarKind.LOCAL, VarKind.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarKind.LOCAL, VarKind.NUM);
        expectedVector.add(StatementPos.TARGET, VarKind.LOCAL, VarKind.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarKind.NUM, VarKind.NUM);
        expectedVector.add(StatementPos.TARGET, VarKind.NUM, VarKind.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarKind.NUM, VarKind.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarKind.IN_ASSIGN_STMT, VarKind.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarKind.IN_ASSIGN_STMT, VarKind.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarKind.IN_ASSIGN_STMT, VarKind.NUM);


        assertThat(expectedVector.get()).containsExactlyElementsOf(binaryVector);
    }

}