package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import com.github.javaparser.ast.Node;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.ValueFeature.Scope;
import jp.posl.jprophet.evaluator.ValueFeature.VarType;

public class ValueFeatureExtractorTest {
    @Test public void testDeclaration() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    final public String str;\n")
            .append("    public void a(Object o) {\n")
            .append("        boolean bool = 0;\n")
            .append("        final int i = 0;\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        ValueFeatureExtractor extractor = new ValueFeatureExtractor();
        List<ValueFeature> actualFeatures = extractor.extract(node);

        ValueFeature expectedFieldFeature = new ValueFeature();
        expectedFieldFeature.type = VarType.STRING;
        expectedFieldFeature.scope = Scope.FIELD;
        ValueFeature expectedLocalVarFeature = new ValueFeature();
        expectedLocalVarFeature.type = VarType.BOOLEAN;
        expectedLocalVarFeature.scope = Scope.LOCAL;
        ValueFeature expectedLocalConstFeature = new ValueFeature();
        expectedLocalConstFeature.type = VarType.NUM;
        expectedLocalConstFeature.scope = Scope.LOCAL;
        expectedLocalConstFeature.constant = true;
        ValueFeature expectedArgumentFeature = new ValueFeature();
        expectedArgumentFeature.type = VarType.OBJECT;
        expectedArgumentFeature.scope = Scope.ARGUMENT;

        assertThat(actualFeatures.get(0)).isEqualToComparingFieldByField(expectedFieldFeature);
        assertThat(actualFeatures.get(1)).isEqualToComparingFieldByField(expectedLocalVarFeature);
        assertThat(actualFeatures.get(2)).isEqualToComparingFieldByField(expectedLocalConstFeature);
        assertThat(actualFeatures.get(3)).isEqualToComparingFieldByField(expectedArgumentFeature);
        return;
    }

    @Test public void testValueInStmt() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    public void a() {\n")
            .append("        if(cond)\n")
            .append("           hoge = 0;\n")
            .append("        while(true)\n")
            .append("           fuga = 0;\n")
            .append("        for(;;)\n")
            .append("           foo = 0;\n")
            .append("        for(String str: strs)\n")
            .append("           bar = 0;\n")
            .append("        method(piyo);\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        ValueFeatureExtractor extractor = new ValueFeatureExtractor();
        List<ValueFeature> actualFeatures = extractor.extract(node);

        ValueFeature expectedFeatureInCond = new ValueFeature();
        expectedFeatureInCond.condition = true;
        expectedFeatureInCond.ifStmt = true;
        ValueFeature expectedFeatureInIfStmt = new ValueFeature();
        expectedFeatureInIfStmt.ifStmt = true;
        expectedFeatureInIfStmt.assign = true;
        ValueFeature expectedFeatureInLoop = new ValueFeature();
        expectedFeatureInLoop.loop = true;
        expectedFeatureInLoop.assign = true;
        ValueFeature expectedFeatureInForeachCond = new ValueFeature();
        expectedFeatureInForeachCond.loop = true;
        ValueFeature expectedDeclarationFeatureInForeachCond = new ValueFeature();
        expectedDeclarationFeatureInForeachCond.loop = true;
        expectedDeclarationFeatureInForeachCond.type = VarType.STRING;
        expectedDeclarationFeatureInForeachCond.scope = Scope.LOCAL;
        ValueFeature expectedParameterFeature = new ValueFeature();
        expectedParameterFeature.parameter = true;

        assertThat(actualFeatures.get(0)).isEqualToComparingFieldByField(expectedFeatureInCond);
        assertThat(actualFeatures.get(1)).isEqualToComparingFieldByField(expectedFeatureInIfStmt);
        assertThat(actualFeatures.get(2)).isEqualToComparingFieldByField(expectedFeatureInLoop);
        assertThat(actualFeatures.get(3)).isEqualToComparingFieldByField(expectedFeatureInLoop);
        assertThat(actualFeatures.get(4)).isEqualToComparingFieldByField(expectedFeatureInForeachCond);
        assertThat(actualFeatures.get(5)).isEqualToComparingFieldByField(expectedFeatureInLoop);
        assertThat(actualFeatures.get(6)).isEqualToComparingFieldByField(expectedParameterFeature);
        // 宣言時の変数の特徴は，リストの最後尾に並ぶ
        assertThat(actualFeatures.get(7)).isEqualToComparingFieldByField(expectedDeclarationFeatureInForeachCond);
        return;
    }

    @Test public void testValueOfCommutativeOperator() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    public void a() {\n")
            .append("        (a + b);\n")
            .append("        (a * b);\n")
            .append("        (a == b);\n")
            .append("        (a != b);\n")
            .append("        (a || b);\n")
            .append("        (a && b);\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        ValueFeatureExtractor extractor = new ValueFeatureExtractor();
        List<ValueFeature> actualFeatures = extractor.extract(node);

        ValueFeature expectedFeature = new ValueFeature();
        expectedFeature.commutativeOp = true;
        
        actualFeatures.stream().forEach(actual -> {
            assertThat(actual).isEqualToComparingFieldByField(expectedFeature);
        });
        return;
    }

    @Test public void testValueOfBinaryOperator() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    public void a() {\n")
            .append("        (a - b);\n")
            .append("        (a / b);\n")
            .append("        (a % b);\n")
            .append("        (a < b);\n")
            .append("        (a > b);\n")
            .append("        (a <= b);\n")
            .append("        (a >= b);\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        ValueFeatureExtractor extractor = new ValueFeatureExtractor();
        List<ValueFeature> actualFeatures = extractor.extract(node);

        ValueFeature expectedLeftVarFeature = new ValueFeature();
        expectedLeftVarFeature.noncommutativeOpL = true;
        ValueFeature expectedRightVarFeature = new ValueFeature();
        expectedRightVarFeature.noncommutativeOpR = true;
        
        for(int i = 0; i < actualFeatures.size(); i += 2) {
            assertThat(actualFeatures.get(i)).isEqualToComparingFieldByField(expectedLeftVarFeature);
            assertThat(actualFeatures.get(i + 1)).isEqualToComparingFieldByField(expectedRightVarFeature);
        }
        return;
    }

    @Test public void testValueOfUnaryOperator() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    public void a() {\n")
            .append("        a++;\n")
            .append("        b--;\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        ValueFeatureExtractor extractor = new ValueFeatureExtractor();
        List<ValueFeature> actualFeatures = extractor.extract(node);

        ValueFeature expectedFeature = new ValueFeature();
        expectedFeature.unaryOp = true;
        
        actualFeatures.stream().forEach(actual -> {
            assertThat(actual).isEqualToComparingFieldByField(expectedFeature);
        });
        return;
    }
}