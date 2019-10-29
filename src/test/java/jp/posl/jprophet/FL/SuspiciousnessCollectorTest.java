package jp.posl.jprophet.FL;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.FL.strategy.Coefficient;
import jp.posl.jprophet.FL.strategy.Jaccard;
import jp.posl.jprophet.FL.coverage.TestResults;
import jp.posl.jprophet.FL.coverage.CoverageCollector;

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
        this.config = new RepairConfiguration("./SCtmp/", null, new GradleProject(this.projectPath));
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
            testResults = coverageCollector.exec(SourceClassFilePaths, TestClassFilePaths);
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        SuspiciousnessCollector suspiciousnessCollector = new SuspiciousnessCollector(testResults, coefficient);
        suspiciousnessCollector.exec();
        List<Suspiciousness> suspiciousnesses = suspiciousnessCollector.getSuspiciousnesses();

        //Ifstatementの3行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline3 = suspiciousnesses.stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getPath()) && s.getLineNumber() == 3)
            .collect(Collectors.toList());
        assertThat(ifline3.size()).isEqualTo(1);
        double sus3 = 0.2; //1/(1+0+4)
        assertThat(ifline3.get(0).getValue()).isEqualTo(sus3);

        //Ifstatementの6行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline6 = suspiciousnesses.stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getPath()) && s.getLineNumber() == 6)
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