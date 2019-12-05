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
            .append("            if (true)\n\n")
            .append("                String la = \"b\";\n\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        DiffCollector diffCollector = new DiffCollector();
        diffCollector.collect(beforeSource, afterSource);
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