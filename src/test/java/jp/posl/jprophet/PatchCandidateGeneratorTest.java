package jp.posl.jprophet;

import org.junit.Test;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.project.FileLocator;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;

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
        String fileFqn = "test01";
        FileLocator fileLocator = new FileLocator(filePath, fileFqn);
        List<FileLocator> fileLocators = new ArrayList<FileLocator>(Arrays.asList(fileLocator));
        Project stubProject = mock(GradleProject.class);
        when(stubProject.getSrcFileLocators()).thenReturn(fileLocators);

        List<PatchCandidate> candidates = this.patchCandidateGenerator.exec(stubProject);

        // 各operationが呼ばれて修正パッチ候補を生成しているかテスト
        // 現状VariableReplacementOperationだけが修正パッチ候補を一つ生成する
        assertThat(candidates.size()).isEqualTo(1);
    }
}