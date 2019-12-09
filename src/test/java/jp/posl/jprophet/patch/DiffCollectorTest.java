package jp.posl.jprophet.patch;

import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.junit.Test;

public class DiffCollectorTest {
    @Test public void testForCondIntroduction(){
        final String beforeSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        String la = \"a\";\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final String afterSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        String la = \"a\";\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            if (true)\n")
            .append("                la = \"b\";\n")
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

    @Test public void testForVariableReplace(){
        final String beforeSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma(String str) {\n")
            .append("        String la = \"b\";\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final String afterSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma(String str) {\n")
            .append("        String la = \"b\";\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            la = str;\n")
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

    @Test public void testForCopyReplace(){
        final String beforeSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma(String str) {\n")
            .append("        String la = \"b\";\n")
            .append("        la = \"c\";\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final String afterSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma(String str) {\n")
            .append("        String la = \"b\";\n")
            .append("        la = \"c\";\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            la = str;\n")
            .append("            la = \"b\";\n")
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

    @Test public void testForCtrlFlowIntroduction(){
        final String beforeSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        int n = 0;\n")
            .append("        String lb = \"d\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final String afterSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        int n = 0;\n")
            .append("        if (true)\n")
            .append("            return;\n")
            .append("        String lb = \"d\";\n")
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

    @Test public void testForCondRefinement(){
        final String beforeSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        String la = \"a\";\n")
            .append("        int n = 0;\n")
            .append("        if (true)\n")
            .append("            n = 3;\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        final String afterSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        String la = \"a\";\n")
            .append("        int n = 0;\n")
            .append("        if (true || (la == null))\n")
            .append("            n = 3;\n")
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