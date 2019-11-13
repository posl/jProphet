package jp.posl.jprophet.operation;

import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.IfStmt;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import jp.posl.jprophet.NodeUtility;


public class ConditionGeneratorTest {
    @Test public void test() {
        String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n") 
            .append("    boolean ba;\n\n")
            .append("    private void ma() {\n")
            .append("        if (JPROPHET_ABST_HOLE)\n")
            .append("            return;\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        IfStmt targetIfStmt = JavaParser.parse(targetSource).findFirst(IfStmt.class).get();
        Expression abstHole = targetIfStmt.getCondition();

        ConditionGenerator condGenerator = new ConditionGenerator();
        Expression actualCondExpression = condGenerator.generateCondition(abstHole);

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n\n") 
            .append("    boolean ba;\n\n")
            .append("    private void ma() {\n")
            .append("        if (ba == a)\n")
            .append("            return;\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        IfStmt expectedIfStmt = JavaParser.parse(expectedSource).findFirst(IfStmt.class).get();
        Expression expectedCondExpression = expectedIfStmt.getCondition();

        assertThat(actualCondExpression).isEqualTo(expectedCondExpression);

    }
}