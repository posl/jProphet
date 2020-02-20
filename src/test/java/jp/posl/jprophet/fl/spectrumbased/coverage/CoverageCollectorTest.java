package jp.posl.jprophet.fl.spectrumbased.coverage;

import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.RepairConfiguration;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;

public class CoverageCollectorTest{
    // 入力として用意するテスト用のプロジェクト
    private String projectPath;
    private RepairConfiguration config;
    private ProjectBuilder projectBuilder = new ProjectBuilder();
    private List<String> SourceClassFilePaths = new ArrayList<String>();
    private List<String> TestClassFilePaths = new ArrayList<String>();
    private TestResults testResults = new TestResults();

    private String loopProjectPath;
    private RepairConfiguration loopConfig;
    private ProjectBuilder loopProjectBuilder = new ProjectBuilder();
    private List<String> loopSourceClassFilePaths = new ArrayList<String>();
    private List<String> loopTestClassFilePaths = new ArrayList<String>();
    private TestResults loopTestResults = new TestResults();


    @Before public void setup(){
        this.projectPath = "src/test/resources/testFLProject";
        this.config = new RepairConfiguration("./TEtmp/", null, new GradleProject(this.projectPath));
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

        this.loopProjectPath = "src/test/resources/testGradleProject03";
        this.loopConfig = new RepairConfiguration("./looptmp/", null, new GradleProject(this.loopProjectPath));
        this.loopSourceClassFilePaths.add("testGradleProject02.App");
        this.loopSourceClassFilePaths.add("testGradleProject02.App2");
        this.loopTestClassFilePaths.add("testGradleProject02.App2Test");
        this.loopTestClassFilePaths.add("testGradleProject02.AppTest");


        loopProjectBuilder.build(loopConfig);
    
    }

    /**
     * CoverageCollectorが動作しているかどうかのテスト
     */
    
     @Test public void testForTestExecutor(){

        CoverageCollector coverageCollector = new CoverageCollector("TEtmp");

        try {
            testResults = coverageCollector.exec(SourceClassFilePaths, TestClassFilePaths);
        } 
        catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        
        //失敗,成功したテストの個数が正しいか確認
        assertThat(testResults.getFailedTestResults().size()).isEqualTo(1);
        assertThat(testResults.getSuccessedTestResults().size()).isEqualTo(9);

        List<String> Smethodlist = testResults.getSuccessedTestResults().stream()
            //.map(s -> s.getMethodName().value)
            .map(s -> s.getMethodName())
            .collect(Collectors.toList());

        List<String> Fmethodlist = testResults.getFailedTestResults().stream()
            //.map(s -> s.getMethodName().value)
            .map(s -> s.getMethodName())
            .collect(Collectors.toList());

        //失敗,成功したテストの一覧が正しいか確認
        assertThat(Smethodlist).contains("testFLProject.AppTest.testAppHasAGreeting");
        assertThat(Smethodlist).contains("testFLProject.ForstatementTest.Ftest0_1");
        assertThat(Smethodlist).contains("testFLProject.ForstatementTest.Ftest1_1");
        assertThat(Smethodlist).contains("testFLProject.ForstatementTest.Ftest2_2");
        assertThat(Smethodlist).contains("testFLProject.ForstatementTest.Ftest3_6");
        assertThat(Smethodlist).contains("testFLProject.IfstatementTest.test1_2");
        assertThat(Smethodlist).contains("testFLProject.IfstatementTest.test2_3");
        assertThat(Smethodlist).contains("testFLProject.IfstatementTest2.test3_4");
        assertThat(Smethodlist).contains("testFLProject.IfstatementTest4.test0_0");
        assertThat(Fmethodlist).contains("testFLProject.IfstatementTest3.test4_5");

        try {
            FileUtils.deleteDirectory(new File("./TEtmp/"));
            FileUtils.deleteDirectory(new File("./looptmp/"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test public void testForLoop(){
        CoverageCollector coverageCollector = new CoverageCollector("looptmp");
        boolean isSuccess;
        try {
            loopTestResults = coverageCollector.exec(loopSourceClassFilePaths, loopTestClassFilePaths);
            isSuccess = true;
        } 
        catch (Exception e){
            isSuccess = false;
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        assertThat(isSuccess).isTrue();

        try {
            FileUtils.deleteDirectory(new File("./TEtmp/"));
            FileUtils.deleteDirectory(new File("./looptmp/"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}