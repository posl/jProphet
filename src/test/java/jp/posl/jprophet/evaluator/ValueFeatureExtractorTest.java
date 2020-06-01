package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import com.github.javaparser.ast.Node;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.ValueFeatureVec.Scope;
import jp.posl.jprophet.evaluator.ValueFeatureVec.ValueType;

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
        List<ValueFeatureVec> actualVecs = extractor.extract(node);

        ValueFeatureVec expectedFieldVec = new ValueFeatureVec();
        expectedFieldVec.type = ValueType.STRING;
        expectedFieldVec.scope = Scope.FIELD;
        ValueFeatureVec expectedParameterVec = new ValueFeatureVec();
        expectedParameterVec.type = ValueType.OBJECT;
        expectedParameterVec.scope = Scope.ARGUMENT;
        ValueFeatureVec expectedLocalVarVec = new ValueFeatureVec();
        expectedLocalVarVec.type = ValueType.NUM;
        expectedLocalVarVec.scope = Scope.LOCAL;
        expectedLocalVarVec.constant = true;

        assertThat(actualVecs.get(0)).isEqualToComparingFieldByField(expectedFieldVec);
        assertThat(actualVecs.get(1)).isEqualToComparingFieldByField(expectedLocalVarVec);
        assertThat(actualVecs.get(2)).isEqualToComparingFieldByField(expectedParameterVec);
        return;
    }
}