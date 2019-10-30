package jp.posl.jprophet.fl.spectrumbased.statement;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.Project;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.spectrumbased.strategy.Coefficient;
import jp.posl.jprophet.fl.spectrumbased.strategy.Jaccard;
import jp.posl.jprophet.fl.spectrumbased.coverage.TestResults;
import jp.posl.jprophet.fl.spectrumbased.coverage.CoverageCollector;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;

public class SuspiciousnessCollectorTest{
    // 入力として用意するテスト用のプロジェクト
    private String projectPath;
    private RepairConfiguration config;
    private ProjectBuilder projectBuilder = new ProjectBuilder();
    private List<String> SourceClassFilePaths = new ArrayList<String>();
    private List<String> TestClassFilePaths = new ArrayList<String>();
    private TestResults testResults = new TestResults();
    private Coefficient coefficient = new Jaccard();



    @Before public void setup(){
        this.projectPath = "src/test/resources/testFLProject";
        this.config = new RepairConfiguration("./SCtmp/", null, new Project(this.projectPath));
        this.SourceClassFilePaths.add("testFLProject.Forstatement");
        this.SourceClassFilePaths.add("testFLProject.Ifstatement");
        this.SourceClassFilePaths.add("testFLProject.App");

        this.TestClassFilePaths.add("testFLProject.IfstatementTest3");
        this.TestClassFilePaths.add("testFLProject.AppTest");
        this.TestClassFilePaths.add("testFLProject.IfstatementTest2");
        this.TestClassFilePaths.add("testFLProject.IfstatementTest4");
        this.TestClassFilePaths.add("testFLProject.ForstatementTest");
        this.TestClassFilePaths.add("testFLProject.IfstatementTest");

        projectBuilder.build(config);
    }

    /**
     * SuspiciousnessCalculatorが動作しているかどうかのテスト
     */
    
     @Test public void testForSuspiciousnessCalculator(){

        CoverageCollector coverageCollector = new CoverageCollector("SCtmp");

        try {
            //testResults = coverageCollector.exec(sourceClass, testClass);
            testResults = coverageCollector.exec(SourceClassFilePaths, TestClassFilePaths);
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        SuspiciousnessCollector suspiciousnessCalculator = new SuspiciousnessCollector(testResults, coefficient);
        suspiciousnessCalculator.exec();

        //Ifstatementの3行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline3 = suspiciousnessCalculator.getSuspiciousnessList().stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getFQN()) && s.getLine() == 3)
            .collect(Collectors.toList());
        assertThat(ifline3.size()).isEqualTo(1);
        double sus3 = 0.2; //1/(1+0+4)
        assertThat(ifline3.get(0).getValue()).isEqualTo(sus3);

        //Ifstatementの6行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline6 = suspiciousnessCalculator.getSuspiciousnessList().stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getFQN()) && s.getLine() == 6)
            .collect(Collectors.toList());
        assertThat(ifline6.size()).isEqualTo(1);
        double sus6 = 0; //0/(0+1+1)
        assertThat(ifline6.get(0).getValue()).isEqualTo(sus6);

        try {
            FileUtils.deleteDirectory(new File("./SCtmp/"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}