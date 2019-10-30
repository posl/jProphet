package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.RepairUnit;
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

        final String source = new StringBuilder().append("")
        .append("public class A {\n")
        .append("   private String fa = \"a\";\n")
        .append("   private String fb = \"a\";\n")
        .append("   private void ma(String pa, String pb) {\n")
        .append("       String la = \"b\";\n")
        .append(targetStatement)
        .append("   }\n")
        .append("   private void mb(String a, String b) {\n")
        .append("   }\n")
        .append("}\n")
        .toString();

        List<String> expectedTargetSources = new ArrayList<String>();
        expectedTargetSources.add("this.mb(this.fa, \"fuga\")");
        expectedTargetSources.add("this.mb(\"hoge\", this.fa)");
        expectedTargetSources.add("this.mb(this.fb, \"fuga\")");
        expectedTargetSources.add("this.mb(\"hoge\", this.fb)");
        expectedTargetSources.add("this.mb(la, \"fuga\")");
        expectedTargetSources.add("this.mb(\"hoge\", la)");
        expectedTargetSources.add("this.mb(pa, \"fuga\")");
        expectedTargetSources.add("this.mb(\"hoge\", pa)");
        expectedTargetSources.add("this.mb(pb, \"fuga\")");
        expectedTargetSources.add("this.mb(\"hoge\", pb)");

        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(source);
        List<String> candidateSources = new ArrayList<String>();
        for(RepairUnit repairUnit : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation(repairUnit);
            candidateSources.addAll(vr.exec().stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }
        assertThat(candidateSources).containsOnlyElementsOf(expectedTargetSources);
        return;
    }

    /**
     * 代入文の左辺をプログラム中の変数で置換できるかテスト 
     */
    @Test public void testForAssignmentReplace(){
        final String targetStatement = 
                "       la = \"hoge\";\n"; 

        final String source = new StringBuilder().append("")
        .append("public class A {\n")
        .append("   private String fa = \"a\";\n")
        .append("   private void ma(String pa) {\n")
        .append("       String la = \"a\";\n")
        .append("       String lb = \"b\";\n")
        .append(targetStatement)
        .append("   }\n")
        .append("}\n")
        .toString();

        List<String> expectedTargetSources = new ArrayList<String>();
        expectedTargetSources.add("la = la");
        expectedTargetSources.add("la = lb");
        expectedTargetSources.add("la = this.fa");
        expectedTargetSources.add("la = pa");

        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(source);
        List<String> candidateSources = new ArrayList<String>();
        for(RepairUnit repairUnit : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation(repairUnit);
            candidateSources.addAll(vr.exec().stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }

        assertThat(candidateSources).containsOnlyElementsOf(expectedTargetSources);
        return;
    }

    /**
     * クラス外のステートメントに対して正常に動作するかテスト
     */
    @Test public void testForWhenThereIsNoReplacement(){
        final String sourceThatHasNothingToReplace = new StringBuilder().append("")
        .append("import java.util.List;\n")
        .toString();

        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(sourceThatHasNothingToReplace);
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        for(RepairUnit repairUnit : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation(repairUnit);
            candidates.addAll(vr.exec());
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
        .append("public class A {\n")
        .append("   private void ma() {\n")
        .append("       String la = \"a\";\n")
        .append("       String lb = \"b\";\n")
        .append(targetStatement)
        .append("   }\n")
        .append("}\n")
        .toString();

        final String targetStatementAsRepairUnitToString = "la = lb"; 

        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(source);
        List<String> candidateSources = new ArrayList<String>();
        for(RepairUnit repairUnit : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation(repairUnit);
            candidateSources.addAll(vr.exec().stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }

        assertThat(candidateSources).doesNotContain(targetStatementAsRepairUnitToString);
        return;
    }

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

        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(source);
        List<String> candidateSources = new ArrayList<String>();
        for(RepairUnit repairUnit : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation(repairUnit);
            candidateSources.addAll(vr.exec().stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }

        assertThat(candidateSources).doesNotContain(targetStatementAsRepairUnitToString);
        return;
    }
}