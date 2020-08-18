package jp.posl.jprophet.operation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class CondIntroductionOperationTest {
    /**
     * ifブロックが挿入されているかテスト
     */
    @Test public void test(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private void ma() {\n")
            .append("        la = \"b\";\n")
            .append("        fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();

        expectedSources.add(new StringBuilder().append("")
            .append("if (fa == null)\n")
            .append("    la = \"b\";")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (fa != null)\n")
            .append("    la = \"b\";")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (true)\n")
            .append("    la = \"b\";")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (fa == null)\n")
            .append("    fa = \"b\";")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("if (fa != null)\n")
            .append("    fa = \"b\";")
            .toString()
        );
        
        expectedSources.add(new StringBuilder().append("")
            .append("if (true)\n")
            .append("    fa = \"b\";")
            .toString()
        );

        new OperaionTest().test(targetSource, expectedSources, new CondIntroductionOperation());
        
        return;
    }
    
}