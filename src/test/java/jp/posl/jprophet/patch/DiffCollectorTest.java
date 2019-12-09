package jp.posl.jprophet.patch;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class DiffCollectorTest {

    /**
     * CondIntroductionで修正された場合のdiffの取得のテスト
     */
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
        RepairDiff diffCollector = new RepairDiff(beforeCu, afterCu);
        String diff = diffCollector.toString();
        String expectedDiff = new StringBuilder().append("")
            .append("5     -            la = \"b\";\n\n")
            .append("5     +            if (true)\n")
            .append("6     +                la = \"b\";\n\n")
            .toString();
        
        assertThat(diff).isEqualTo(expectedDiff);

        return;
    }

    /**
     * VariableReplacementで修正された場合のdiffの取得のテスト
     */
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
        RepairDiff diffCollector = new RepairDiff(beforeCu, afterCu);
        String diff = diffCollector.toString();
        String expectedDiff = new StringBuilder().append("")
            .append("5     -            la = \"b\";\n\n")
            .append("5     +            la = str;\n\n")
            .toString();
        
        assertThat(diff).isEqualTo(expectedDiff);

        return;
    }

    /**
     * CopyReplace修正された場合のdiffの取得のテスト
     */
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
        RepairDiff diffCollector = new RepairDiff(beforeCu, afterCu);
        String diff = diffCollector.toString();
        String expectedDiff = new StringBuilder().append("")
            .append("\n")
            .append("6     +            la = str;\n\n")
            .toString();

        assertThat(diff).isEqualTo(expectedDiff);

        return;
    }

    /**
     * CtrlFlowIntroduction修正された場合のdiffの取得のテスト
     */
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
        RepairDiff diffCollector = new RepairDiff(beforeCu, afterCu);
        String diff = diffCollector.toString();
        String expectedDiff = new StringBuilder().append("")
            .append("\n")
            .append("4     +        if (true)\n")
            .append("5     +            return;\n\n")
            .toString();

        assertThat(diff).isEqualTo(expectedDiff);

        return;
    }

    /**
     * CondRefinment修正された場合のdiffの取得のテスト
     */
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
        RepairDiff diffCollector = new RepairDiff(beforeCu, afterCu);
        String diff = diffCollector.toString();
        String expectedDiff = new StringBuilder().append("")
            .append("5     -        if (true)\n\n")
            .append("5     +        if (true || (la == null))\n\n")
            .toString();

        assertThat(diff).isEqualTo(expectedDiff);

        return;
    }


}