package jp.posl.jprophet.patch;

import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import org.junit.Test;

public class DiffCollectorTest {
    @Test public void test(){
        final String beforeSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final String afterSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            if (true)\n\n")
            .append("                String la = \"b\";\n\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        DiffCollector diffCollector = new DiffCollector();
        diffCollector.collect(beforeSource, afterSource);

        /*
        NodeList<Statement> nodes = new NodeList<Statement>();
        nodes.add(new ExpressionStmt(new MethodCallExpr("methodCall2")));
        nodes.add(new ExpressionStmt(new MethodCallExpr("methodCall3")));
        nodes.add(new ExpressionStmt(new MethodCallExpr("methodCall4")));
        IfStmt insertNode = new IfStmt(new MethodCallExpr("methodCall"), new BlockStmt(nodes), null);
        IfStmt test2 = new IfStmt(new MethodCallExpr("methodCall"), insertNode, null); 
        Statement newStmt = JavaParser.parseStatement(insertNode.toString());
        Statement newStmt2 = JavaParser.parseStatement(test2.toString());
        LexicalPreservingPrinter.setup(newStmt);
        System.out.println(LexicalPreservingPrinter.print(newStmt));
        */
        return;
    }

    @Test public void test2(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void mb() {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        DiffCollector diffCollector = new DiffCollector();
        List<String> lists = diffCollector.changeStringToList(targetSource);
        for (String s : lists){
            System.out.println(s);
        }
        return;
    }
}