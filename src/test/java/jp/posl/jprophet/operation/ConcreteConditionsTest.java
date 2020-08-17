package jp.posl.jprophet.operation;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;

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

        final CompilationUnit cu = JavaParser.parse(targetSource);
        final List<VariableDeclarator> varDeclarations = cu.findAll(VariableDeclarator.class);
        final List<Parameter> parameters = cu.findAll(Parameter.class);

        final ConcreteConditions concreteConditions = new ConcreteConditions(varDeclarations, parameters);

        final List<String> expectedCondExpressions = List.of(
                    "fieldBoolVarA == true",
                    "fieldBoolVarA == false",
                    "localBoolVarA == true",
                    "localBoolVarA == false",
                    "localObjectA == null",
                    "localObjectA != null",
                    "fieldBoolVarA == null",
                    "fieldBoolVarA != null",
                    "localBoolVarA == null",
                    "localBoolVarA != null",
                    "true"
        ); 

        List<String> actualCondExpressions = concreteConditions.getExpressions().stream()
            .map(expr -> expr.toString())
            .collect(Collectors.toList());

        assertThat(actualCondExpressions).containsOnlyElementsOf(expectedCondExpressions);
    }
}