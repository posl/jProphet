package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class NodeUtilityTest {

    private String sourceCode;
    private String sourceCode2;
    private NodeList<Statement> nodeList;
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

        CompilationUnit compilationUnit = JavaParser.parse(sourceCode2);
        
        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        this.nodeList = block.getStatements();
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

        Node nodeToInsert = nodeList.get(0); //String la = "b";
        Node previousNode = nodeList.get(1); //la = "hoge";
        Node nextNode = nodeList.get(2); //ld = "huga";
        
        String reparsedSource = null;
        String reparsedSource2 = null;

        Node insertedNode = NodeUtility.insertNodeBetweenNodes(nodeToInsert, previousNode, nextNode).orElseThrow();
        Node insertedNode2 = NodeUtility.insertNodeWithNewLine(nodeToInsert, nextNode).orElseThrow();
        CompilationUnit insertedCompilationUnit = insertedNode.findCompilationUnit().orElseThrow();
        CompilationUnit insertedCompilationUnit2 = insertedNode2.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(insertedCompilationUnit);
        LexicalPreservingPrinter.setup(insertedCompilationUnit2);
        reparsedSource = LexicalPreservingPrinter.print(insertedCompilationUnit);
        reparsedSource2 = LexicalPreservingPrinter.print(insertedCompilationUnit2);

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
        
        String sourceOfStatementToInsert = new StringBuilder().append("")
            .append("int x = 0;\n")
            .toString();

        Statement statementToInsert = JavaParser.parseStatement(sourceOfStatementToInsert);


        Node previousNode = nodeList.get(1); //la = "hoge";
        Node nextNode = nodeList.get(2); //ld = "huga";

        String reparsedSource = null;
        String reparsedSource2 = null;

        Node insertedStatement = NodeUtility.insertNodeBetweenNodes(statementToInsert, previousNode, nextNode).orElseThrow();
        Node insertedStatement2 = NodeUtility.insertNodeWithNewLine(statementToInsert, nextNode).orElseThrow();
        CompilationUnit insertedCompilationUnit = insertedStatement.findCompilationUnit().orElseThrow();
        CompilationUnit insertedCompilationUnit2 = insertedStatement2.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(insertedCompilationUnit);
        LexicalPreservingPrinter.setup(insertedCompilationUnit2);
        reparsedSource = LexicalPreservingPrinter.print(insertedStatement.findCompilationUnit().orElseThrow());
        reparsedSource2 = LexicalPreservingPrinter.print(insertedStatement2.findCompilationUnit().orElseThrow());

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
        
        Node string = nodeList.get(0).getChildNodes().get(0).getChildNodes().get(0).getChildNodes().get(0); //String
        Node targetNode = nodeList.get(2); //ld = "huga";

        String reparsedSource = null;

        Node insertedNode = NodeUtility.insertNodeInOneLine(string, targetNode).orElseThrow();
        CompilationUnit insertedCompilationUnit = insertedNode.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(insertedCompilationUnit);
        reparsedSource = LexicalPreservingPrinter.print(insertedCompilationUnit);

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

        String reparsedSource = null;

        Node nodeToReplaceWith = nodeList.get(0); //String la = "b";
        Node targetNode = nodeList.get(2); //ld = "huga";

        Node replacedNode = NodeUtility.replaceNode(nodeToReplaceWith, targetNode).orElseThrow();
        CompilationUnit replacedCompilationUnit = replacedNode.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(replacedCompilationUnit);
        reparsedSource = LexicalPreservingPrinter.print(replacedCompilationUnit);

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
        
        Node nodeToReplaceWith = nodeList.get(1).getChildNodes().get(0).getChildNodes().get(1); //"hoge"
        Node targetNode = nodeList.get(2).getChildNodes().get(0).getChildNodes().get(1); //"huga"
        
        String reparsedSource = null;

        Node replacedStatement = NodeUtility.replaceNode(nodeToReplaceWith, targetNode).orElseThrow();
        CompilationUnit replacedCompilationUnit = replacedStatement.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(replacedCompilationUnit);
        reparsedSource = LexicalPreservingPrinter.print(replacedCompilationUnit);

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
        
        Node targetNode = nodeList.get(2); //ld = "huga";

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
        CompilationUnit insertedCompilationUnit = NodeUtility.insertTokenWithNewLine(tokenRange, targetNode).orElseThrow();
        LexicalPreservingPrinter.setup(insertedCompilationUnit);
        reparsedSource = LexicalPreservingPrinter.print(insertedCompilationUnit);

        assertThat(reparsedSource).isEqualTo(expectedSource);

        return;
    }

    /**
     * TokenRangeを持たないノード(複数行のもの)が挿入できるかテスト
     */
    @Test public void testForInsertNodeNotHaveToken(){

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       if (methodCall())\n")
            .append("           la = \"hoge\";\n")
            .append("       ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        
        Node insertNode = new IfStmt(new MethodCallExpr("methodCall"), nodeList.get(1), null); //if (methodCall()) \n la = "hoge";
        Node targetNode = nodeList.get(2); //ld = "huga";
        
        String reparsedSource = null;

        Node replacedStatement = NodeUtility.insertNodeWithNewLine(insertNode, targetNode).orElseThrow();
        CompilationUnit replacedCompilationUnit = replacedStatement.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(replacedCompilationUnit);
        reparsedSource = LexicalPreservingPrinter.print(replacedCompilationUnit);
        assertThat(reparsedSource).isEqualTo(expectedSource);

        return;
    }
    /**
     * TokenRangeをもたないノード(複数行のもの)が置換できるかテスト
     */
    @Test public void testForReplaceNodeNotHaveToken(){

        String expectedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       if (methodCall())\n")
            .append("           la = \"hoge\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        
        Node nodeToReplaceWith = new IfStmt(new MethodCallExpr("methodCall"), nodeList.get(1), null); //if (methodCall()) \n la = "hoge";
        Node targetNode = nodeList.get(2); //ld = "huga";
        
        String reparsedSource = null;

        Node replacedStatement = NodeUtility.replaceNode(nodeToReplaceWith, targetNode).orElseThrow();
        CompilationUnit replacedCompilationUnit = replacedStatement.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(replacedCompilationUnit);
        reparsedSource = LexicalPreservingPrinter.print(replacedCompilationUnit);
        assertThat(reparsedSource).isEqualTo(expectedSource);

        return;
    }


}
