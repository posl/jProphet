package jp.posl.jprophet;

import org.junit.Test;

import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.project.FileLocator;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.project.GradleProject;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;


public class PatchCandidateGeneratorTest {
    private PatchCandidateGenerator patchCandidateGenerator = new PatchCandidateGenerator();

    /**
     * テスト用のプロジェクトを用意し
     * 全テンプレートoperationが呼び出されて修正パッチ候補が生成されているかテスト 
     */
    @Test public void testForExec(){
        // 一つのファイルを持ったプロジェクトのスタブを生成
        String filePath = "src/test/resources/test01.java";
        List<FileLocator> fileLocatorsForTest = new ArrayList<FileLocator>(Arrays.asList(new FileLocator(filePath, null)));
        Project stubProject = mock(GradleProject.class);
        when(stubProject.getSrcFileLocators()).thenReturn(fileLocatorsForTest);
        
        // 必ず一つのRepairUnitを返すAstOperationの匿名クラスを実装してGeneratorに注入
        AstOperation stubOperation = new AstOperation(){
            @Override
            public List<CompilationUnit> exec(Node targetNode) {
                return List.of(targetNode.findCompilationUnit().orElseThrow()); 
            }
        };

        List<PatchCandidate> candidates = this.patchCandidateGenerator.exec(stubProject, List.of(stubOperation));

        int astNodeNumOfTest01 = 17;
        assertThat(candidates.size()).isEqualTo(astNodeNumOfTest01);
    }
}