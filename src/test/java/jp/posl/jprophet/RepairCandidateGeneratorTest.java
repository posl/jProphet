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
        
        // 必ず一つのRepairUnitを返すAstOperationの匿名クラスを実装してGeneratorに注入
        AstOperation stubOperation = new AstOperation(){
            @Override
            public List<RepairUnit> exec(RepairUnit repairUnit) {
                return List.of(new RepairUnit(null, 0, null)); 
            }
        };

        List<RepairCandidate> candidates = this.repairCandidateGenerator.exec(stubProject, List.of(stubOperation));

        int astNodeNumOfTest01 = 17;
        assertThat(candidates.size()).isEqualTo(astNodeNumOfTest01);
    }
}