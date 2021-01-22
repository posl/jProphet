package jp.posl.jprophet.operation;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

public class MethodReplacementOperationTest {
    /**
     * メソッドと引数を置換する機能のテスト
     */
    @Test public void testForMethodReplace(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private String fb = \"b\";\n")
            .append("    private String fc = \"c\";\n")
            .append("    private void ma() {\n")
            .append("        this.ma(\"hoge\", \"fuga\");\n")
            .append("    }\n")
            .append("    private void mb(String a) {\n")
            .append("    }\n")
            .append("    private void mc() {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = List.of(
            "this.mb(fa)",
            "this.mb(this.fb)",
            "this.mb(this.fc)",
            "this.mc()"
        );

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new MethodReplacementOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }

    @Test public void testFor(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private int fb = 0;\n")
            .append("    private String ma() {\n")
            .append("        this.ma(\"hoge\", \"fuga\");\n")
            .append("    }\n")
            .append("    private String mb() {\n")
            .append("    }\n")
            .append("    private int mc() {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        // final List<String> expectedSources = List.of(
        //     "this.mb(\"hoge\", \"fuga\")",
        //     "this.mb(this.fa, \"fuga\")",
        //     "this.mb(\"hoge\", this.fa)"
        // );

        final List<String> expectedSources = List.of(
            "this.mb()"
        );

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new MethodReplacementOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }
}