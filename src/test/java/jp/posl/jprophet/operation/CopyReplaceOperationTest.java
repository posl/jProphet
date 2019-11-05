package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.RepairUnit;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CopyReplaceOperationTest{
    

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
        .append("       int k = 3;\n")
        .append("       if (k == 5) {\n")
        .append("           k = 5;\n")
        .append("       }\n")
        .append(targetStatement)
        .append("       int n = 5;\n")
        .append("       if (n == 5) {\n")
        .append("           n = 3;\n")
        .append("       }\n")
        .append("       String ld = \"d\";\n")
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
            CopyReplaceOperation cr = new CopyReplaceOperation(repairUnit);
            List<RepairUnit> candi = cr.exec();
            System.out.println(candi);
        }
        return;
        
    }

}