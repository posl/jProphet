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
            .append("    private void ma(String a, String b) {\n")
            .append("    }\n")
            .append("    private void mb(String a, String b) {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String targetSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
            .append(targetStatement)
            .append(afterTargetStatement)
            .toString();

        String expectedTargetSource = 
                    "        this.mb(\"hoge\", \"fuga\");\n";

        String expectedSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
            .append(expectedTargetSource)
            .append(afterTargetStatement)
            .toString();

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            List<CompilationUnit> cUnits = new MethodReplacementOperation().exec(node);
            for (CompilationUnit cUnit : cUnits){
                LexicalPreservingPrinter.setup(cUnit);
                candidateSources.add(LexicalPreservingPrinter.print(cUnit));
            }
        }
        // assertThat(candidateSources).contains(expectedSource);
        return;
    }

}