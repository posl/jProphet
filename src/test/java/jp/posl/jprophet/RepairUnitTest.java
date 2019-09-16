package jp.posl.jprophet;

import jp.posl.jprophet.AstGenerator;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.List;

import com.github.javaparser.ast.expr.SimpleName;

public class RepairUnitTest {
    private String sourceCode;
    private List<RepairUnit> repairUnits;

    @Before public void setUpRepairUnits(){
        this.sourceCode = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        this.repairUnits = new AstGenerator().getAllRepairUnit(sourceCode);
    }

    @Test public void testForTargetIndex() {
        RepairUnit repairUnit = this.repairUnits.get(2); 
        RepairUnit copiedRepairUnit = RepairUnit.copy(repairUnit);

        assertThat(copiedRepairUnit.getTargetNodeIndex()).isEqualTo(2);
    }   


    @Test public void testForCompilationUnit() {
        RepairUnit repairUnit = this.repairUnits.get(0); 
        RepairUnit copiedRepairUnit = RepairUnit.copy(repairUnit);

        SimpleName methodName = repairUnit.getCompilationUnit().findFirst(SimpleName.class).get();
        SimpleName copiedMethodName = copiedRepairUnit.getCompilationUnit().findFirst(SimpleName.class).get();

        copiedMethodName.setIdentifier("test");
        assertThat(methodName.getIdentifier()).isEqualTo("A");
    }   

    @Test public void testForTargetNode() {
        RepairUnit repairUnit = this.repairUnits.get(2); 
        RepairUnit copiedRepairUnit = RepairUnit.copy(repairUnit);

        SimpleName copiedMethodName = copiedRepairUnit.getNode().findFirst(SimpleName.class).get();
        SimpleName copiedMethodNameFromCu = copiedRepairUnit.getCompilationUnit().findAll(SimpleName.class).get(1);

        copiedMethodName.setIdentifier("test");
        assertThat(copiedMethodNameFromCu.getIdentifier()).isEqualTo("test");
    }   
}