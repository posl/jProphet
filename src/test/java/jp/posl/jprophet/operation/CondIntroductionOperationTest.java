package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import com.github.javaparser.ast.Node;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CondIntroductionOperationTest {
    /**
     * returnをするifブロックが挿入されているかテスト
     */
    /*
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
            .append("public class A {\n\n") //toString(PrettyPrinter)での出力ではクラス宣言の先頭行の後に空行が入る仕様 
            .append("    private String fa = \"a\";\n\n") //ここも
            .append("    private void ma() {\n")
            .append("        if (true)\n")
            .append("            la = \"b\";\n")
            .append("        fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString()
        );

        expectedSources.add(new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private void ma() {\n")
            .append("        la = \"b\";\n")
            .append("        if (true)\n")
            .append("            fa = \"b\";\n")
            .append("    }\n")
            .append("}\n")
            .toString()
        );

        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<String> candidateSources = new ArrayList<String>();
        for(Node node : nodes){
            final CondIntroductionOperation cio = new CondIntroductionOperation();
            candidateSources.addAll(cio.exec(node).stream()
                .map(ru -> ru.findCompilationUnit().get().toString()) 
                .collect(Collectors.toList())
            );
        }

        assertThat(candidateSources).containsAll(expectedSources);
        return;
    }
    */
}