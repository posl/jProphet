package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Node;

import org.junit.Test;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import jp.posl.jprophet.NodeUtility;

public class PatchFeatureTest {

    @Test public void test() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       fuga();\n\n")
            .append("       if(foo)\n\n")
            .append("           hoge();\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final String revisedSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("   public void a() {\n\n")
            .append("       fuga2();\n\n")
            .append("       if(bar)\n\n")
            .append("           hoge();\n\n")
            .append("   }\n\n")
            .append("}\n")
            .toString();

        final List<Node> originalNodes = NodeUtility.getAllNodesFromCode(originalSource);
        final List<Node> revisedNodes = NodeUtility.getAllNodesFromCode(revisedSource);

        final PatchFeature patchFeature = new PatchFeature();
        final Map<String, Integer> featureVec = patchFeature.exec(originalNodes.get(0), revisedNodes.get(0));
        return;
    }

    @Test public void hoge() {
        final List<String> ori = List.of(
            "A",
            "public void a()",
            "a",
            "void",
            "{",
            "hoge();",
            "hoge()",
            "hoge"
        );
        final List<String> rev = List.of(
            "A",
            "public void a()",
            "a",
            "void",
            "{",
            "if(foo)",
            "foo",
            "hoge();",
            "foo",
            "hoge()",
            "hoge"
        );

        final Patch<String> diff = DiffUtils.diff(ori, rev);
        final List<Delta<String>> deltas = diff.getDeltas();
        return;
    }
}