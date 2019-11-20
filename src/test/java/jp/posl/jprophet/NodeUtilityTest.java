package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class NodeUtilityTest {

    private String sourceCode;
    private String sourceCode2;
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

        this.sourceCode2 = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       ld = \"huga\";\n")
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
     * deepCopyByReparseメソッドがNodeの親ノードも含めてコピーできているかテスト
     */
    @Test public void testForCopiedNodeByReparse(){
        Node node = NodeUtility.getAllDescendantNodes(JavaParser.parse(sourceCode)).get(0);
        Node copiedNode = NodeUtility.deepCopyByReparse(node);

        assertThat(copiedNode).isNotSameAs(node);
        assertThat(copiedNode.getParentNode().isPresent()).isTrue();
        assertThat(copiedNode.getParentNode().get()).isNotSameAs(node.getParentNode().get());
    }



    /**
     * ノードが挿入されているかテスト(コピペ)
     */
    @Test public void testForInsertNodeByCopy(){

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
        
        CompilationUnit compilationUnit = JavaParser.parse(sourceCode2);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();

        String reparsedSource = null;
        String reparsedSource2 = null;

        try{
            Node insertNode = NodeUtility.insertNodeBetweenNodes(nodeList.get(0), nodeList.get(1),nodeList.get(2));
            Node insertNode2 = NodeUtility.insertNodeWithNewLine(nodeList.get(0), nodeList.get(2));
            CompilationUnit insertedCompilationUnit = insertNode.findCompilationUnit().orElseThrow();
            CompilationUnit insertedCompilationUnit2 = insertNode2.findCompilationUnit().orElseThrow();
            LexicalPreservingPrinter.setup(insertedCompilationUnit);
            LexicalPreservingPrinter.setup(insertedCompilationUnit2);
            reparsedSource = LexicalPreservingPrinter.print(insertedCompilationUnit);
            reparsedSource2 = LexicalPreservingPrinter.print(insertedCompilationUnit2);
        }catch(NoSuchElementException e){}

        assertThat(reparsedSource).isEqualTo(expectedSource);
        assertThat(reparsedSource2).isEqualTo(expectedSource);

        return;
    }

    /**
     * ノードが挿入されているかテスト(作成したノード)
     */
    @Test public void testForInsertByManual(){

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
        
        CompilationUnit compilationUnit = JavaParser.parse(sourceCode2);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();
        
        String insertStatementSource = new StringBuilder().append("")
            .append("int x = 0;\n")
            .toString();

        Statement insertStatement = JavaParser.parseStatement(insertStatementSource);

        String reparsedSource = null;
        String reparsedSource2 = null;

        try{
            Node insertedStatement = NodeUtility.insertNodeBetweenNodes(insertStatement, nodeList.get(1),nodeList.get(2));
            Node insertedStatement2 = NodeUtility.insertNodeWithNewLine(insertStatement, nodeList.get(2));
            CompilationUnit insertedCompilationUnit = insertedStatement.findCompilationUnit().orElseThrow();
            CompilationUnit insertedCompilationUnit2 = insertedStatement2.findCompilationUnit().orElseThrow();
            LexicalPreservingPrinter.setup(insertedCompilationUnit);
            LexicalPreservingPrinter.setup(insertedCompilationUnit2);
            reparsedSource = LexicalPreservingPrinter.print(insertedStatement.findCompilationUnit().orElseThrow());
            reparsedSource2 = LexicalPreservingPrinter.print(insertedStatement2.findCompilationUnit().orElseThrow());
        }catch (NoSuchElementException e) {}

        assertThat(reparsedSource).isEqualTo(expectedSource);
        assertThat(reparsedSource2).isEqualTo(expectedSource);

        return;
    }

    /** 
     * 1行の中でノードが挿入されるかテスト
     */
    @Test public void testForInsertNodeInOneLineByCopy(){

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       String ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        
        CompilationUnit compilationUnit = JavaParser.parse(sourceCode2);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();
        Node string = nodeList.get(0).getChildNodes().get(0).getChildNodes().get(0).getChildNodes().get(0);

        String reparsedSource = null;

        try{
            Node insertNode = NodeUtility.insertNodeInOneLine(string, nodeList.get(2));
            CompilationUnit insertedCompilationUnit = insertNode.findCompilationUnit().orElseThrow();
            LexicalPreservingPrinter.setup(insertedCompilationUnit);
            reparsedSource = LexicalPreservingPrinter.print(insertedCompilationUnit);
        }catch (NoSuchElementException e) {}

        assertThat(reparsedSource).isEqualTo(expectedSource);
        
        return;
    }

    /**
     * ノードが置換されているかテスト(コピペ)
     */
    @Test public void testForReplaceNodeByCopy(){

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
        
        CompilationUnit compilationUnit = JavaParser.parse(sourceCode2);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();

        Node replacedNode = NodeUtility.replaceNode(nodeList.get(0), nodeList.get(2));
        CompilationUnit replacedCompilationUnit = replacedNode.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(replacedCompilationUnit);
        String reparsedSource = LexicalPreservingPrinter.print(replacedCompilationUnit);
        assertThat(reparsedSource).isEqualTo(expectedSource);
        
        return;
    }

    /**
     * ノードが置換されているかテスト(作成したノード)
     */
    @Test public void testForReplaceNodeByManual(){

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
        
        CompilationUnit compilationUnit = JavaParser.parse(sourceCode2);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();
        Node replaceNode = nodeList.get(1).getChildNodes().get(0).getChildNodes().get(1);
        Node targetNode = nodeList.get(2).getChildNodes().get(0).getChildNodes().get(1);
        
        String reparsedSource = null;

        try{
            Node replacedStatement = NodeUtility.replaceNode(replaceNode, targetNode);
            CompilationUnit replacedCompilationUnit = replacedStatement.findCompilationUnit().orElseThrow();
            LexicalPreservingPrinter.setup(replacedCompilationUnit);
            reparsedSource = LexicalPreservingPrinter.print(replacedCompilationUnit);
        }catch (NoSuchElementException e) {}

        assertThat(reparsedSource).isEqualTo(expectedSource);

        return;
    }

    /** TokenRangeを作成してノードの前に挿入するテスト */
    @Test public void testForInsertToken(){

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       if (true){ la=\"p\";}\n")
            .append("       ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        
        CompilationUnit compilationUnit = JavaParser.parse(sourceCode2);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();
        
        Node targetNode = nodeList.get(2);

        JavaToken begin = new JavaToken(JavaToken.Kind.IF.getKind());
        JavaToken end = new JavaToken(JavaToken.Kind.RBRACE.getKind());

        begin.insertAfter(end);
        begin.insertAfter(new JavaToken(JavaToken.Kind.SEMICOLON.getKind()));
        begin.insertAfter(new JavaToken(JavaToken.Kind.STRING_LITERAL.getKind(), "\"p\""));
        begin.insertAfter(new JavaToken(JavaToken.Kind.ASSIGN.getKind()));
        begin.insertAfter(new JavaToken(JavaToken.Kind.IDENTIFIER.getKind(), "la"));
        begin.insertAfter(new JavaToken(JavaToken.Kind.SPACE.getKind()));
        begin.insertAfter(new JavaToken(JavaToken.Kind.LBRACE.getKind()));
        begin.insertAfter(new JavaToken(JavaToken.Kind.RPAREN.getKind()));
        begin.insertAfter(new JavaToken(JavaToken.Kind.TRUE.getKind()));
        begin.insertAfter(new JavaToken(JavaToken.Kind.LPAREN.getKind()));
        begin.insertAfter(new JavaToken(JavaToken.Kind.SPACE.getKind()));

        TokenRange tokenRange = new TokenRange(begin, end);

        String reparsedSource = null;
        try{
            CompilationUnit insertedCompilationUnit = NodeUtility.insertTokenWithNewLine(tokenRange, targetNode);
            LexicalPreservingPrinter.setup(insertedCompilationUnit);
            reparsedSource = LexicalPreservingPrinter.print(insertedCompilationUnit);
        }catch (NoSuchElementException e) {}

        assertThat(reparsedSource).isEqualTo(expectedSource);

        return;
    }
    
}
