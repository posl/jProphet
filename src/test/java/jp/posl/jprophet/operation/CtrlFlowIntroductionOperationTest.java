package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.RepairUnit;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CtrlFlowIntroductionOperationTest {
    /**
     * returnをするifブロックが挿入されているかテスト
     */
    @Test public void testForAddReturn(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private void ma() {\n")
            .append("        String la = \"b\";\n")
            .append("        fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        List<String> expectedSources = new ArrayList<String>();

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n\n") //toString(PrettyPrinter)での出力ではクラス宣言の先頭行の後に空行が入る仕様 
            .append("    private String fa = \"a\";\n\n") //ここも
            .append("    private void ma() {\n")
            .append("        if (JPROPHET_ABST_HOLE)\n")
            .append("            return;\n")
            .append("        String la = \"b\";\n")
            .append("        fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private void ma() {\n")
            .append("        String la = \"b\";\n")
            .append("        if (JPROPHET_ABST_HOLE)\n")
            .append("            return;\n")
            .append("        fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString()
        );


        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(RepairUnit repairUnit : repairUnits){
            CtrlFlowIntroductionOperation vr = new CtrlFlowIntroductionOperation();
            candidateSources.addAll(vr.exec(repairUnit).stream()
                .map(ru -> ru.getCompilationUnit().toString()) 
                .collect(Collectors.toList())
            );
        }
        assertThat(candidateSources).containsAll(expectedSources);
        return;
    }

    /**
     * ループ構文内でbreakを行うifブロックが挿入されているかテスト
     */
    @Test public void testForAddBreakInLoop(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            if (JPROPHET_ABST_HOLE)\n")
            .append("                break;\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();


        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(RepairUnit repairUnit : repairUnits){
            CtrlFlowIntroductionOperation vr = new CtrlFlowIntroductionOperation();
            candidateSources.addAll(vr.exec(repairUnit).stream()
                .map(ru -> ru.getCompilationUnit().toString()) 
                .collect(Collectors.toList())
            );
        }
        assertThat(candidateSources).contains(expectedSource);
        return;
    }
}