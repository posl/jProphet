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
     * if文が含まれる場合のテスト
     */
    @Test public void testForIfStatementCopy(){

        final String beforeTargetStatement = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String lb = \"b\";\n")
            .toString();
        
        final String statementToBeCopied = 
            "        la = \"hoge\";\n";

        final String targetStatement = new StringBuilder().append("")
            .append("        if (method() && method2()) {\n")
            .append("            lb = \"huga\";\n")
            .append("        }\n")
            .toString();

        final String afterTargetStatement = new StringBuilder().append("")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String targetSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
            .append(statementToBeCopied)
            .append(targetStatement)
            .append(afterTargetStatement)
            .toString();


        List<String> expectedTargetSources = new ArrayList<String>();
        expectedTargetSources.add("        la = la;\n");
        expectedTargetSources.add("        la = lb;\n");
        expectedTargetSources.add("        la = this.fa;\n");
        expectedTargetSources.add("        la = pa;\n");

        List<String> expectedSources = expectedTargetSources.stream()
            .map(str -> {
                return new StringBuilder().append("")
                    .append(beforeTargetStatement)
                    .append(statementToBeCopied)
                    .append(str)
                    .append(targetStatement)
                    .append(afterTargetStatement)
                    .toString();
            })
            .collect(Collectors.toList());

        expectedSources.addAll(expectedTargetSources.stream()
            .map(str -> {
                return new StringBuilder().append("")
                    .append(beforeTargetStatement)
                    .append(statementToBeCopied)
                    .append("        if (true) {\n")
                    .append("    " + str)
                    .append("            lb = \"huga\";\n")
                    .append("        }\n")
                    .append(afterTargetStatement)
                    .toString();
            })
            .collect(Collectors.toList()));

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
        //assertThat(candidateSources).containsOnlyElementsOf(expectedSources);
        return;
    }
}