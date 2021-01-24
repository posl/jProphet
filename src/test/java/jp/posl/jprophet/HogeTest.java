package jp.posl.jprophet;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.Statement;

import org.junit.Test;

public class HogeTest {
    @Test public void test() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        Fuga hoge = new Fuga();\n")
            .append("        hoge = \"str\";\n")
            .append("        method();\n")
            .append("        if (hoge) return \"hoge\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        final CompilationUnit cu = JavaParser.parse(src);
        final MethodCallExpr m = cu.findFirst(MethodCallExpr.class).orElseThrow();
        final Statement s = m.findParent(Statement.class).orElseThrow();
        return;
    }
    
}
