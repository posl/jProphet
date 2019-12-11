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

public class MethodReplacementOperationTest {
    /**
     * 引数を変数に置換する機能のテスト
     */
    @Test public void testForArgumentReplace(){
        final String beforeTargetStatement = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma(String pa, String pb) {\n")
            .toString();
        final String targetStatement = 
                    "        this.ma(\"hoge\", \"fuga\");\n";
        final String afterTargetStatement = new StringBuilder().append("")
            .append("    }\n")
            .append("    private void mb(String a, String b) {\n")
            .append("    }\n")
            .append("    private void mc(String a, String b) {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String targetSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
            .append(targetStatement)
            .append(afterTargetStatement)
            .toString();

        final List<String> expectedFixedStatements = List.of(
                    "        this.mb(\"hoge\", \"fuga\");\n",
                    "        this.mc(\"hoge\", \"fuga\");\n"
        );

        final List<String> expectedSources = expectedFixedStatements.stream()
            .map(expectedTargetStatement -> {
                return new StringBuilder().append("")
                    .append(beforeTargetStatement)
                    .append(expectedTargetStatement)
                    .append(afterTargetStatement)
                    .toString();
            }).collect(Collectors.toList());

        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<String> actualSources = new ArrayList<String>();
        for(Node node : nodes){
            final List<CompilationUnit> cUnits = new MethodReplacementOperation().exec(node);
            for (CompilationUnit cUnit : cUnits){
                LexicalPreservingPrinter.setup(cUnit);
                actualSources.add(LexicalPreservingPrinter.print(cUnit));
            }
        }
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
        return;
    }

}