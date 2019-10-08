package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.RepairUnit;
import static org.assertj.core.api.Assertions.*;
import java.util.List;

public class ValueReplacementOperationTest{
    private final String sourceMethod = new StringBuilder().append("")
    .append("public class A {\n")
    .append("   private void ma() {\n")
    .append("       this.mb();\n")
    .append("   }\n")
    .append("   private void mb() {\n")
    .append("   }\n")
    .append("   private void mc() {\n")
    .append("   }\n")
    .append("}\n")
    .toString();

    private final String sourceValue = new StringBuilder().append("")
    .append("public class A {\n")
    .append("   private String ma = \"a\";\n")
    .append("   private String mb = \"b\";\n")
    .append("   private void ma() {\n")
    .append("       String lb = \"b\";\n")
    .append("       String lc = \"c\";\n")
    .append("       this.ma = lb;\n")
    .append("   }\n")
    .append("}\n")
    .toString();
    @Test public void test(){
        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(this.sourceValue);
        ValueReplacementOperation op = new ValueReplacementOperation();
        List<RepairUnit> candidates = op.exec(repairUnits.get(23));
        return;
    }
}