package jp.posl.jprophet.operation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class CtrlFlowIntroductionOperationTest {
    /**
     * returnをするifブロックが挿入されているかテスト
     */
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
            .append("if (true)\n")
            .append("    return;")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (fa == null)\n")
            .append("    return;")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (fa != null)\n")
            .append("    return;")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (la == null)\n")
            .append("    return;")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (la != null)\n")
            .append("    return;")
            .toString()
        );

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new CtrlFlowIntroductionOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }

    /**
     * 返り値の型がVoidではない関数でreturnのパッチが生成されないか
     */
    @Test public void testReturnIsNotInsertedInNonVoidFunction(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private String ma() {\n")
            .append("        String la = \"b\";\n")
            .append("        fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();


        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new CtrlFlowIntroductionOperation());
        assertThat(actualSources.size()).isEqualTo(0);
    }


    /**
     * for文内でbreakを行うifブロックが挿入されているかテスト
     */
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
            .append("if (true)\n")
            .append("    return;")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (true)\n")
            .append("    break;")
            .toString()
        );

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new CtrlFlowIntroductionOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }

    /**
     * while文内でbreakを行うifブロックが挿入されているかテスト
     */
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
            .append("if (true)\n")
            .append("    return;")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (true)\n")
            .append("    break;")
            .toString()
        );

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new CtrlFlowIntroductionOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }

    /**
     * switch文がある時にエラーが起きないかテスト
     * 特にtargetNodeがSwitchEntryStmtの時
     */
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


        OperationTest operationTest = new OperationTest();
        operationTest.applyOperation(targetSource, new CtrlFlowIntroductionOperation());
    }
}