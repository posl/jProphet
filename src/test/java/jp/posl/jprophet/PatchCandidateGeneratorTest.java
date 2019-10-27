package jp.posl.jprophet;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PatchCandidateGeneratorTest {
    private PatchCandidateGenerator patchCandidateGenerator = new PatchCandidateGenerator();

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

        List<PatchCandidate> candidates = this.patchCandidateGenerator.exec(stubProject);

        // 各operationが呼ばれて修正パッチ候補を生成しているかテスト
        // 現状VariableReplacementOperationだけが修正パッチ候補を一つ生成する
        assertThat(candidates.size()).isEqualTo(1);
    }
}