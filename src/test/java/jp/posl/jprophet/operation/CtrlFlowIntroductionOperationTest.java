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
        .append("public class A {\n")
        .append("    private String fa = \"a\";\n")
        .append("    private void ma() {\n")
        .append("        String la = \"b\";\n")
        .append("        fa = \"b\";\n")
        .append("    }\n")
        .append("}\n")
        .toString();

        List<String> expectedSources = new ArrayList<String>();

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma() {\n")
            .append("        if (false)\n")
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
            .append("        if (false)\n")
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
        assertThat(candidateSources).containsOnlyElementsOf(expectedSources);
        return;
    }

}