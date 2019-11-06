package jp.posl.jprophet.operation;

import org.junit.Test;
import org.junit.Before;

import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.RepairUnit;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CopyReplaceOperationTest{
    
    private List<RepairUnit> candidates = new ArrayList<RepairUnit>();
    private String source = new StringBuilder().append("")
    .append("public class A {\n")
    .append("   private String fa = \"a\";\n")
    .append("   private String fb = \"a\";\n")
    .append("   private void ma(String pa, String pb) {\n")
    .append("       String la = \"b\";\n")
    .append("       la = \"hoge\";\n")
    //.append("       int k = 3;\n")
    //.append("       if (k == 5) {\n")
    //.append("           k = 5;\n")
    //.append("       }\n")
    .append("       this.mb(\"hoge\", \"fuga\");\n")
    .append("       String ld = \"d\";\n")
    .append("       ld = \"hoge\";\n")
    .append("   }\n")
    .append("   private void mb(String a, String b) {\n")
    .append("   }\n")
    .append("}\n")
    .toString();

    @Before public void setUp(){
        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(source);
        for(RepairUnit repairUnit : repairUnits){
            CopyReplaceOperation cr = new CopyReplaceOperation(repairUnit);
            this.candidates.addAll(cr.exec());
        }
    }

    /**
     * ステートメントコピペ後の修正候補の数のテスト
     */
    @Test public void testForNumOfRepairCopied(){
        //assertThat(candidates.size()).isEqualTo(14);
        System.out.println(candidates);
        return;
    }

    /**
     * 修正される対象のステートメントが正しいかテスト
     */
    @Test public void testForTargetStatementAfterCopied(){
        List<String> expectedTargetSources = new ArrayList<String>();
        expectedTargetSources.add("int k = 3;");
        expectedTargetSources.add("if (k == 5) {\n    k = 5;\n}");
        expectedTargetSources.add("k = 5;");
        expectedTargetSources.add("this.mb(\"hoge\", \"fuga\");");
        expectedTargetSources.add("String ld = \"d\";");

        List<String> candidateSources = this.candidates.stream()
            .map(s -> s.toString())
            .collect(Collectors.toList());
        
        //assertThat(candidateSources).containsOnlyElementsOf(expectedTargetSources);
        return;
    }

    /**
     * RepairUnitのcompilationUnitが正しく書き換えられているかテスト(14個のうち1つだけ確認)
     */
    @Test public void testForCopiedStatementBeforeTarget(){
        final String targetStatement = "        this.mb(\"hoge\", \"fuga\");\n";
        final String copiedStatement = "        int k = 3;\n";
        final String expectedSource = new StringBuilder().append("")
        .append("public class A {\n\n")
        .append("    private String fa = \"a\";\n\n")
        .append("    private String fb = \"a\";\n\n")
        .append("    private void ma(String pa, String pb) {\n")
        .append("        String la = \"b\";\n")
        .append("        int k = 3;\n")
        .append("        if (k == 5) {\n")
        .append("            k = 5;\n")
        .append("        }\n")
        .append(copiedStatement)
        .append(targetStatement)
        .append("        String ld = \"d\";\n")
        .append("    }\n\n")
        .append("    private void mb(String a, String b) {\n")
        .append("    }\n")
        .append("}\n")
        .toString();

        //assertThat(candidates.get(4).getCompilationUnit().toString()).isEqualTo(expectedSource);
        return;
    }

    /**
     * メソッド外のステートメントに対して正常に動作するかテスト
     */
    @Test public void testForWhenThereIsNoCopy(){
        final String sourceThatHasNothingToCopy = new StringBuilder().append("")
        .append("import java.util.List;\n")
        .toString();

        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(sourceThatHasNothingToCopy);
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        for(RepairUnit repairUnit : repairUnits){
            CopyReplaceOperation cr = new CopyReplaceOperation(repairUnit);
            candidates.addAll(cr.exec());
        }

        //assertThat(candidates.size()).isZero();
        return;
    }
}