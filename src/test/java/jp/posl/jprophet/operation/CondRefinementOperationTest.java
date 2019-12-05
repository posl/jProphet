package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CondRefinementOperationTest{

    /**
     * 条件文が置換されているかテスト
     */
    @Test public void testForConditionReplacement(){

        final String beforeTargetStatement = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma() {\n")
            .append("        String lb = \"b\";\n")
            .toString();

        final String targetStatement = new StringBuilder().append("")
            .append("        if (method1() && method2()) {\n")
            .toString();

        final String afterTargetStatement = new StringBuilder().append("")
            .append("            lb = \"huga\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String targetSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
            .append(targetStatement)
            .append(afterTargetStatement)
            .toString();


        List<String> expectedTargetSources = new ArrayList<String>();
        expectedTargetSources.add("        if ((method1() && method2()) || (fa == null)) {\n");
        expectedTargetSources.add("        if ((method1() && method2()) || (fa != null)) {\n");
        expectedTargetSources.add("        if ((method1() && method2()) || (lb == null)) {\n");
        expectedTargetSources.add("        if ((method1() && method2()) || (lb != null)) {\n");
        expectedTargetSources.add("        if ((method1() && method2()) || (true)) {\n");
        expectedTargetSources.add("        if ((method1() && method2()) && !(fa == null)) {\n");
        expectedTargetSources.add("        if ((method1() && method2()) && !(fa != null)) {\n");
        expectedTargetSources.add("        if ((method1() && method2()) && !(lb == null)) {\n");
        expectedTargetSources.add("        if ((method1() && method2()) && !(lb != null)) {\n");
        expectedTargetSources.add("        if ((method1() && method2()) && !(true)) {\n");


        List<String> expectedSources = expectedTargetSources.stream()
            .map(str -> {
                return new StringBuilder().append("")
                    .append(beforeTargetStatement)
                    .append(str)
                    .append(afterTargetStatement)
                    .toString();
            })
            .collect(Collectors.toList());

        List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : nodes){
            CondRefinementOperation cr = new CondRefinementOperation();
            List<CompilationUnit> cUnits = cr.exec(node);
            for (CompilationUnit cUnit : cUnits){
                LexicalPreservingPrinter.setup(cUnit);
                candidateSources.add(LexicalPreservingPrinter.print(cUnit));
            }
        }
        assertThat(candidateSources).containsOnlyElementsOf(expectedSources);
        return;
    }
}