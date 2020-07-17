package jp.posl.jprophet.evaluator.extractor;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.NameExpr;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.extractor.feature.VariableKinds;
import jp.posl.jprophet.evaluator.extractor.feature.VariableKinds.VarKind;

public class VariableKindExtractorTest {
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

        Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        VariableKindExtractor extractor = new VariableKindExtractor();
        List<VariableKinds> actualKindsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        VariableKinds expectedFieldKind = new VariableKinds(Set.of(
            VarKind.STRING,
            VarKind.OBJECT,
            VarKind.FIELD,
            VarKind.IN_ASSIGN_STMT
        ));
        VariableKinds expectedLocalVarKind = new VariableKinds(Set.of(
            VarKind.BOOLEAN,
            VarKind.LOCAL,
            VarKind.IN_ASSIGN_STMT
        ));
        VariableKinds expectedLocalConstKind = new VariableKinds(Set.of(
            VarKind.NUM,
            VarKind.LOCAL,
            VarKind.CONSTANT,
            VarKind.IN_ASSIGN_STMT
        ));

    
        assertThat(actualKindsList.get(0)).isEqualToComparingFieldByField(expectedLocalVarKind);
        assertThat(actualKindsList.get(1)).isEqualToComparingFieldByField(expectedLocalConstKind);
        assertThat(actualKindsList.get(2)).isEqualToComparingFieldByField(expectedFieldKind);
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

        Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        VariableKindExtractor extractor = new VariableKindExtractor();
        List<VariableKinds> actualKindsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        VariableKinds expectedKindsInCond = new VariableKinds(Set.of(
            VarKind.IN_CONDITION,
            VarKind.IN_IF_STMT
        ));
        VariableKinds expectedKindsInIfStmt = new VariableKinds(Set.of(
            VarKind.IN_IF_STMT,
            VarKind.IN_ASSIGN_STMT
        ));
        VariableKinds expectedKindsInLoop = new VariableKinds(Set.of(
            VarKind.IN_LOOP,
            VarKind.IN_ASSIGN_STMT
        ));
        VariableKinds expectedKindsInForeachCond = new VariableKinds(Set.of(
            VarKind.IN_LOOP
        ));
        VariableKinds expectedParameterKind = new VariableKinds(Set.of(
            VarKind.PARAMETER
        ));

        assertThat(actualKindsList.get(0)).isEqualToComparingFieldByField(expectedKindsInCond);
        assertThat(actualKindsList.get(1)).isEqualToComparingFieldByField(expectedKindsInIfStmt);
        assertThat(actualKindsList.get(2)).isEqualToComparingFieldByField(expectedKindsInLoop);
        assertThat(actualKindsList.get(3)).isEqualToComparingFieldByField(expectedKindsInLoop);
        assertThat(actualKindsList.get(4)).isEqualToComparingFieldByField(expectedKindsInForeachCond);
        assertThat(actualKindsList.get(5)).isEqualToComparingFieldByField(expectedKindsInLoop);
        assertThat(actualKindsList.get(6)).isEqualToComparingFieldByField(expectedParameterKind);
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

        Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        VariableKindExtractor extractor = new VariableKindExtractor();
        List<VariableKinds> actualKindsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        VariableKinds expectedKinds = new VariableKinds(Set.of(
            VarKind.COMMUTATIVE_OPERAND
        ));
        
        actualKindsList.stream().forEach(actual -> {
            assertThat(actual).isEqualToComparingFieldByField(expectedKinds);
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

        Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        VariableKindExtractor extractor = new VariableKindExtractor();
        List<VariableKinds> actualKindsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        VariableKinds expectedLeftVarKind = new VariableKinds(Set.of(
            VarKind.NONCOMMUTATIVE_OPERAND_LEFT
        ));
        VariableKinds expectedRightVarKind = new VariableKinds(Set.of(
            VarKind.NONCOMMUTATIVE_OPERAND_RIGHT
        ));
        
        for(int i = 0; i < actualKindsList.size(); i += 2) {
            assertThat(actualKindsList.get(i)).isEqualToComparingFieldByField(expectedLeftVarKind);
            assertThat(actualKindsList.get(i + 1)).isEqualToComparingFieldByField(expectedRightVarKind);
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

        Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        VariableKindExtractor extractor = new VariableKindExtractor();
        List<VariableKinds> actualKindsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        VariableKinds expectedLeftVarKind = new VariableKinds(Set.of(
            VarKind.NONCOMMUTATIVE_OPERAND_LEFT
        ));
        VariableKinds expectedRightVarKind = new VariableKinds(Set.of(
            VarKind.NONCOMMUTATIVE_OPERAND_RIGHT,
            VarKind.PARAMETER
        ));
        
        assertThat(actualKindsList.get(0)).isEqualToComparingFieldByField(expectedLeftVarKind);
        assertThat(actualKindsList.get(1)).isEqualToComparingFieldByField(expectedRightVarKind);
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

        Node root = NodeUtility.getAllNodesFromCode(src).get(0);
        VariableKindExtractor extractor = new VariableKindExtractor();
        List<VariableKinds> actualKindsList = root.findAll(NameExpr.class).stream()
            .map(nameExpr -> extractor.extract(nameExpr))
            .collect(Collectors.toList());

        VariableKinds expectedKinds = new VariableKinds(Set.of(
            VarKind.UNARY_OPERAND
        ));
        
        actualKindsList.stream().forEach(actual -> {
            assertThat(actual).isEqualToComparingFieldByField(expectedKinds);
        });
    }
}