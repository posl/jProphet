package jp.posl.jprophet.evaluator;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.ModFeature.ModType;
import jp.posl.jprophet.evaluator.StatementFeature.StatementType;
import jp.posl.jprophet.evaluator.VariableFeature.VarType;
import jp.posl.jprophet.operation.MethodReplacementOperation;
import jp.posl.jprophet.patch.DefaultPatchCandidate;
import jp.posl.jprophet.patch.PatchCandidate;

public class FeatureExtractorTest {

    @Test public void testFeatureExtractor() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            // .append("       int hoge;\n")
            // .append("       hoge = 0;\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            // .append("       int hoge;\n")
            // .append("       hoge = 0;\n")
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
        // expectedVector.add(StatementPos.PREV, StatementType.ASSIGN, modType);
        expectedVector.add(StatementPos.TARGET, StatementType.METHOD_CALL, modType);
        // expectedVector.add(StatementPos.PREV, VarType.NUM, VarType.NUM);
        // expectedVector.add(StatementPos.TARGET, StatementType.METHOD_CALL, modType);
        assertThat(expectedVector.get()).containsExactlyElementsOf(binaryVector);
    }

}