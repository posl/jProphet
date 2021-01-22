package jp.posl.jprophet;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.junit.Test;

public class HogeTest {
    @Test public void test() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        Fuga hoge = new Fuga();\n")
            .append("        hoge = \"str\";\n")
            .append("        if (hoge) return \"hoge\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        final CompilationUnit cu = JavaParser.parse(src);
        return;
    }
    
}
