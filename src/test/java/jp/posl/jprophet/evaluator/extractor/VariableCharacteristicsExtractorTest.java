package jp.posl.jprophet.evaluator.extractor;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.NameExpr;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.extractor.feature.VariableCharacteristics;
import jp.posl.jprophet.evaluator.extractor.feature.VariableCharacteristics.VarChar;

public class VariableCharacteristicsExtractorTest {
    /**
     * 宣言ノードにおける変数の型やスコープの特徴抽出のテスト
     */
    @Test public void testDeclaration() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    final public String str;\n")
            .append("    public void a() {\n")
            .append("        boolean bool = true;\n")
            .append("        final int i = 0;\n")
            .append("        bool = false;\n")
            .append("        i = 1;\n")
            .append("        str = \"str\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        final VariableCharacteristicExtractor extractor = new VariableCharacteristicExtractor();
        final List<VariableCharacteristics> actualCharsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        final VariableCharacteristics expectedFieldChars = new VariableCharacteristics(Set.of(
            VarChar.STRING,
            VarChar.OBJECT,
            VarChar.FIELD,
            VarChar.IN_ASSIGN_STMT
        ));
        final VariableCharacteristics expectedLocalVarChars = new VariableCharacteristics(Set.of(
            VarChar.BOOLEAN,
            VarChar.LOCAL,
            VarChar.IN_ASSIGN_STMT
        ));
        final VariableCharacteristics expectedLocalConstChars = new VariableCharacteristics(Set.of(
            VarChar.NUM,
            VarChar.LOCAL,
            VarChar.CONSTANT,
            VarChar.IN_ASSIGN_STMT
        ));

    
        assertThat(actualCharsList.get(0)).isEqualToComparingFieldByField(expectedLocalVarChars);
        assertThat(actualCharsList.get(1)).isEqualToComparingFieldByField(expectedLocalConstChars);
        assertThat(actualCharsList.get(2)).isEqualToComparingFieldByField(expectedFieldChars);
    }


    /**
     * 変数がどのステートメントに含まれているか
     */
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

        final Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        final VariableCharacteristicExtractor extractor = new VariableCharacteristicExtractor();
        final List<VariableCharacteristics> actualCharsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        final VariableCharacteristics expectedCharsInCond = new VariableCharacteristics(Set.of(
            VarChar.IN_CONDITION,
            VarChar.IN_IF_STMT
        ));
        final VariableCharacteristics expectedCharsInIfStmt = new VariableCharacteristics(Set.of(
            VarChar.IN_IF_STMT,
            VarChar.IN_ASSIGN_STMT
        ));
        final VariableCharacteristics expectedCharsInLoop = new VariableCharacteristics(Set.of(
            VarChar.IN_LOOP,
            VarChar.IN_ASSIGN_STMT
        ));
        final VariableCharacteristics expectedCharsInForeachCond = new VariableCharacteristics(Set.of(
            VarChar.IN_LOOP
        ));
        final VariableCharacteristics expectedParameterChars = new VariableCharacteristics(Set.of(
            VarChar.PARAMETER
        ));

        assertThat(actualCharsList.get(0)).isEqualToComparingFieldByField(expectedCharsInCond);
        assertThat(actualCharsList.get(1)).isEqualToComparingFieldByField(expectedCharsInIfStmt);
        assertThat(actualCharsList.get(2)).isEqualToComparingFieldByField(expectedCharsInLoop);
        assertThat(actualCharsList.get(3)).isEqualToComparingFieldByField(expectedCharsInLoop);
        assertThat(actualCharsList.get(4)).isEqualToComparingFieldByField(expectedCharsInForeachCond);
        assertThat(actualCharsList.get(5)).isEqualToComparingFieldByField(expectedCharsInLoop);
        assertThat(actualCharsList.get(6)).isEqualToComparingFieldByField(expectedParameterChars);
    }

    /**
     * 変数が可換演算中に存在する時の特徴抽出
     */
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

        final Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        final VariableCharacteristicExtractor extractor = new VariableCharacteristicExtractor();
        final List<VariableCharacteristics> actualCharsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        final VariableCharacteristics expectedChars = new VariableCharacteristics(Set.of(
            VarChar.COMMUTATIVE_OPERAND
        ));
        
        actualCharsList.stream().forEach(actual -> {
            assertThat(actual).isEqualToComparingFieldByField(expectedChars);
        });
    }

    /**
     * 変数が二項演算中に存在する時の特徴抽出
     */
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

        final Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        final VariableCharacteristicExtractor extractor = new VariableCharacteristicExtractor();
        final List<VariableCharacteristics> actualCharsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        final VariableCharacteristics expectedLeftVarChars = new VariableCharacteristics(Set.of(
            VarChar.NONCOMMUTATIVE_OPERAND_LEFT
        ));
        final VariableCharacteristics expectedRightVarChars = new VariableCharacteristics(Set.of(
            VarChar.NONCOMMUTATIVE_OPERAND_RIGHT
        ));
        
        for(int i = 0; i < actualCharsList.size(); i += 2) {
            assertThat(actualCharsList.get(i)).isEqualToComparingFieldByField(expectedLeftVarChars);
            assertThat(actualCharsList.get(i + 1)).isEqualToComparingFieldByField(expectedRightVarChars);
        }
    }

    /**
     * 変数が二項演算の被演算子の子ノードに存在する時
     */
    @Test public void test() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    public void a() {\n")
            .append("        (a - hoge(b));\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        final VariableCharacteristicExtractor extractor = new VariableCharacteristicExtractor();
        final List<VariableCharacteristics> actualCharsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        final VariableCharacteristics expectedLeftVarChars = new VariableCharacteristics(Set.of(
            VarChar.NONCOMMUTATIVE_OPERAND_LEFT
        ));
        final VariableCharacteristics expectedRightVarChars = new VariableCharacteristics(Set.of(
            VarChar.NONCOMMUTATIVE_OPERAND_RIGHT,
            VarChar.PARAMETER
        ));
        
        assertThat(actualCharsList.get(0)).isEqualToComparingFieldByField(expectedLeftVarChars);
        assertThat(actualCharsList.get(1)).isEqualToComparingFieldByField(expectedRightVarChars);
    }

    /**
     * 変数が単項演算中に存在する時
     */
    @Test public void testValueOfUnaryOperator() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    public void a() {\n")
            .append("        a++;\n")
            .append("        b--;\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        final VariableCharacteristicExtractor extractor = new VariableCharacteristicExtractor();
        final List<VariableCharacteristics> actualCharsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        final VariableCharacteristics expectedChars = new VariableCharacteristics(Set.of(
            VarChar.UNARY_OPERAND
        ));
        
        actualCharsList.stream().forEach(actual -> {
            assertThat(actual).isEqualToComparingFieldByField(expectedChars);
        });
    }
}