package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import com.github.javaparser.ast.Node;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;

public class ValueFeatureExtractorTest {    
    @Test public void test() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public String str;\n")
            .append("   public void a(Object o) {\n")
            .append("       final int i = 0;\n")
            .append("       int j = 0;\n")
            .append("       if(i == 0) {\n")
            .append("           o.toString();\n")
            .append("           j = 1;\n")
            .append("       }\n")
            .append("   }\n")
            .append("}\n")
            .toString();

            Node node = NodeUtility.getAllNodesFromCode(src).get(0);
            ValueFeatureExtractor extractor = new ValueFeatureExtractor();
            List<ValueFeatureVec> vecs = extractor.extract(node);
            return;
    }
}