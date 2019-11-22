package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.ThisExpr;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VariableReplacementOperationTest{
    

    /**
     * 引数を変数に置換する機能のテスト
     */
    @Test public void testForArgumentReplace(){
        final String targetStatement = 
                "       this.mb(\"hoge\", \"fuga\");\n";
        final String beforeTargetStatement = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private String fb = \"a\";\n\n")
            .append("    private void ma(String pa, String pb) {\n")
            .append("        String la = \"b\";\n")
            .toString();

        final String afterTargetStatement = new StringBuilder().append("")
            .append("    }\n\n")
            .append("    private void mb(String a, String b) {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String targetSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
            .append(targetStatement)
            .append(afterTargetStatement)
            .toString();

        List<String> expectedTargetSources = new ArrayList<String>();
        expectedTargetSources.add("        this.mb(this.fa, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", this.fa);\n");
        expectedTargetSources.add("        this.mb(this.fb, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", this.fb);\n");
        expectedTargetSources.add("        this.mb(la, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", la);\n");
        expectedTargetSources.add("        this.mb(pa, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", pa);\n");
        expectedTargetSources.add("        this.mb(pb, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", pb);\n");

        List<String> expectedSources = expectedTargetSources.stream()
            .map(str -> {
                return new StringBuilder().append("")
                    .append(beforeTargetStatement)
                    .append(str)
                    .append(afterTargetStatement)
                    .toString();
            })
            .collect(Collectors.toList());

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation();
            candidateSources.addAll(vr.exec(node).stream()
                .map(cu -> cu.toString())
                .collect(Collectors.toList())
            );
        }
        assertThat(candidateSources).containsOnlyElementsOf(expectedSources);
        return;
    }

    /**
     * 代入文の左辺をプログラム中の変数で置換できるかテスト 
     */
    @Test public void testForAssignmentReplace(){
        final String targetStatement = 
                "       la = \"hoge\";\n"; 
        final String beforeTargetStatement = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String lb = \"b\";\n")
            .toString();

        final String afterTargetStatement = new StringBuilder().append("")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String targetSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
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
                    .append(str)
                    .append(afterTargetStatement)
                    .toString();
            })
            .collect(Collectors.toList());

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation();
            candidateSources.addAll(vr.exec(node).stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }

        assertThat(candidateSources).containsOnlyElementsOf(expectedSources);
        return;
    }

    /**
     * クラス外のステートメントに対して正常に動作するかテスト
     */
    @Test public void testForWhenThereIsNoReplacement(){
        final String sourceThatHasNothingToReplace = new StringBuilder().append("")
        .append("import java.util.List;\n")
        .toString();

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(sourceThatHasNothingToReplace);
        List<Node> candidates = new ArrayList<Node>();
        for(Node node : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation();
            candidates.addAll(vr.exec(node));
        }

        assertThat(candidates.size()).isZero();
        return;
    }

    /**
     * 生成した修正パッチ候補に元のステートメントと同じものが含まれていないことをテスト
     */
    @Test public void testThatCandidatesDoesNotContainOriginalInAssignExpr(){
        final String targetStatement = 
                "       la = lb;\n"; 

        final String source = new StringBuilder().append("")
        .append("public class A {\n\n")
        .append("    private void ma() {\n")
        .append("        String la = \"a\";\n")
        .append("        String lb = \"b\";\n")
        .append(targetStatement)
        .append("    }\n")
        .append("}\n")
        .toString();

        final String expectedTargetStatement = 
                "        la = lb;\n"; 
        final String expectedSource = new StringBuilder().append("")
        .append("public class A {\n\n")
        .append("    private void ma() {\n")
        .append("        String la = \"a\";\n")
        .append("        String lb = \"b\";\n")
        .append(expectedTargetStatement) 
        .append("    }\n")
        .append("}\n")
        .toString();

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(source);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation();
            candidateSources.addAll(vr.exec(node).stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }

        assertThat(candidateSources).doesNotContain(expectedSource);
        return;
    }

    /**
     * 変化のない置換が行われていないかテスト
     */
    @Test public void testThatCandidatesDoesNotContainOriginalInArgs(){
        final String targetStatement = 
                "       hoge(la);\n"; 

        final String source = new StringBuilder().append("")
        .append("public class A {\n")
        .append("   private void ma() {\n")
        .append("       String la = \"a\";\n")
        .append("       String lb = \"b\";\n")
        .append(targetStatement)
        .append("   }\n")
        .append("}\n")
        .toString();

        final String targetStatementAsRepairUnitToString = "hoge(la)"; 

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(source);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation();
            candidateSources.addAll(vr.exec(node).stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }

        assertThat(candidateSources).doesNotContain(targetStatementAsRepairUnitToString);
        return;
    }

    /**
     * 置換先候補について基本型に対応しているかテスト  
     * 基本型に対応していないバグがあったので追加
     */
    @Test public void testForCollectPrimitiveType(){
        final String targetStatement = 
                "        hoge(0);\n"; 

        final String beforeTargetStatement = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private void ma() {\n")
            .append("        int la = 1;\n")
            .toString();

        final String afterTargetStatement = new StringBuilder().append("")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String targetSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
            .append(targetStatement)
            .append(afterTargetStatement)
            .toString();


        final String expectedSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
            .append("        hoge(la);\n")
            .append(afterTargetStatement)
            .toString();

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation();
            candidateSources.addAll(vr.exec(node).stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }

        assertThat(candidateSources).contains(expectedSource);
        return;
    }
}