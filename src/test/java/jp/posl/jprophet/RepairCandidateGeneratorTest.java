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
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RepairCandidateGeneratorTest {
    
    @Mock private CondRefinementOperation condRefinementOperation = new CondRefinementOperation();
	@Mock private CondIntroductionOperation condIntroductionOperation = new CondIntroductionOperation(); 
	@Mock private CtrlFlowIntroductionOperation ctrlFlowIntroductionOperation = new CtrlFlowIntroductionOperation(); 
	@Mock private InsertInitOperation insertInitOperation = new InsertInitOperation(); 
	@Mock private ValueReplacementOperation valueReplacementOperation =  new ValueReplacementOperation();
	@Mock private CopyReplaceOperation copyReplaceOperation = new CopyReplaceOperation();
    @InjectMocks
    private RepairCandidateGenerator repairCandidateGenerator = new RepairCandidateGenerator();
    @Before public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test public void test(){
        // 一つのファイルを持ったプロジェクトのスタブを生成
        String filePath = "src/test/resources/test01.java";
        List<String> filePathsForTest = new ArrayList<String>(Arrays.asList(filePath));
        ProjectConfiguration stubProject = mock(ProjectConfiguration.class);
        when(stubProject.getFilePaths()).thenReturn(filePathsForTest);

        // 全てのRepairUnitに対して各テンプレート適用操作につきが一つのRepairUnitを返すようにモックを生成
        List<RepairUnit> units = new ArrayList<RepairUnit>(Arrays.asList(new RepairUnit(null, null))); 
        when(condRefinementOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(condIntroductionOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(ctrlFlowIntroductionOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(insertInitOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(valueReplacementOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        when(copyReplaceOperation.exec(Mockito.any(RepairUnit.class))).thenReturn(units);
        List<AbstractRepairCandidate> candidates = repairCandidateGenerator.exec(stubProject);


        assertThat(candidates.size()).isEqualTo(36);

    }
}