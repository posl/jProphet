package jp.posl.jprophet.patch;

import java.util.List;

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
            .append("            String la = \"dd\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        DiffCollector diffCollector = new DiffCollector();
        diffCollector.exec(beforeSource, afterSource);
        return;
    }

    @Test public void test2(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        for (int i = 0; i < 10; i++) {\n")
            .append("            String la = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        List<String> lists = DiffCollector.getLines(targetSource);
        for (String s : lists){
            System.out.println(s);
        }
        return;
    }
}