package jp.posl.jprophet.operation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


public class VariableReplacementOperationTest{
    

    /**
     * 引数を変数に置換する機能のテスト
     */
    @Test public void testForArgumentReplace(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private String fb = \"a\";\n")
            .append("    private void ma(String pa, String pb) {\n")
            .append("        String la = \"b\";\n")
            .append("        this.mb(\"hoge\", \"fuga\");\n")
            .append("    }\n")
            .append("    private void mb(String a, String b) {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();
        expectedSources.add("this.mb(this.fa, \"fuga\")");
        expectedSources.add("this.mb(\"hoge\", this.fa)");
        expectedSources.add("this.mb(this.fb, \"fuga\")");
        expectedSources.add("this.mb(\"hoge\", this.fb)");
        expectedSources.add("this.mb(la, \"fuga\")");
        expectedSources.add("this.mb(\"hoge\", la)");
        expectedSources.add("this.mb(pa, \"fuga\")");
        expectedSources.add("this.mb(\"hoge\", pa)");
        expectedSources.add("this.mb(pb, \"fuga\")");
        expectedSources.add("this.mb(\"hoge\", pb)");

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new VariableReplacementOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }

    /**
     * 代入文の左辺をプログラム中の変数で置換できるかテスト 
     */
    @Test public void testForAssignmentReplace(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String lb = \"b\";\n")
            .append("        la = \"hoge\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();
        expectedSources.add("la = la");
        expectedSources.add("la = lb");
        expectedSources.add("la = this.fa");
        expectedSources.add("la = pa");

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new VariableReplacementOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }

    /**
     * クラス外のステートメントに対して正常に動作するかテスト
     */
    @Test public void testForWhenThereIsNoReplacement(){
        final String sourceThatHasNothingToReplace = new StringBuilder().append("")
        .append("import java.util.List;\n")
        .toString();

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(sourceThatHasNothingToReplace, new VariableReplacementOperation());
        assertThat(actualSources.size()).isZero();
        return;
    }

    /**
     * 生成した修正パッチ候補に元のステートメントと同じものが含まれていないことをテスト
     */
    @Test public void testThatCandidatesDoesNotContainOriginalInAssignExpr(){

        final String targetSource = new StringBuilder().append("")
        .append("public class A {\n")
        .append("    private void ma() {\n")
        .append("        String la = \"a\";\n")
        .append("        String lb = \"b\";\n")
        .append("        la = lb;\n")
        .append("    }\n")
        .append("}\n")
        .toString();

        final String expectedSource = "la = lb"; 

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new VariableReplacementOperation());
        assertThat(actualSources).doesNotContain(expectedSource);
        return;
    }

    /**
     * 変化のない置換が行われていないかテスト
     */
    @Test public void testThatCandidatesDoesNotContainOriginalInArgs(){

        final String targetSource = new StringBuilder().append("")
        .append("public class A {\n")
        .append("   private void ma() {\n")
        .append("       String la = \"a\";\n")
        .append("       String lb = \"b\";\n")
        .append("       hoge(la);\n")
        .append("   }\n")
        .append("}\n")
        .toString();

        final String expectedSource = "hoge(la)"; 

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new VariableReplacementOperation());
        assertThat(actualSources).doesNotContain(expectedSource);
    }

    /**
     * 置換先候補について基本型に対応しているかテスト  
     * 基本型に対応していないバグがあったので追加
     */
    @Test public void testForCollectPrimitiveType(){

        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        int la = 1;\n")
            .append("        hoge(0);\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String expectedSource = "hoge(la)";

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new VariableReplacementOperation());
        assertThat(actualSources).contains(expectedSource);
    }

    /**
     * If文の条件式中の比較文の変数を置換できるかテスト 
     */
    @Test public void testForReplaceBinExprInIfStmt(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String lb = \"b\";\n")
            .append("        if(la == lb) \n") 
            .append("            return;\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();
        expectedSources.add("lb == lb");
        expectedSources.add("this.fa == lb");
        expectedSources.add("pa == lb");
        expectedSources.add("la == la");
        expectedSources.add("la == this.fa");
        expectedSources.add("la == pa");

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new VariableReplacementOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }

    /**
     * If文の条件式中の比較文の変数を置換できるかテスト 
     */
    @Test public void testForVarReplaceInIfStmt(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String lb = \"b\";\n")
            .append("        if(la) \n") 
            .append("            return;\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();
        expectedSources.add("lb");
        expectedSources.add("this.fa");
        expectedSources.add("pa");

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new VariableReplacementOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }

    /**
     * returnされる変数を置換できるかテスト 
     */
    @Test public void testVarInReturnStmt(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private String ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String lb = \"b\";\n")
            .append("        return la;\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();
        expectedSources.add("return lb;");
        expectedSources.add("return this.fa;");
        expectedSources.add("return pa;");

        OperationTest operationTest = new OperationTest();
        final List<String> actualSources = operationTest.applyOperation(targetSource, new VariableReplacementOperation());
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
    }
}