package jp.posl.jprophet.operation;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;


public class DeclarationCollectorTest {
    @Test public void testForCollectFields() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private String fb = \"a\";\n\n")
            .append("    private void ma() {\n")
            .append("        hoge();\n")
            .append("    }\n\n")
            .append("}\n\n")
            .toString();
        
        final CompilationUnit cu = JavaParser.parse(targetSource);
        List<VariableDeclarator> expectedFieldDeclarations = cu.findAll(VariableDeclarator.class);

        DeclarationCollector collector = new DeclarationCollector();
        final Node targetNode = cu.findFirst(MethodCallExpr.class).get();
        List<VariableDeclarator> actualFieldDeclarations = collector.collectFileds(targetNode);

        assertThat(actualFieldDeclarations).containsAll(expectedFieldDeclarations);
    }

    @Test public void testForCollectLocalVars() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private void ma() {\n")
            .append("        String la = \"a\";\n\n")
            .append("        String lb = \"b\";\n\n")
            .append("        hoge();\n")
            .append("    }\n\n")
            .append("}\n\n")
            .toString();
        
        final CompilationUnit cu = JavaParser.parse(targetSource);
        final Node targetNode = cu.findFirst(MethodCallExpr.class).get();
        List<VariableDeclarator> expectedLocalVarDeclarations = cu.findAll(VariableDeclarator.class);

        DeclarationCollector collector = new DeclarationCollector();
        List<VariableDeclarator> actualLocalVarDeclarations = collector.collectLocalVars(targetNode);

        assertThat(actualLocalVarDeclarations).containsAll(expectedLocalVarDeclarations);
    }

    @Test public void testForCollectParameters() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private void ma(String pa, String pb) {\n")
            .append("        hoge();\n")
            .append("    }\n\n")
            .append("}\n\n")
            .toString();
        
        final CompilationUnit cu = JavaParser.parse(targetSource);
        final Node targetNode = cu.findFirst(MethodCallExpr.class).get();
        List<Parameter> expectedParameters = cu.findAll(Parameter.class);

        DeclarationCollector collector = new DeclarationCollector();
        List<Parameter> actualParameters = collector.collectParameters(targetNode);

        assertThat(actualParameters).containsAll(expectedParameters);
    }
}