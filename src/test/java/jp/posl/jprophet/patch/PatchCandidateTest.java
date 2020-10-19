package jp.posl.jprophet.patch;

import static org.junit.Assert.fail;
import java.io.IOException;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.operation.VariableReplacementOperation;


public class PatchCandidateTest {
    private PatchCandidate patchCandidate;
    private String filePath = "src/test/resources/test01.java";
    private CompilationUnit compilationUnit;
    private CompilationUnit fixedCompilationUnit;
    private CompilationUnit newFixedCompilationUnit;
    private String fqn = "test01";
    private String operation = "VariableReplacementOperation";

    @Before public void setUp() {
        try {
            this.compilationUnit = JavaParser.parse(Paths.get(this.filePath));
            this.fixedCompilationUnit = JavaParser.parse(Paths.get(this.filePath));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        Node node = compilationUnit.findRootNode().getChildNodes().get(0).getChildNodes().get(2);
        Node fixedNode = fixedCompilationUnit.findRootNode().getChildNodes().get(0).getChildNodes().get(2);
        fixedNode.getTokenRange().orElseThrow().getBegin().replaceToken(new JavaToken(node.getTokenRange().orElseThrow().getBegin().getRange().get(), JavaToken.Kind.PRIVATE.getKind(), "private", null, null));
        LexicalPreservingPrinter.setup(fixedCompilationUnit);
        this.newFixedCompilationUnit = JavaParser.parse(LexicalPreservingPrinter.print(fixedCompilationUnit));
        this.patchCandidate = new PatchCandidate(node, this.newFixedCompilationUnit, filePath, fqn, VariableReplacementOperation.class, 1);
    }

    /**
     * getLineNumberのテスト
     * src/test/resources/test01.java のメソッド定義の先頭の行番号をテスト
     */
    @Test public void testForGetLineNumber() {
        int actualLineNumber = this.patchCandidate.getLineNumber().get();
        assertThat(actualLineNumber).isEqualTo(3);
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
        CompilationUnit actualCompilationUnit = this.patchCandidate.getFixedCompilationUnit();
        CompilationUnit expectedCompilationUnit = this.newFixedCompilationUnit;
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
            .append("1       public class A {\n")
            .append("2          String a = \"a\";\n")
            .append("3     -    public void a() {\n")
            .append("3     +    private void a() {\n")
            .append("4             a = \"b\";\n")
            .append("5          }\n\n")
            .toString();

        assertThat(diff).isEqualTo(expectedDiff);
    }

}