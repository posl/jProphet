package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CopyReplaceOperationTest{
    

    /**
     * copiedStatementが置換でき,targetStatementの前にコピペされているかテスト
     */
    @Test public void testForStatementCopy(){
        
        final String beforeCopiedStatement = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private String fb = \"a\";\n\n")
            .append("    private void ma(String pa, String pb) {\n")
            .append("        String la = \"b\";\n")
            .toString();
        
        final String statementToBeCopied = 
            "        this.mb(\"hoge\", \"fuga\");\n";

        final String targetStatement = 
            "        la = \"d\";\n";

        final String afterTargetStatement = new StringBuilder().append("")
            .append("    }\n\n")
            .append("    private void mb(String a, String b) {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String targetSource = new StringBuilder().append("")
            .append(beforeCopiedStatement)
            .append(statementToBeCopied)
            .append(targetStatement)
            .append(afterTargetStatement)
            .toString();

        List<String> expectedTargetSources = new ArrayList<String>();
        expectedTargetSources.add("        this.mb(this.fa, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", this.fa);\n");
        expectedTargetSources.add("        this.mb(this.fb, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", this.fb);\n");
        expectedTargetSources.add("        this.mb(la, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", la);\n");
        expectedTargetSources.add("        this.mb(pa, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", pa);\n");
        expectedTargetSources.add("        this.mb(pb, \"fuga\");\n");
        expectedTargetSources.add("        this.mb(\"hoge\", pb);\n");

        List<String> expectedSources = expectedTargetSources.stream()
            .map(str -> {
                return new StringBuilder().append("")
                    .append(beforeCopiedStatement)
                    .append(statementToBeCopied)
                    .append(str)
                    .append(targetStatement)
                    .append(afterTargetStatement)
                    .toString();
            })
            .collect(Collectors.toList());

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            CopyReplaceOperation cr = new CopyReplaceOperation();
            /*
            candidateSources.addAll(cr.exec(node).stream()
                .map(cu -> cu.toString())
                .collect(Collectors.toList())
            );
            */
            List<CompilationUnit> cuList = cr.exec(node);
            for (CompilationUnit c : cuList){
                LexicalPreservingPrinter.setup(c);
                candidateSources.add(LexicalPreservingPrinter.print(c));
            }
        }
        assertThat(candidateSources).containsOnlyElementsOf(expectedSources);
        return;
    }

    /**
     * if文が含まれる場合のテスト
     */
    @Test public void testForIfStatementCopy(){

        final String beforeTargetStatement = new StringBuilder().append("")
            .append("public class A {\n\n")
            .append("    private String fa = \"a\";\n\n")
            .append("    private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String lb = \"b\";\n")
            .toString();
        
        final String statementToBeCopied = 
            "        la = \"hoge\";\n";

        final String targetStatement = new StringBuilder().append("")
            .append("        if (true) {\n")
            .append("            lb = \"huga\";\n")
            .append("        }\n")
            .toString();

        final String afterTargetStatement = new StringBuilder().append("")
            .append("    }\n")
            .append("}\n")
            .toString();

        final String targetSource = new StringBuilder().append("")
            .append(beforeTargetStatement)
            .append(statementToBeCopied)
            .append(targetStatement)
            .append(afterTargetStatement)
            .toString();


        List<String> expectedTargetSources = new ArrayList<String>();
        expectedTargetSources.add("        la = la;\n");
        expectedTargetSources.add("        la = lb;\n");
        expectedTargetSources.add("        la = this.fa;\n");
        expectedTargetSources.add("        la = pa;\n");

        List<String> expectedSources = expectedTargetSources.stream()
            .map(str -> {
                return new StringBuilder().append("")
                    .append(beforeTargetStatement)
                    .append(statementToBeCopied)
                    .append(str)
                    .append(targetStatement)
                    .append(afterTargetStatement)
                    .toString();
            })
            .collect(Collectors.toList());

        expectedSources.addAll(expectedTargetSources.stream()
            .map(str -> {
                return new StringBuilder().append("")
                    .append(beforeTargetStatement)
                    .append(statementToBeCopied)
                    .append("        if (true) {\n")
                    .append("    " + str)
                    .append("            lb = \"huga\";\n")
                    .append("        }\n")
                    .append(afterTargetStatement)
                    .toString();
            })
            .collect(Collectors.toList()));

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            CopyReplaceOperation cr = new CopyReplaceOperation();
            candidateSources.addAll(cr.exec(node).stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }
        assertThat(candidateSources).containsOnlyElementsOf(expectedSources);
        return;
    }

    /**
     * クラス外のステートメントに対して正常に動作するかテスト
     */
    @Test public void testForWhenThereIsNoCopy(){
        final String sourceThatHasNothingToReplace = new StringBuilder().append("")
        .append("import java.util.List;\n")
        .toString();

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(sourceThatHasNothingToReplace);
        List<Node> candidates = new ArrayList<Node>();
        for(Node node : repairUnits){
            CopyReplaceOperation vr = new CopyReplaceOperation();
            candidates.addAll(vr.exec(node));
        }

        assertThat(candidates.size()).isZero();
        return;
    }

    /**
     * 生成した修正パッチ候補に元のステートメントと同じものが含まれていないことをテスト
     */
    @Test public void testThatCandidatesDoesNotContainOriginal(){
        final String targetStatement = 
                "       la = lb;\n"; 

        final String source = new StringBuilder().append("")
        .append("public class A {\n\n")
        .append("    private void ma() {\n")
        .append("        String la = \"a\";\n")
        .append("        String lb = \"b\";\n")
        .append(targetStatement)
        .append("    }\n")
        .append("}\n")
        .toString();

        final String expectedTargetStatement = 
                "        la = lb;\n"; 
        final String expectedSource = new StringBuilder().append("")
        .append("public class A {\n\n")
        .append("    private void ma() {\n")
        .append("        String la = \"a\";\n")
        .append("        String lb = \"b\";\n")
        .append(expectedTargetStatement) 
        .append("    }\n")
        .append("}\n")
        .toString();

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(source);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            CopyReplaceOperation cr = new CopyReplaceOperation();
            candidateSources.addAll(cr.exec(node).stream()
                .map(ru -> ru.toString())
                .collect(Collectors.toList())
            );
        }

        assertThat(candidateSources).doesNotContain(expectedSource);
        return;
    }
}