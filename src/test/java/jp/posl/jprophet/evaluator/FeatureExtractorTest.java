package jp.posl.jprophet.evaluator;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.extractor.FeatureExtractor;
import jp.posl.jprophet.evaluator.extractor.FeatureVector;
import jp.posl.jprophet.evaluator.extractor.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.extractor.feature.ModFeature.ModType;
import jp.posl.jprophet.evaluator.extractor.feature.StatementFeature.StatementType;
import jp.posl.jprophet.evaluator.extractor.feature.VariableFeature.VarType;
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
        final ModType modType = ModType.INSERT_STMT;
        expectedVector.add(modType);

        expectedVector.add(StatementPos.PREV, StatementType.ASSIGN, modType);
        expectedVector.add(StatementPos.TARGET, StatementType.METHOD_CALL, modType);

        expectedVector.add(StatementPos.PREV, VarType.LOCAL, VarType.LOCAL);
        expectedVector.add(StatementPos.PREV, VarType.LOCAL, VarType.NUM);
        expectedVector.add(StatementPos.PREV, VarType.LOCAL, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.NUM);
        expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.LOCAL);
        expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarType.IN_ASSIGN_STMT, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarType.IN_ASSIGN_STMT, VarType.LOCAL);
        expectedVector.add(StatementPos.PREV, VarType.IN_ASSIGN_STMT, VarType.NUM);

        expectedVector.add(StatementPos.TARGET, VarType.LOCAL, VarType.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarType.LOCAL, VarType.NUM);
        expectedVector.add(StatementPos.TARGET, VarType.LOCAL, VarType.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarType.NUM, VarType.NUM);
        expectedVector.add(StatementPos.TARGET, VarType.NUM, VarType.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarType.NUM, VarType.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarType.IN_ASSIGN_STMT, VarType.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarType.IN_ASSIGN_STMT, VarType.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarType.IN_ASSIGN_STMT, VarType.NUM);
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

        final ModType modType1 = ModType.INSERT_STMT;
        expectedVector.add(modType1);
        expectedVector.add(StatementPos.TARGET, StatementType.ASSIGN, modType1);
        expectedVector.add(StatementPos.NEXT, StatementType.ASSIGN, modType1);
        expectedVector.add(StatementPos.NEXT, StatementType.METHOD_CALL, modType1);

        expectedVector.add(StatementPos.TARGET, VarType.LOCAL, VarType.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarType.LOCAL, VarType.NUM);
        expectedVector.add(StatementPos.TARGET, VarType.LOCAL, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.TARGET, VarType.NUM, VarType.NUM);
        expectedVector.add(StatementPos.TARGET, VarType.NUM, VarType.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarType.NUM, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.TARGET, VarType.IN_ASSIGN_STMT, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.TARGET, VarType.IN_ASSIGN_STMT, VarType.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarType.IN_ASSIGN_STMT, VarType.NUM);

        expectedVector.add(StatementPos.NEXT, VarType.LOCAL, VarType.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarType.LOCAL, VarType.NUM);
        expectedVector.add(StatementPos.NEXT, VarType.LOCAL, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.NEXT, VarType.NUM, VarType.NUM);
        expectedVector.add(StatementPos.NEXT, VarType.NUM, VarType.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarType.NUM, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.NEXT, VarType.IN_ASSIGN_STMT, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.NEXT, VarType.IN_ASSIGN_STMT, VarType.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarType.IN_ASSIGN_STMT, VarType.NUM);

        expectedVector.add(StatementPos.NEXT, VarType.LOCAL, VarType.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarType.LOCAL, VarType.NUM);
        expectedVector.add(StatementPos.NEXT, VarType.LOCAL, VarType.PARAMETER);
        expectedVector.add(StatementPos.NEXT, VarType.NUM, VarType.NUM);
        expectedVector.add(StatementPos.NEXT, VarType.NUM, VarType.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarType.NUM, VarType.PARAMETER);
        expectedVector.add(StatementPos.NEXT, VarType.IN_ASSIGN_STMT, VarType.PARAMETER);
        expectedVector.add(StatementPos.NEXT, VarType.IN_ASSIGN_STMT, VarType.LOCAL);
        expectedVector.add(StatementPos.NEXT, VarType.IN_ASSIGN_STMT, VarType.NUM);


        final ModType modType2 = ModType.REPLACE_VAR;
        expectedVector.add(modType2);
        expectedVector.add(StatementPos.PREV, StatementType.ASSIGN, modType2);
        expectedVector.add(StatementPos.PREV, StatementType.ASSIGN, modType2);
        expectedVector.add(StatementPos.TARGET, StatementType.METHOD_CALL, modType2);

        expectedVector.add(StatementPos.PREV, VarType.LOCAL, VarType.LOCAL);
        expectedVector.add(StatementPos.PREV, VarType.LOCAL, VarType.NUM);
        expectedVector.add(StatementPos.PREV, VarType.LOCAL, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.NUM);
        expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.LOCAL);
        expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarType.IN_ASSIGN_STMT, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarType.IN_ASSIGN_STMT, VarType.LOCAL);
        expectedVector.add(StatementPos.PREV, VarType.IN_ASSIGN_STMT, VarType.NUM);

        expectedVector.add(StatementPos.PREV, VarType.LOCAL, VarType.LOCAL);
        expectedVector.add(StatementPos.PREV, VarType.LOCAL, VarType.NUM);
        expectedVector.add(StatementPos.PREV, VarType.LOCAL, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.NUM);
        expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.LOCAL);
        expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarType.IN_ASSIGN_STMT, VarType.IN_ASSIGN_STMT);
        expectedVector.add(StatementPos.PREV, VarType.IN_ASSIGN_STMT, VarType.LOCAL);
        expectedVector.add(StatementPos.PREV, VarType.IN_ASSIGN_STMT, VarType.NUM);

        expectedVector.add(StatementPos.TARGET, VarType.LOCAL, VarType.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarType.LOCAL, VarType.NUM);
        expectedVector.add(StatementPos.TARGET, VarType.LOCAL, VarType.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarType.NUM, VarType.NUM);
        expectedVector.add(StatementPos.TARGET, VarType.NUM, VarType.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarType.NUM, VarType.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarType.IN_ASSIGN_STMT, VarType.PARAMETER);
        expectedVector.add(StatementPos.TARGET, VarType.IN_ASSIGN_STMT, VarType.LOCAL);
        expectedVector.add(StatementPos.TARGET, VarType.IN_ASSIGN_STMT, VarType.NUM);


        assertThat(expectedVector.get()).containsExactlyElementsOf(binaryVector);
    }

}