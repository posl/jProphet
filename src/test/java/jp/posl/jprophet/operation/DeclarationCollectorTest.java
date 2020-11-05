package jp.posl.jprophet.operation;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;


public class DeclarationCollectorTest {
    
    @Test public void testCollectFields() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private String fb = \"a\";\n\n")
            .append("    private void ma() {\n")
            .append("        String la = \"a\";\n\n")
            .append("        hoge();\n")
            .append("    }\n\n")
            .append("}\n\n")
            .toString();
        
        final CompilationUnit cu = JavaParser.parse(targetSource);
        final List<VariableDeclarator> expectedFieldDeclarations = new ArrayList<VariableDeclarator>();
        cu.findAll(FieldDeclaration.class).stream()
            .map(f -> f.getVariables().stream().collect(Collectors.toList()))
            .forEach(expectedFieldDeclarations::addAll);

        final DeclarationCollector collector = new DeclarationCollector();
        final Node targetNode = cu.findFirst(MethodCallExpr.class).get();
        final List<VariableDeclarator> actualFieldDeclarations = collector.collectFileds(targetNode);

        assertThat(actualFieldDeclarations).containsOnlyElementsOf(expectedFieldDeclarations);
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
        final List<VariableDeclarator> expectedLocalVarDeclarations = cu.findAll(VariableDeclarator.class);

        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> actualLocalVarDeclarations = collector.collectLocalVarsDeclared(targetNode);

        assertThat(actualLocalVarDeclarations).containsOnlyElementsOf(expectedLocalVarDeclarations);
    }

    @Test public void testCollectLocalVarsExistsBeforeTargetNode() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private void ma() {\n")
            .append("        String la = \"a\";\n\n")
            .append("        hoge();\n")
            .append("        String lb = \"b\";\n\n")
            .append("    }\n\n")
            .append("}\n\n")
            .toString();
        
        final CompilationUnit cu = JavaParser.parse(targetSource);
        final Node targetNode = cu.findFirst(MethodCallExpr.class).get();
        final List<VariableDeclarator> expectedLocalVarDeclarations = cu.findAll(VariableDeclarator.class).stream()
            .filter(v -> v.getNameAsString().equals("la"))
            .collect(Collectors.toList());

        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> actualLocalVarDeclarations = collector.collectLocalVarsDeclared(targetNode);

        assertThat(actualLocalVarDeclarations).containsOnlyElementsOf(expectedLocalVarDeclarations);
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
        final List<Parameter> expectedParameters = cu.findAll(Parameter.class);

        final DeclarationCollector collector = new DeclarationCollector();
        final List<Parameter> actualParameters = collector.collectParameters(targetNode);

        assertThat(actualParameters).containsOnlyElementsOf(expectedParameters);
    }
}