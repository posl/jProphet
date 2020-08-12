package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.patch.DiffWithType;

import com.github.javaparser.ast.Node;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MethodReplacementOperationTest {
    /**
     * メソッドと引数を置換する機能のテスト
     */
    @Test public void testForMethodReplace(){
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma() {\n")
            .append("        this.ma(\"hoge\", \"fuga\");\n")
            .append("    }\n")
            .append("    private void mb() {\n")
            .append("    }\n")
            .append("    private void mc() {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = List.of(
                    "this.mb(\"hoge\", \"fuga\")",
                    "this.mc(\"hoge\", \"fuga\")",
                    "this.mb(this.fa, \"fuga\")",
                    "this.mb(\"hoge\", this.fa)",
                    "this.mc(this.fa, \"fuga\")",
                    "this.mc(\"hoge\", this.fa)"
        );

        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<String> actualSources = new ArrayList<String>();
        for(Node node : nodes){
            final List<DiffWithType> results = new MethodReplacementOperation().exec(node);
            for (DiffWithType result : results){
                System.out.println(result.getTargetNodeAfterFix().toString());
                actualSources.add(result.getTargetNodeAfterFix().toString());
            }
        }
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
        return;
    }
}