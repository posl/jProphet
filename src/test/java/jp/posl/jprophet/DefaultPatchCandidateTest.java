package jp.posl.jprophet;

import static org.junit.Assert.fail;
import java.io.IOException;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import org.junit.Test;

public class DefaultPatchCandidateTest {
    /**
     * getLineNumberのテスト
     * src/test/resources/test01.java のメソッド定義の先頭の行番号をテスト
     */
    @Test public void testForGetLineNumber() {
        String filePath = "src/test/resources/test01.java";
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(Paths.get(filePath));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        Node targetNode = compilationUnit.findRootNode().getChildNodes().get(0).getChildNodes().get(2);
        RepairUnit repairUnit = new RepairUnit(targetNode, 3, compilationUnit);
        PatchCandidate patchCandidate = new DefaultPatchCandidate(repairUnit, filePath, "test01");

        int actualLineNumber = patchCandidate.getLineNumber().get();
        assertThat(actualLineNumber).isEqualTo(3);
    }
}