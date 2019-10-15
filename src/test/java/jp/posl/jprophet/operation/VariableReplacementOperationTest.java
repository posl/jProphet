package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.RepairUnit;
import static org.assertj.core.api.Assertions.*;
import java.util.List;

public class VariableReplacementOperationTest{
    private final String sourceForFieldReplacement = new StringBuilder().append("")
    .append("public class A {\n")
    .append("   private String fa = \"a\";\n")
    .append("   private String fb = \"b\";\n")
    .append("   private void ma() {\n")
    .append("       this.fa = 'hoge';\n")
    .append("   }\n")
    .append("}\n")
    .toString();

    private final String sourceForLocalVarReplacement = new StringBuilder().append("")
    .append("public class A {\n")
    .append("   private String fa = \"a\";\n")
    .append("   private void ma() {\n")
    .append("       String la = \"b\";\n")
    .append("       String lb = \"c\";\n")
    .append("       this.fa = lb;\n")
    .append("   }\n")
    .append("}\n")
    .toString();

    private final String sourceForArgumentReplacement = new StringBuilder().append("")
    .append("public class A {\n")
    .append("   private String fa = \"a\";\n")
    .append("   private String fb = \"a\";\n")
    .append("   private void ma() {\n")
    .append("       String la = \"b\";\n")
    .append("       this.mb(a, b);\n")
    .append("   }\n")
    .append("   private void mb(String a) {\n")
    .append("   }\n")
    .append("}\n")
    .toString();

    private final String source = new StringBuilder().append("")
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
        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(this.sourceForArgumentReplacement);
        VariableReplacementOperation op = new VariableReplacementOperation();
        List<RepairUnit> candidates = op.exec(repairUnits.get(28));
        return;
    }
}