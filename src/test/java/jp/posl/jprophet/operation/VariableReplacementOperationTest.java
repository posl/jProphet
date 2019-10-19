package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.RepairUnit;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class VariableReplacementOperationTest{
    private final String sourceForAssignmentReplace = new StringBuilder().append("")
    .append("public class A {\n")
    .append("   private String fa = \"a\";\n")
    .append("   private void ma() {\n")
    .append("       String la = \"b\";\n")
    .append("       String lb = \"c\";\n")
    .append("       this.fa = lb;\n")
    .append("   }\n")
    .append("}\n")
    .toString();

    private final String sourceForArgumentReplace = new StringBuilder().append("")
    .append("public class A {\n")
    .append("   private String fa = \"a\";\n")
    .append("   private String fb = \"a\";\n")
    .append("   private String fc = \"a\";\n")
    .append("   private void ma() {\n")
    .append("       String la = \"b\";\n")
    .append("       this.mb(fa, fb);\n")
    .append("       this.mb(fa);\n")
    .append("   }\n")
    .append("   private void mb(String a) {\n")
    .append("   }\n")
    .append("}\n")
    .toString();

    private final String sourceThatHasNothingToReplace = new StringBuilder().append("")
    .append("import java.util.List;\n")
    .append("public class A {\n")
    .append("}\n")
    .toString();


    @Test public void testForArgumentReplace(){
        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(this.sourceForArgumentReplace);
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        for(RepairUnit repairUnit : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation(repairUnit);
            candidates.addAll(vr.exec());
        }
        return;
    }

    @Test public void testForAssignmentReplace(){
        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(this.sourceForAssignmentReplace);
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        for(RepairUnit repairUnit : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation(repairUnit);
            candidates.addAll(vr.exec());
        }
        return;
    }

    @Test public void testForWhenThereIsNoReplacement(){
        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(this.sourceThatHasNothingToReplace);
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        for(RepairUnit repairUnit : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation(repairUnit);
            candidates.addAll(vr.exec());
        }
        return;
    }

}