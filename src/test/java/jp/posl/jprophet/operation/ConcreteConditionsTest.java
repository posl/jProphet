package jp.posl.jprophet.operation;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;



public class ConcreteConditionsTest {
    @Test public void test() {
        String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n") 
            .append("    boolean fieldBoolVarA;\n\n")
            .append("    private void ma() {\n")
            .append("        boolean localBoolVarA;\n\n")
            .append("        Object localObjectA;\n\n")
            .append("        if (JPROPHET_ABST_HOLE)\n")
            .append("            return;\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final IfStmt targetIfStmt = JavaParser.parse(targetSource).findFirst(IfStmt.class).get();
        final Expression abstCondition = targetIfStmt.getCondition();

        final ConcreteConditions concreteConditions = new ConcreteConditions(abstCondition);
        final List<Expression> actualCondExpressions = concreteConditions.getExpressions();

        final String expectedSourceBeforeTarget = new StringBuilder().append("")
            .append("public class A {\n\n") 
            .append("    boolean fieldBoolVarA;\n\n")
            .append("    private void methodA() {\n")
            .append("        boolean localBoolVarA;\n\n")
            .append("        Object localObjectA;\n\n")
            .toString();

        final List<String> expectedTargetSources = List.of(
                    "        if (fieldBoolVarA == true)\n",
                    "        if (fieldBoolVarA == false)\n",
                    "        if (localBoolVarA == true)\n",
                    "        if (localBoolVarA == false)\n",
                    "        if (localObjectA == null)\n",
                    "        if (localObjectA != null)\n",
                    "        if (fieldBoolVarA == null)\n",
                    "        if (fieldBoolVarA != null)\n",
                    "        if (localBoolVarA == null)\n",
                    "        if (localBoolVarA != null)\n",
                    "        if (true)\n"
        );

        final String expectedSourceAfterTarget = new StringBuilder().append("")
            .append("            return;\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final List<IfStmt> expectedIfStmts = expectedTargetSources.stream()
            .map(s -> { 
                return new StringBuilder()
                    .append(expectedSourceBeforeTarget)
                    .append(s)
                    .append(expectedSourceAfterTarget)
                    .toString();
            })
            .map(s -> { 
                return JavaParser.parse(s).findFirst(IfStmt.class).get();
            })
            .collect(Collectors.toList());

        final List<Expression> expectedCondExpressions = expectedIfStmts.stream()
            .map(s -> s.getCondition())
            .collect(Collectors.toList());

        assertThat(actualCondExpressions).containsOnlyElementsOf(expectedCondExpressions);
    }
}