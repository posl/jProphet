package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;

import org.junit.Before;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public class CopyReplaceOperationTest{
    
    private List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
    private String source = new StringBuilder().append("")
    .append("public class A {\n")
    .append("   private String fa = \"a\";\n")
    .append("   private String fb = \"a\";\n")
    .append("   private void ma(String pa, String pb) {\n")
    .append("       String la = \"b\";\n")
    .append("       la = \"hoge\";\n")
    //.append("       int k = 3;\n")
    .append("       if (k == 5) {\n")
    .append("           k = 5;\n")
    .append("       }\n")
    .append("       this.mb(\"hoge\", \"fuga\");\n")
    .append("       String ld = \"d\";\n")
    .append("       ld = \"hoge\";\n")
    .append("   }\n")
    .append("   private void mb(String a, String b) {\n")
    .append("   }\n")
    .append("}\n")
    .toString();

    @Before public void setUp(){
        List<Node> nodes = NodeUtility.getAllNodesFromCode(source);
        for(Node node : nodes){
            CopyReplaceOperation cr = new CopyReplaceOperation();
            this.candidates.addAll(cr.exec(node));
        }
        return;
    }

    /**
     * ステートメントコピペ後の修正候補の数のテスト
     */
    @Test public void testForNumOfRepairCopied(){
        //assertThat(candidates.size()).isEqualTo(14);
        return;
    }

    /**
     * 修正される対象のステートメントが正しいかテスト
     */
    @Test public void testForTargetStatementAfterCopied(){
        List<String> expectedTargetSources = new ArrayList<String>();
        expectedTargetSources.add("int k = 3;");
        expectedTargetSources.add("if (k == 5) {\n    k = 5;\n}");
        expectedTargetSources.add("k = 5;");
        expectedTargetSources.add("this.mb(\"hoge\", \"fuga\");");
        expectedTargetSources.add("String ld = \"d\";");

        List<String> candidateSources = this.candidates.stream()
            .map(s -> s.toString())
            .collect(Collectors.toList());
        
        //assertThat(candidateSources).containsOnlyElementsOf(expectedTargetSources);
        return;
    }

    /**
     * RepairUnitのcompilationUnitが正しく書き換えられているかテスト(14個のうち1つだけ確認)
     */
    @Test public void testForCopiedStatementBeforeTarget(){
        final String targetStatement = "        this.mb(\"hoge\", \"fuga\");\n";
        final String copiedStatement = "        int k = 3;\n";
        final String expectedSource = new StringBuilder().append("")
        .append("public class A {\n\n")
        .append("    private String fa = \"a\";\n\n")
        .append("    private String fb = \"a\";\n\n")
        .append("    private void ma(String pa, String pb) {\n")
        .append("        String la = \"b\";\n")
        .append("        int k = 3;\n")
        .append("        if (k == 5) {\n")
        .append("            k = 5;\n")
        .append("        }\n")
        .append(copiedStatement)
        .append(targetStatement)
        .append("        String ld = \"d\";\n")
        .append("    }\n\n")
        .append("    private void mb(String a, String b) {\n")
        .append("    }\n")
        .append("}\n")
        .toString();

        //assertThat(candidates.get(4).getCompilationUnit().toString()).isEqualTo(expectedSource);
        return;
    }

    /**
     * メソッド外のステートメントに対して正常に動作するかテスト
     */
    @Test public void testForWhenThereIsNoCopy(){
        final String sourceThatHasNothingToCopy = new StringBuilder().append("")
        .append("import java.util.List;\n")
        .toString();

        List<Node> nodes = NodeUtility.getAllNodesFromCode(sourceThatHasNothingToCopy);
        List<Node> candidates = new ArrayList<Node>();
        for(Node node : nodes){
            CopyReplaceOperation cr = new CopyReplaceOperation();
            candidates.addAll(cr.exec(node));
        }

        //assertThat(candidates.size()).isZero();
        return;
    }
}