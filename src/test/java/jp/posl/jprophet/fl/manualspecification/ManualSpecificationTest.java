package jp.posl.jprophet.fl.manualspecification;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.manualspecification.strategy.*;


public class ManualSpecificationTest{
    // 入力として用意するテスト用のプロジェクト
    final private String projectPath = "src/test/resources/testFLProject";
    final private RepairConfiguration config = new RepairConfiguration("BStmp", null, new GradleProject(this.projectPath));
    private List<SpecificationStrategy> specificationStrategyList = new ArrayList<SpecificationStrategy>();


    @Before public void setUp(){
        this.specificationStrategyList.add(new SpecificOneLineBug("testFLProject.Ifstatement", 7, 0.5));
        this.specificationStrategyList.add(new SpecificBugsByRange("testFLProject.App", 8, 10, 0.7));
        this.specificationStrategyList.add(new SpecificBugsWavy("testFLProject.Ifstatement", 2, 1, 2, 0.1));
        this.specificationStrategyList.add(new SpecificBugsWavy("testFLProject.Ifstatement", 16, 1, 3, 0.2));
        this.specificationStrategyList.add(new SpecificBugsWavy("testFLProject.Forstatement",10, 0.9, 2, 0.05));
        this.specificationStrategyList.add(new SpecificBugsWavy("testFLProject.App",4, 1, 2, 0.7));
    }
    /**
     * BugSpecification動作しているかどうかのテスト
     */
    @Test public void testForBugSpecification(){
        ManualSpecification manualSpecification = new ManualSpecification(config, this.specificationStrategyList);
        List<Suspiciousness> suspiciousnessList = manualSpecification.exec().toList();

        
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 7).getValue()).isEqualTo(0.5);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.App", 8).getValue()).isEqualTo(0.7);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.App", 9).getValue()).isEqualTo(0.7);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.App", 10).getValue()).isEqualTo(0.7);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 2).getValue()).isEqualTo(1);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 1).getValue()).isEqualTo(1 - 0.1);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 3).getValue()).isEqualTo(1 - 0.1);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 4).getValue()).isEqualTo(1 - 0.1 * 2);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 16).getValue()).isEqualTo(1);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 15).getValue()).isEqualTo(1 - 0.2);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 17).getValue()).isEqualTo(1 - 0.2);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 14).getValue()).isEqualTo(1 - 0.2 * 2);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 13).getValue()).isEqualTo(1 - 0.2 * 3);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Forstatement", 10).getValue()).isEqualTo(0.9);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Forstatement", 11).getValue()).isEqualTo(0.9 - 0.05);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Forstatement", 9).getValue()).isEqualTo(0.9 - 0.05);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Forstatement", 8).getValue()).isEqualTo(0.9 - 0.05 * 2);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.App", 3).getValue()).isEqualTo(1 - 0.7);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.App", 5).getValue()).isEqualTo(1 - 0.7);

        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 5).getValue()).isEqualTo(0);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Ifstatement", 12).getValue()).isEqualTo(0);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.Forstatement", 1).getValue()).isEqualTo(0);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.App", 2).getValue()).isEqualTo(0);
        assertThat(getSuspiciousness(suspiciousnessList, "testFLProject.App", 6).getValue()).isEqualTo(0);
        
    }

    private Suspiciousness getSuspiciousness(List<Suspiciousness> suspiciousnessList, String fqn, int line){
        List<Suspiciousness> suspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getFQN()) && s.getLineNumber() == line)
            .collect(Collectors.toList());
        return suspiciousness.get(0);
    }


}
