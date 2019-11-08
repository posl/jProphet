package jp.posl.jprophet;

import jp.posl.jprophet.NodeUtility;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import com.github.javaparser.ast.Node;

public class RepairUnitTest {
    private String sourceCode;
    private List<RepairUnit> repairUnits;

    /**
     * 入力用のソースコードからRepairUnitのリストを生成
     */
    @Before public void setUpRepairUnits(){
        this.sourceCode = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        this.repairUnits = new NodeUtility().getAllRepairUnit(sourceCode);
    }

    /**
     * copyメソッドによってRepairUnitクラスのtargetIndexフィールドがコピーされているかどうかテスト
     */
    @Test public void testForCopiedTargetIndex() {
        int expectedIndex = 2;
        RepairUnit repairUnit = this.repairUnits.get(expectedIndex); 
        RepairUnit copiedRepairUnit = RepairUnit.deepCopy(repairUnit);

        assertThat(copiedRepairUnit.getTargetNodeIndex()).isEqualTo(expectedIndex);
    }   

    /**
     * copyメソッドによってコピーされたRepairUnitインスタンスがコピー元と異なるインスタンスかテスト
     */
    @Test public void testForCopiedRepairUnit(){
        RepairUnit repairUnit = this.repairUnits.get(0); 
        RepairUnit copiedRepairUnit = RepairUnit.deepCopy(repairUnit);

        assertThat(repairUnit).isNotSameAs(copiedRepairUnit);
    }

    /**
     * copyメソッドによってCompilationUnitがディープコピーされているかテスト
     */
    @Test public void testForCopiedCompilationUnit() {
        RepairUnit repairUnit = this.repairUnits.get(0); 
        RepairUnit copiedRepairUnit = RepairUnit.deepCopy(repairUnit);

        Node nodeInCompilationUnit = repairUnit.getCompilationUnit().getChildNodes().get(0);
        Node copiedNodeInCompilationUnit = copiedRepairUnit.getCompilationUnit().getChildNodes().get(0);

        // コピーされたRepairUnit中のcompilationUnitがコピー元のものと異なる(object1 != object2)か
        assertThat(repairUnit.getCompilationUnit()).isNotSameAs(copiedRepairUnit.getCompilationUnit());
        // コピーされたRepairUnit中のcompilationUnit中のNodeがコピー元のものと異なる(object1 != object2)か
        assertThat(nodeInCompilationUnit).isNotSameAs(copiedNodeInCompilationUnit);
    }   

    /**
     * copyメソッドによってコピーされたtargetNodeが同じくコピーされた
     * compilationUnitの子ノードを参照しているかテスト 
     */
    @Test public void testForCopiedTargetNode() {
        RepairUnit copiedRepairUnit = RepairUnit.deepCopy(this.repairUnits.get(2));

        Node copiedTargetNode = copiedRepairUnit.getTargetNode();
        Node copiedTargetNodeFromCu = NodeUtility.findByLevelOrderIndex(copiedRepairUnit.getCompilationUnit(), 2).orElseThrow();
        Node copiedNodeFromTargetNode = copiedTargetNode.getChildNodes().get(0);
        Node copiedNodeFromCu = copiedTargetNodeFromCu.getChildNodes().get(0);

        // コピーされたRepairUnit中のtargetNodeが同じくコピーされたcompilationUnit中のものと同一(object1 == object2)か
        assertThat(copiedTargetNode).isSameAs(copiedTargetNodeFromCu);
        // コピーされたRepairUnit中のtargetNode中のNodeも同じくコピーされたcompilationUnit中のものと同一(object1 == object2)か
        assertThat(copiedNodeFromTargetNode).isSameAs(copiedNodeFromCu);
    }   
}