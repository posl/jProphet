package jp.posl.jprophet.patch;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RepairDiffTest {

    /**
     * diffが取得できているかテスト
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
            .append("3               String la = \"a\";\n")
            .append("4               for (int i = 0; i < 10; i++) {\n")
            .append("5     -             la = \"b\";\n")
            .append("6               }\n")
            .append("7           }\n\n")
            .append("3               String la = \"a\";\n")
            .append("4               for (int i = 0; i < 10; i++) {\n")
            .append("5     +             if (true)\n")
            .append("6     +                 la = \"b\";\n")
            .append("7               }\n")
            .append("8           }\n\n")
            .toString();
        
        assertThat(diff).isEqualTo(expectedDiff);

        return;
    }

    /**
     * 修正箇所より前に行が1行しかない場合のテスト
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
            .append("    public void ma(String str) {\n")
            .append("        String la = \"b\";\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
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
            .append("1       public class A {\n")
            .append("2     -     private void ma(String str) {\n")
            .append("3               String la = \"b\";\n")
            .append("4               for (int i = 0; i < 10; i++) {\n\n")
            .append("1       public class A {\n")
            .append("2     +     public void ma(String str) {\n")
            .append("3               String la = \"b\";\n")
            .append("4               for (int i = 0; i < 10; i++) {\n\n")
            .append("")
            .toString();
        
        System.out.println(diff);
        assertThat(diff).isEqualTo(expectedDiff);

        return;
    }

    /**
     * 修正箇所の後ろに行が1行しかない場合のテスト
     */
    @Test public void testForCopyReplace(){
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
            .append("            la = \"b\";\n")
            .append("        }\n")
            .append("        }\n")
            .append("}\n")
            .toString();

        CompilationUnit beforeCu = JavaParser.parse(beforeSource);
        CompilationUnit afterCu = JavaParser.parse(afterSource);
        RepairDiff diffCollector = new RepairDiff(beforeCu, afterCu);
        String diff = diffCollector.toString();
        String expectedDiff = new StringBuilder().append("")
            .append("5                   la = \"b\";\n")
            .append("6               }\n")
            .append("7     -     }\n")
            .append("8       }\n\n")
            .append("5                   la = \"b\";\n")
            .append("6               }\n")
            .append("7     +         }\n")
            .append("8       }\n\n")
            .toString();

        assertThat(diff).isEqualTo(expectedDiff);

        return;
    }

}