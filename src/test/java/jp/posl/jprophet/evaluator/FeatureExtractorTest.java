package jp.posl.jprophet.evaluator;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.operation.MethodReplacementOperation;
import jp.posl.jprophet.patch.DefaultPatchCandidate;
import jp.posl.jprophet.patch.PatchCandidate;

public class FeatureExtractorTest {
    @Test void hoge() {
        final FeatureExtractor featureExtractor = new FeatureExtractor();
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
            .append("   }\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final PatchCandidate patchCandidate = new DefaultPatchCandidate(originalNodes.get(0), revisedNodes.get(0).findCompilationUnit().get(), "", "", MethodReplacementOperation.class, 0);
        final FeatureExtractor extractor = new FeatureExtractor();
        FeatureVector vec = extractor.extract(patchCandidate);
    }
}