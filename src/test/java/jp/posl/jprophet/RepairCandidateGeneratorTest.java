package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jp.posl.jprophet.operation.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;


public class RepairCandidateGeneratorTest {
    private RepairCandidateGenerator repairCandidateGenerator = new RepairCandidateGenerator();

    /**
     * テスト用のプロジェクトを用意し
     * 全テンプレートoperationが呼び出されて修正パッチ候補が生成されているかテスト 
     */
    @Test public void testForExec(){
        // 一つのファイルを持ったプロジェクトのスタブを生成
        String filePath = "src/test/resources/test01.java";
        List<String> filePathsForTest = new ArrayList<String>(Arrays.asList(filePath));
        Project stubProject = mock(Project.class);
        when(stubProject.getSourceFilePaths()).thenReturn(filePathsForTest);

        List<RepairCandidate> candidates = this.repairCandidateGenerator.exec(stubProject);

        // 各operationが呼ばれて修正パッチ候補を生成しているかテスト
        // 現状VariableReplacementOperationだけが修正パッチ候補を一つ生成する
        assertThat(candidates.size()).isEqualTo(1);
    }
}