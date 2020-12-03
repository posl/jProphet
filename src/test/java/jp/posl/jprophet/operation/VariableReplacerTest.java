package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;



public class VariableReplacerTest {

    //置換対象の変数名が正しく取得できている前提でテストする
    final List<String> fieldNames = List.of("fa");
    final List<String> localVarNames = List.of("la");
    final List<String> parameterNames = List.of("pa");

    @Test public void testForReplacementAssignExpr() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa;\n")
            .append("   private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String hoge;\n") 
            .append("        hoge = \"b\";\n")  //Target
            .append("   }\n")
            .append("}\n")
            .toString();
        final List<String> expectedExpressions = List.of(
            "hoge = this.fa",
            "hoge = la",
            "hoge = pa");
        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<Node> replacedNodes = new ArrayList<Node>();
        for(Node node : nodes){
            final VariableReplacer vr = new VariableReplacer();
            final List<Node> results = vr.replaceAllVariables(
                    node, fieldNames, localVarNames, parameterNames);
            replacedNodes.addAll(results);
        }
        final List<String> actualExpressions = replacedNodes.stream().map(n -> n.toString()).collect(Collectors.toList());
        assertThat(actualExpressions).containsOnlyElementsOf(expectedExpressions);
    }

    @Test public void testForReplacementArgs() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa;\n")
            .append("   private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        ma(\"b\");\n") //Target
            .append("   }\n")
            .append("}\n")
            .toString();
        final List<String> expectedExpressions = List.of(
            "ma(this.fa)",
            "ma(la)",
            "ma(pa)");
        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<Node> replacedNodes = new ArrayList<Node>();
        for(Node node : nodes){
            final VariableReplacer vr = new VariableReplacer();
            final List<Node> results = vr.replaceAllVariables(
                    node, fieldNames, localVarNames, parameterNames);
            replacedNodes.addAll(results);
        }
        final List<String> actualExpressions = replacedNodes.stream().map(n -> n.toString()).collect(Collectors.toList());
        assertThat(actualExpressions).containsOnlyElementsOf(expectedExpressions);
    }

    @Test public void testForReplacementNameExprInIfCondition() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa;\n")
            .append("   private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String hoge = \"b\";\n")
            .append("        if (hoge == \"b\") return;\n") //Target
            .append("   }\n")
            .append("}\n")
            .toString();
        final List<String> expectedExpressions = List.of(
            "if (this.fa == \"b\")\n    return;",
            "if (la == \"b\")\n    return;",
            "if (pa == \"b\")\n    return;");
        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<Node> replacedNodes = new ArrayList<Node>();
        for(Node node : nodes){
            final VariableReplacer vr = new VariableReplacer();
            final List<Node> results = vr.replaceAllVariables(
                    node, fieldNames, localVarNames, parameterNames);
            replacedNodes.addAll(results);
        }
        final List<String> actualExpressions = replacedNodes.stream().map(n -> n.toString()).collect(Collectors.toList());
        assertThat(actualExpressions).containsOnlyElementsOf(expectedExpressions);
    }

    @Test public void testForReplacementVarInReturnStmt() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa;\n")
            .append("   private String ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        return \"b\";\n")  //Target
            .append("   }\n")
            .append("}\n")
            .toString();
        final List<String> expectedStatements = List.of(
            "return this.fa;",
            "return la;",
            "return pa;");
        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<Node> replacedNodes = new ArrayList<Node>();
        for(Node node : nodes){
            final VariableReplacer vr = new VariableReplacer();
            final List<Node> results = vr.replaceAllVariables(
                    node, fieldNames, localVarNames, parameterNames);
            replacedNodes.addAll(results);
        }
        final List<String> actualStatements = replacedNodes.stream().map(n -> n.toString()).collect(Collectors.toList());
        assertThat(actualStatements).containsOnlyElementsOf(expectedStatements);
    }

}