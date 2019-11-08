package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;

public class NodeUtilityTest {

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
        List<Node> nodes = NodeUtility.getAllNodesFromCode((sourceCode));
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
        Node node = NodeUtility.getAllDescendantNodes(JavaParser.parse(sourceCode)).get(0);
        Node copiedNode = NodeUtility.deepCopy(node);

        assertThat(copiedNode).isNotSameAs(node);
        assertThat(copiedNode.getParentNode().isPresent()).isTrue();
        assertThat(copiedNode.getParentNode().get()).isNotSameAs(node.getParentNode().get());
    }


}