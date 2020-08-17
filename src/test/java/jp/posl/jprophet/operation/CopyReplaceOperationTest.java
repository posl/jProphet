package jp.posl.jprophet.operation;

import org.junit.Test;



import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class CopyReplaceOperationTest{
    

    /**
     * copiedStatementが置換でき,targetStatementの前にコピペされているかテスト
     */

    
    @Test public void testForStatementCopy(){
        
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private String fb = \"a\";\n")
            .append("    private void ma(String pa, String pb) {\n")
            .append("        String la = \"b\";\n")
            .append("        this.mb(\"hoge\", \"fuga\");\n")
            .append("        la = \"d\";\n")
            .append("    }\n")
            .append("    private void mb(String a, String b) {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final List<String> expectedSources = new ArrayList<String>();
        expectedSources.add("this.mb(this.fa, \"fuga\");");
        expectedSources.add("this.mb(\"hoge\", this.fa);");
        expectedSources.add("this.mb(this.fb, \"fuga\");");
        expectedSources.add("this.mb(\"hoge\", this.fb);");
        expectedSources.add("this.mb(la, \"fuga\");");
        expectedSources.add("this.mb(\"hoge\", la);");
        expectedSources.add("this.mb(pa, \"fuga\");");
        expectedSources.add("this.mb(\"hoge\", pa);");
        expectedSources.add("this.mb(pb, \"fuga\");");
        expectedSources.add("this.mb(\"hoge\", pb);");

        OperationTest operationTest = new OperationTest();
        operationTest.test(targetSource, expectedSources, new CopyReplaceOperation());
    }

    /**
     * if文が含まれる場合のテスト
     */
    @Test public void testForIfStatementCopy(){

        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private void ma(String pa) {\n")
            .append("        String la = \"a\";\n")
            .append("        String lb = \"b\";\n")
            .append("        la = \"hoge\";\n")
            .append("        if (true) {\n")
            .append("            lb = \"huga\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        List<String> expectedSources = new ArrayList<String>();
        expectedSources.add("la = la;");
        expectedSources.add("la = lb;");
        expectedSources.add("la = this.fa;");
        expectedSources.add("la = pa;");

        OperationTest operationTest = new OperationTest();
        operationTest.test(targetSource, expectedSources, new CopyReplaceOperation());
    }

    /**
     * クラス外のステートメントに対して正常に動作するかテスト
     */
    /*
    @Test public void testForWhenThereIsNoCopy(){
        final String sourceThatHasNothingToReplace = new StringBuilder().append("")
        .append("import java.util.List;\n")
        .toString();

        List<Node> nodes = NodeUtility.getAllNodesFromCode(sourceThatHasNothingToReplace);
        List<Node> candidates = new ArrayList<Node>();
        for(Node node : nodes){
            CopyReplaceOperation cr = new CopyReplaceOperation();
            candidates.addAll(cr.exec(node));
        }

        assertThat(candidates.size()).isZero();
        return;
    }

    /**
     * 生成した修正パッチ候補に元のステートメントと同じものが含まれていないことをテスト
     */
    /*
    @Test public void testThatCandidatesDoesNotContainOriginal(){
        final String targetStatement = 
                "       la = lb;\n"; 

        final String source = new StringBuilder().append("")
        .append("public class A {\n")
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
        .append("public class A {\n")
        .append("    private void ma() {\n")
        .append("        String la = \"a\";\n")
        .append("        String lb = \"b\";\n")
        .append(expectedTargetStatement) 
        .append("    }\n")
        .append("}\n")
        .toString();

        List<Node> nodes = NodeUtility.getAllNodesFromCode(source);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : nodes){
            CopyReplaceOperation cr = new CopyReplaceOperation();
            List<CompilationUnit> cUnits = cr.exec(node);
            for (CompilationUnit cUnit : cUnits){
                LexicalPreservingPrinter.setup(cUnit);
                candidateSources.add(LexicalPreservingPrinter.print(cUnit));
            }
        }

        assertThat(candidateSources).doesNotContain(expectedSource);
        return;
    }

    /**
     * 少し複雑なコードで動作するかどうかテスト
     */
    @Test public void testForRun(){

        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private String fb = \"a\";\n")
            .append("    private void ma(String pa, String pb) {\n")
            .append("        String la = \"b\";\n")
            .append("        int num = 0;\n")
            .append("        this.mb(\"hoge\", \"fuga\");\n")
            .append("        if (true) {\n")
            .append("            la = \"c\";\n")
            .append("            while (true) {\n")
            .append("                this.mb(\"f\", \"g\");\n")
            .append("            }\n")
            .append("            return;\n")
            .append("        }\n")
            .append("        la = \"d\";\n")
            .append("    }\n")
            .append("    private void mb(String a, String b) {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        OperationTest operationTest = new OperationTest();
        //operationTest.test(targetSource, new ArrayList<String>(), new CopyReplaceOperation());
    }

    /**
     * switch文がある時にエラーが起きないかテスト
     */
    @Test public void testForSwitch(){

        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private String fa = \"a\";\n")
            .append("    private String fb = \"a\";\n")
            .append("    private void ma(String pa, String pb) {\n")
            .append("        String la = \"b\";\n")
            .append("        int num = 0;\n")
            .append("        switch (num) {\n")
            .append("        case 1:\n")
            .append("            num = 1;\n")
            .append("            break;\n")
            .append("        case 2:\n")
            .append("            num = 2;\n")
            .append("            break;\n")
            .append("        default:\n")
            .append("            num = 0;\n")
            .append("        }\n")
            .append("        la = \"d\";\n")
            .append("        this.mb(\"hoge\", \"fuga\");\n")
            .append("    }\n")
            .append("    private void mb(String a, String b) {\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        OperationTest operationTest = new OperationTest();
        //operationTest.test(targetSource, new ArrayList<String>(), new CopyReplaceOperation());
    }
}