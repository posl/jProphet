package jp.posl.jprophet.patch;

import static org.junit.Assert.fail;
import java.io.IOException;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.RepairUnit;


public class DefaultPatchCandidateTest {
    private PatchCandidate patchCandidate;
    private String filePath = "src/test/resources/test01.java";
    private CompilationUnit compilationUnit;
    private String fqn = "test01";

    @Before public void setUp() {
        try {
            this.compilationUnit = JavaParser.parse(Paths.get(this.filePath));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        Node targetNode = compilationUnit.findRootNode().getChildNodes().get(0).getChildNodes().get(2);
        RepairUnit repairUnit = new RepairUnit(targetNode, 3, compilationUnit);
        this.patchCandidate = new DefaultPatchCandidate(repairUnit, filePath, fqn);
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
        CompilationUnit actualCompilationUnit = this.patchCandidate.getCompilationUnit();
        CompilationUnit expectedCompilationUnit = this.compilationUnit;
        assertThat(actualCompilationUnit).isEqualTo(expectedCompilationUnit);
    }

}