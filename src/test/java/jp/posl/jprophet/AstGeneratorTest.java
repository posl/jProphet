package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;

public class AstGeneratorTest {

    private String sourceCode;
    /**
     * 入力用のソースコードからNodeリストを生成
     */
    @Before public void setUpRepairUnits(){
        this.sourceCode = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("   }\n")
            .append("}\n")
            .toString();
    }

    /**
     * getAllNodesFromCodeメソッドによってソースコードの全てのNodeが取得できているかテスト
     */
    @Test public void testForStringByGetAllChileNodes(){
        List<Node> nodes = AstGenerator.getAllNodesFromCode((sourceCode));
        assertThat(nodes.size()).isEqualTo(6);
        assertThat(nodes.get(0).toString()).isEqualTo("public class A {\n\n    public void a() {\n    }\n}");
        assertThat(nodes.get(1).toString()).isEqualTo("A");
        assertThat(nodes.get(2).toString()).isEqualTo("public void a() {\n}");
        assertThat(nodes.get(3).toString()).isEqualTo("a");
        assertThat(nodes.get(4).toString()).isEqualTo("void");
        assertThat(nodes.get(5).toString()).isEqualTo("{\n}");
    }


    /**
     * deepCopyメソッドがNodeの親ノードも含めてコピーできているかテスト
     */
    @Test public void testForCopiedNode(){
        Node node = AstGenerator.getAllDescendantNodes(JavaParser.parse(sourceCode)).get(0);
        Node copiedNode = AstGenerator.deepCopy(node);

        assertThat(copiedNode).isNotSameAs(node);
        assertThat(copiedNode.getParentNode().isPresent()).isTrue();
        assertThat(copiedNode.getParentNode().get()).isNotSameAs(node.getParentNode().get());
    }




    @Test public void testForGeneratedAstString() {
        final String source = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        List<RepairUnit> repairUnit = new AstGenerator().getAllRepairUnit(source);
        
        // ASTが取得できているか確認
        assertThat(repairUnit.size()).isEqualTo(6);
        assertThat(repairUnit.get(0).getTargetNode().toString()).isEqualTo("public class A {\n\n    public void a() {\n    }\n}");
        assertThat(repairUnit.get(1).getTargetNode().toString()).isEqualTo("A");
        assertThat(repairUnit.get(2).getTargetNode().toString()).isEqualTo("public void a() {\n}");
        assertThat(repairUnit.get(3).getTargetNode().toString()).isEqualTo("a");
        assertThat(repairUnit.get(4).getTargetNode().toString()).isEqualTo("void");
        assertThat(repairUnit.get(5).getTargetNode().toString()).isEqualTo("{\n}");
    }   

    @Test public void testForCompilationUnit(){
        final String source = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       int i = 0;\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        List<RepairUnit> repairUnits = new AstGenerator().getAllRepairUnit(source);
        assertThat(repairUnits.size()).isEqualTo(12);
        final int targetUnitIndex = 8;
        RepairUnit targetUnit = repairUnits.get(targetUnitIndex);
        
        // それぞれのRepairUnitの持つコンパイルユニットが別インスタンスで，かつtargetNodeのコンパイルユニットと
        // 一致していることの確認 
        for(RepairUnit repairUnit : repairUnits){
            assertThat(repairUnit.getTargetNode().findRootNode()).isSameAs(repairUnit.getCompilationUnit());
        }
        for(int i = 0; i < repairUnits.size(); i++){
            if(i == targetUnitIndex) continue;
            assertThat(targetUnit.getCompilationUnit()).isNotSameAs(repairUnits.get(i).getCompilationUnit());
        }

        Node node = targetUnit.getTargetNode();
        assertThat(node).isInstanceOf(VariableDeclarator.class);
        ((VariableDeclarator)node).setName("test");
        assertThat(targetUnit.getCompilationUnit().toString()).contains("test");       

        // 実際にASTに変更を加えて確認(念の為)
        for(int i = 0; i < repairUnits.size(); i++){
            if(i == targetUnitIndex) continue;
            assertThat(repairUnits.get(i).getCompilationUnit().toString()).doesNotContain("test");       
        }
    }
}