package jp.posl.jprophet.patch;

import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

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
            .append("            if (true){\n")
            .append("                String la = \"b\";\n\n")
            .append("            }\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        CompilationUnit beforeCu = JavaParser.parse(beforeSource);
        CompilationUnit afterCu = JavaParser.parse(afterSource);
        DiffCollector diffCollector = new DiffCollector(beforeCu, afterCu);
        String test = diffCollector.getSourceDiff();
        System.out.println(test);

        return;
    }

    @Test public void test2(){
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
            .append("            String lb = \"d\";\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        CompilationUnit beforeCu = JavaParser.parse(beforeSource);
        CompilationUnit afterCu = JavaParser.parse(afterSource);
        DiffCollector diffCollector = new DiffCollector(beforeCu, afterCu);
        String test = diffCollector.getSourceDiff();
        System.out.println(test);

        return;
    }

    @Test public void test3(){
        final String beforeSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            int n = 0;\n\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final String afterSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            int n = 0;\n\n")
            .append("            String la = \"b\";\n")
            .append("            la = \"c\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        CompilationUnit beforeCu = JavaParser.parse(beforeSource);
        CompilationUnit afterCu = JavaParser.parse(afterSource);
        DiffCollector diffCollector = new DiffCollector(beforeCu, afterCu);
        String test = diffCollector.getSourceDiff();
        System.out.println(test);

        return;
    }

    @Test public void test4(){
        final String beforeSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            int n = 0;\n\n")
            .append("            String lb = \"d\";\n\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final String afterSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            int n = 0;\n\n")
            .append("            String lb = \"d\";\n\n")
            .append("            if (true){\n")
            .append("                String la = \"b\";\n")
            .append("            }\n")
            .append("            n = 4;\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        CompilationUnit beforeCu = JavaParser.parse(beforeSource);
        CompilationUnit afterCu = JavaParser.parse(afterSource);
        DiffCollector diffCollector = new DiffCollector(beforeCu, afterCu);
        String test = diffCollector.getSourceDiff();
        System.out.println(test);

        return;
    }

    @Test public void test5(){
        final String beforeSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            int n = 0;\n\n")
            .append("            String lb = \"d\";\n\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final String afterSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            int n = 0;\n\n")
            .append("            String lb = \"d\";\n\n")
            .append("            if (true){\n")
            .append("                String la = \"b\";\n")
            .append("            }\n")
            .append("            n = 4;\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        CompilationUnit beforeCu = JavaParser.parse(beforeSource);
        CompilationUnit afterCu = JavaParser.parse(afterSource);
        DiffCollector diffCollector = new DiffCollector(beforeCu, afterCu);
        String test = diffCollector.getSourceDiff();
        System.out.println(test);

        return;
    }


}