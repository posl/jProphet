package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import com.github.javaparser.ast.Node;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.ValueFeature.Scope;
import jp.posl.jprophet.evaluator.ValueFeature.ValueType;

public class ValueFeatureExtractorTest {
    @Test public void test() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    final public String str;\n")
            .append("    public void a(Object o) {\n")
            .append("        final int i = 0;\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        ValueFeatureExtractor extractor = new ValueFeatureExtractor();
        List<ValueFeature> actualFeatures = extractor.extract(node);

        ValueFeature expectedFieldFeature = new ValueFeature();
        expectedFieldFeature.type = ValueType.STRING;
        expectedFieldFeature.scope = Scope.FIELD;
        ValueFeature expectedParameterFeature = new ValueFeature();
        expectedParameterFeature.type = ValueType.OBJECT;
        expectedParameterFeature.scope = Scope.ARGUMENT;
        ValueFeature expectedLocalVarFeature = new ValueFeature();
        expectedLocalVarFeature.type = ValueType.NUM;
        expectedLocalVarFeature.scope = Scope.LOCAL;
        expectedLocalVarFeature.constant = true;

        assertThat(actualFeatures.get(0)).isEqualToComparingFieldByField(expectedFieldFeature);
        assertThat(actualFeatures.get(1)).isEqualToComparingFieldByField(expectedLocalVarFeature);
        assertThat(actualFeatures.get(2)).isEqualToComparingFieldByField(expectedParameterFeature);
        return;
    }
}