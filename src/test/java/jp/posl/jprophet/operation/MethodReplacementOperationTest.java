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
            .append("    private void ma() {\n")
            .append("        this.ma(\"hoge\", \"fuga\");\n")
            .append("    }\n")
            .append("    private void mb() {\n")
            .append("    }\n")
            .append("    private void mc() {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = List.of(
                    "this.mb(\"hoge\", \"fuga\")",
                    "this.mc(\"hoge\", \"fuga\")",
                    "this.mb(this.fa, \"fuga\")",
                    "this.mb(\"hoge\", this.fa)",
                    "this.mc(this.fa, \"fuga\")",
                    "this.mc(\"hoge\", this.fa)"
        );

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new MethodReplacementOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }
}