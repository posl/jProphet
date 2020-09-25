package jp.posl.jprophet.patch;

import static org.junit.Assert.fail;
import java.io.IOException;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.operation.VariableReplacementOperation;
import jp.posl.jprophet.patch.DiffWithType.ModifyType;


public class DefaultPatchCandidateTest {
    
    private PatchCandidate patchCandidate;
    private String filePath = "src/test/resources/test01.java";
    private CompilationUnit compilationUnit;
    private CompilationUnit fixedCompilationUnit;
    private String fqn = "test01";
    private String operation = "VariableReplacementOperation";

    @Before public void setUp() {
        try {
            this.compilationUnit = JavaParser.parse(Paths.get(this.filePath));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        Node node = compilationUnit.findRootNode().getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(2).getChildNodes().get(0).getChildNodes().get(0).getChildNodes().get(1);
        Node fixedNode = new StringLiteralExpr("c");
        String fixedSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    String a = \"a\";\n")
            .append("    public void a() {\n")
            .append("        a = \"c\";\n")
            .append("    }\n")
            .append("}")
            .toString();
        fixedCompilationUnit = JavaParser.parse(fixedSource);
        
        DiffWithType diffWithType = new DiffWithType(ModifyType.CHANGE, node, fixedNode);
        this.patchCandidate = new DefaultPatchCandidate(diffWithType, filePath, fqn, VariableReplacementOperation.class, 1);
    }

    /**
     * getLineNumberのテスト
     * src/test/resources/test01.java のメソッド定義の先頭の行番号をテスト
     */
    
    @Test public void testForGetLineNumber() {
        int actualLineNumber = this.patchCandidate.getLineNumber().get();
        assertThat(actualLineNumber).isEqualTo(4);
    }

    /**
     * getFilePathのテスト
     */
    
    @Test public void testForGetFilePath() {
        String actualFilePath = this.patchCandidate.getFilePath();
        String expectedFilePath = this.filePath;
        assertThat(actualFilePath).isEqualTo(expectedFilePath);
    }

    /**
     * getFqnのテスト
     */
    
    @Test public void testForGetFqn() {
        String actualFqn = this.patchCandidate.getFqn();
        String expectedFqn = this.fqn;
        assertThat(actualFqn).isEqualTo(expectedFqn);
    }

    /**
     * getCompilationUnitのテスト
     */
    
    @Test public void testForGetCompilationUnit() {
        CompilationUnit actualCompilationUnit = this.patchCandidate.getCompilationUnit();
        CompilationUnit expectedCompilationUnit = this.fixedCompilationUnit;
        assertThat(actualCompilationUnit).isEqualTo(expectedCompilationUnit);
    }

    @Test public void testForGetAppliedOperation() {
        String actualAppliedOperation = this.patchCandidate.getAppliedOperation();
        String expectedAppliedOperation = this.operation;
        assertThat(actualAppliedOperation).isEqualTo(expectedAppliedOperation);
    }

    /**
     * toStringのテスト
     */
    
    @Test public void testForToString() {
        String diff = this.patchCandidate.toString();
        String expectedDiff = new StringBuilder().append("")
            .append("ID : 1\n")
            .append("fixed file path : src/test/resources/test01.java\n")
            .append("used operation  : VariableReplacementOperation\n\n")
            .append("2          String a = \"a\";\n")
            .append("3          public void a() {\n")
            .append("4     -       a = \"b\";\n")
            .append("4     +       a = \"c\";\n")
            .append("5          }\n")
            .append("6       }\n\n")
            .toString();

        System.out.println(diff);
        System.out.println(expectedDiff);
        assertThat(diff).isEqualTo(expectedDiff);
    }
    
}