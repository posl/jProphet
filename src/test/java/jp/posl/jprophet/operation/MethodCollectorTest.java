package jp.posl.jprophet.operation;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ThisExpr;


public class MethodCollectorTest {
    @Test public void testCollectFields() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private void ma() {\n")
            .append("        this.mb();\n")
            .append("    }\n\n")
            .append("    private void mb() {\n")
            .append("    }\n\n")
            .append("    private void mc() {\n")
            .append("    }\n\n")
            .append("}\n\n")
            .toString();
        
        final CompilationUnit cu = JavaParser.parse(targetSource);
        List<String> expectedMethodNames = List.of(
            "ma",
            "mb",
            "mc"
        );

        MethodCollector collector = new MethodCollector();
        final ThisExpr targetThisExpr = (ThisExpr)(cu.findFirst(ThisExpr.class).get());
        List<String> actualMethodNames = collector.collectMethodName(targetThisExpr);

        assertThat(actualMethodNames).containsOnlyElementsOf(expectedMethodNames);
    }
}