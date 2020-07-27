package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


public class CtrlFlowIntroductionOperationTest {
    /**
     * returnをするifブロックが挿入されているかテスト
     */
    /*
    @Test public void testForAddReturn(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma() {\n")
            .append("        String la = \"b\";\n")
            .append("        fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n") //toString(PrettyPrinter)での出力ではクラス宣言の先頭行の後に空行が入る仕様 
            .append("    private String fa = \"a\";\n") //ここも
            .append("    private void ma() {\n")
            .append("        if (true)\n")
            .append("            return;\n")
            .append("        String la = \"b\";\n")
            .append("        fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma() {\n")
            .append("        String la = \"b\";\n")
            .append("        if (true)\n")
            .append("            return;\n")
            .append("        fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString()
        );


        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<String> candidateSources = new ArrayList<String>();
        for(Node node : nodes){
            final CtrlFlowIntroductionOperation cfo = new CtrlFlowIntroductionOperation();
            final List<CompilationUnit> cUnits = cfo.exec(node);
            for (CompilationUnit cUnit : cUnits){
                LexicalPreservingPrinter.setup(cUnit);
                candidateSources.add(LexicalPreservingPrinter.print(cUnit));
            }
        }
        assertThat(candidateSources).containsAll(expectedSources);
        return;
    }

    /**
     * for文内でbreakを行うifブロックが挿入されているかテスト
     */
    /*
    @Test public void testForAddBreakInForLoop(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            if (true)\n")
            .append("                break;\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString());

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            if (true)\n")
            .append("                return;\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString());

        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<String> candidateSources = new ArrayList<String>();
        for(Node node : nodes) {
            final CtrlFlowIntroductionOperation cfo = new CtrlFlowIntroductionOperation();
            final List<CompilationUnit> cUnits = cfo.exec(node);
            for (CompilationUnit cUnit : cUnits){
                LexicalPreservingPrinter.setup(cUnit);
                candidateSources.add(LexicalPreservingPrinter.print(cUnit));
            }
        }
        assertThat(candidateSources).containsAll(expectedSources);
        return;
    }

    /**
     * while文内でbreakを行うifブロックが挿入されているかテスト
     */
    /*
    @Test public void testForAddBreakInWhileLoop(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        while (true) {\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        while (true) {\n")
            .append("            if (true)\n")
            .append("                break;\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString());

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        while (true) {\n")
            .append("            if (true)\n")
            .append("                return;\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString());

        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<String> candidateSources = new ArrayList<String>();
        for(Node node : nodes) {
            final CtrlFlowIntroductionOperation cfo = new CtrlFlowIntroductionOperation();
            final List<CompilationUnit> cUnits = cfo.exec(node);
            for (CompilationUnit cUnit : cUnits){
                LexicalPreservingPrinter.setup(cUnit);
                candidateSources.add(LexicalPreservingPrinter.print(cUnit));
            }
        }
        assertThat(candidateSources).containsAll(expectedSources);
        return;
    }

    /**
     * switch文がある時にエラーが起きないかテスト
     * 特にtargetNodeがSwitchEntryStmtの時
     */
    /*
    @Test public void testForSwitch(){

        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private String fb = \"a\";\n")
            .append("    private void ma(String pa, String pb) {\n")
            .append("        String la = \"b\";\n")
            .append("        int num = 0;\n")
            .append("        switch (num) {\n")
            .append("        case 1:\n")
            .append("            num = 1;\n")
            .append("            break;\n")
            .append("        case 2:\n")
            .append("            num = 2;\n")
            .append("            break;\n")
            .append("        default:\n")
            .append("            num = 0;\n")
            .append("        }\n")
            .append("        la = \"d\";\n")
            .append("        this.mb(\"hoge\", \"fuga\");\n")
            .append("    }\n")
            .append("    private void mb(String a, String b) {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<String> candidateSources = new ArrayList<String>();
        for(Node node : nodes){
            final CtrlFlowIntroductionOperation cfo = new CtrlFlowIntroductionOperation();
            final List<CompilationUnit> cUnits = cfo.exec(node);
            for (CompilationUnit cUnit : cUnits){
                LexicalPreservingPrinter.setup(cUnit);
                candidateSources.add(LexicalPreservingPrinter.print(cUnit));
            }
        }
        return;
    }
    */
}