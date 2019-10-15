package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

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
    
    @Mock private CondRefinementOperation condRefinementOperation = new CondRefinementOperation();
	@Mock private CondIntroductionOperation condIntroductionOperation = new CondIntroductionOperation(); 
	@Mock private CtrlFlowIntroductionOperation ctrlFlowIntroductionOperation = new CtrlFlowIntroductionOperation(); 
	@Mock private InsertInitOperation insertInitOperation = new InsertInitOperation(); 
	@Mock private VariableReplacementOperation valueReplacementOperation =  new VariableReplacementOperation();
	@Mock private CopyReplaceOperation copyReplaceOperation = new CopyReplaceOperation();
    @InjectMocks
    private RepairCandidateGenerator repairCandidateGenerator = new RepairCandidateGenerator();
    @Before public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test public void test(){
        // 一つのファイルを持ったプロジェクトのスタブを生成
        String filePath = "src/test/resources/test01.java";
        List<String> filePathsForTest = new ArrayList<String>(Arrays.asList(filePath));
        ProjectConfiguration stubProject = mock(ProjectConfiguration.class);
        when(stubProject.getSourceFilePaths()).thenReturn(filePathsForTest);

        // 全てのRepairUnitに対して各テンプレート適用操作につきが一つのRepairUnitを返すようにモックを生成
        CompilationUnit compilationUnit;
        try{
            compilationUnit = JavaParser.parse(Paths.get(filePath));
        }
        catch (IOException e){
            e.printStackTrace();
            return;
        }
        List<RepairUnit> units = new ArrayList<RepairUnit>(Arrays.asList(new RepairUnit(compilationUnit, 0, compilationUnit))); 
        when(condRefinementOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(condIntroductionOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(ctrlFlowIntroductionOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(insertInitOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(valueReplacementOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(copyReplaceOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);

        // stubProject(6個のASTノードを持つソースファイル一つ)を入力にとり，各operationが6回ずつ呼び出されているかをテスト
        List<AbstractRepairCandidate> candidates = repairCandidateGenerator.exec(stubProject);
        Mockito.verify(condRefinementOperation, times(6)).exec(Mockito.any(RepairUnit.class));
        Mockito.verify(condIntroductionOperation, times(6)).exec(Mockito.any(RepairUnit.class));
        Mockito.verify(ctrlFlowIntroductionOperation, times(6)).exec(Mockito.any(RepairUnit.class));
        Mockito.verify(insertInitOperation, times(6)).exec(Mockito.any(RepairUnit.class));
        Mockito.verify(valueReplacementOperation, times(6)).exec(Mockito.any(RepairUnit.class));
        Mockito.verify(copyReplaceOperation, times(6)).exec(Mockito.any(RepairUnit.class));

        // 最終的な修正パッチ候補が6*6=36個生成されているか(念の為)テスト
        assertThat(candidates.size()).isEqualTo(36);

    }
}