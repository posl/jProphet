package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

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



    /**
     * ノードが挿入されているかテスト(コピペ)
     */
    @Test public void testForInsertNodeByCopy(){
        String source = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       String la = \"b\";\n")
            .append("       ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        
        CompilationUnit compilationUnit = JavaParser.parse(source);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();

        Node insertNode = NodeUtility.insertNode(nodeList.get(0), nodeList.get(1),nodeList.get(2));
        Node insertNode2 = NodeUtility.insertNodeWithNewLine(nodeList.get(0), nodeList.get(2));
        CompilationUnit insertedCompilationUnit = insertNode.findCompilationUnit().orElseThrow();
        CompilationUnit insertedCompilationUnit2 = insertNode2.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(insertedCompilationUnit);
        LexicalPreservingPrinter.setup(insertedCompilationUnit2);
        String reparsedSource = LexicalPreservingPrinter.print(insertedCompilationUnit);
        String reparsedSource2 = LexicalPreservingPrinter.print(insertedCompilationUnit2);

        assertThat(reparsedSource).isEqualTo(expectedSource);
        assertThat(reparsedSource2).isEqualTo(expectedSource);
        
        return;
    }

    /**
     * ノードが挿入されているかテスト(作成したノード)
     */
    @Test public void testForInsertByManual(){
        String source = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       int x = 0;\n")
            .append("       ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        
        CompilationUnit compilationUnit = JavaParser.parse(source);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();
        
        String insertStatementSource = new StringBuilder().append("")
            .append("int x = 0;\n")
            .toString();

        Statement insertStatement = JavaParser.parseStatement(insertStatementSource);
        Node insertedStatement = NodeUtility.insertNode(insertStatement, nodeList.get(1),nodeList.get(2));
        LexicalPreservingPrinter.setup(insertedStatement.findCompilationUnit().orElseThrow());
        String source2 = LexicalPreservingPrinter.print(insertedStatement.findCompilationUnit().orElseThrow());

        assertThat(source2).isEqualTo(expectedSource);

        return;
    }

    /**
     * ノードが置換されているかテスト(コピペ)
     */
    @Test public void testForReplaceNodeByCopy(){
        String source = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       String la = \"b\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        
        CompilationUnit compilationUnit = JavaParser.parse(source);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();

        Node insertNode = NodeUtility.replaceNode(nodeList.get(0), nodeList.get(2));
        CompilationUnit compilationUnit2 = insertNode.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(compilationUnit2);
        String source2 = LexicalPreservingPrinter.print(compilationUnit2);
        System.out.println(source2);
        assertThat(source2).isEqualTo(expectedSource);
        
        return;
    }

    /**
     * ノードが置換されているかテスト(作成したノード)
     */
    @Test public void testForReplaceNodeByManual(){
        String source = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       ld = \"hoge\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        
        CompilationUnit compilationUnit = JavaParser.parse(source);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();
        Node replaceNode = nodeList.get(1).getChildNodes().get(0).getChildNodes().get(1);
        Node targetNode = nodeList.get(2).getChildNodes().get(0).getChildNodes().get(1);
        
        //Statement insertStatement = JavaParser.parseStatement(insertStatementSource);
        Node replacedStatement = NodeUtility.replaceNode(replaceNode, targetNode);
        LexicalPreservingPrinter.setup(replacedStatement.findCompilationUnit().orElseThrow());
        String source2 = LexicalPreservingPrinter.print(replacedStatement.findCompilationUnit().orElseThrow());

        assertThat(source2).isEqualTo(expectedSource);

        return;
    }
    
}
