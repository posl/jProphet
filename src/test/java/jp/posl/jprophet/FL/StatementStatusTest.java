package jp.posl.jprophet.FL;

import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.util.Directory;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.FL.coverage.TestResults;
import jp.posl.jprophet.FL.coverage.CoverageCollector;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class StatementStatusTest{
    // 入力として用意するテスト用のプロジェクト
    private String projectPath;
    private ProjectConfiguration project;
    private ProjectBuilder projectBuilder = new ProjectBuilder();
    private List<String> sourceClassFilePaths = new ArrayList<String>();
    private List<String> testClassFilePaths = new ArrayList<String>();
    private TestResults testResults = new TestResults();


    @Before public void setup(){
        this.projectPath = "src/test/resources/testFLProject";
        this.project = new ProjectConfiguration(this.projectPath, "./LStmp/");
        this.sourceClassFilePaths.add("testFLProject.Forstatement");
        this.sourceClassFilePaths.add("testFLProject.Ifstatement");
        this.sourceClassFilePaths.add("testFLProject.App");


        this.testClassFilePaths.add("testFLProject.IfstatementTest3");
        this.testClassFilePaths.add("testFLProject.AppTest");
        this.testClassFilePaths.add("testFLProject.IfstatementTest2");
        this.testClassFilePaths.add("testFLProject.IfstatementTest4");
        this.testClassFilePaths.add("testFLProject.ForstatementTest");
        this.testClassFilePaths.add("testFLProject.IfstatementTest");

        projectBuilder.build(project);
    
    }

    /**
     * StatementStatusが動作しているかどうかのテスト
     */
    
     @Test public void testForLineStatus(){

        CoverageCollector coverageCollector = new CoverageCollector("LStmp");
        
        try {
            //testResults = coverageCollector.exec(sourceClass, testClass);
            testResults = coverageCollector.exec(sourceClassFilePaths, testClassFilePaths);
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        //testFLProject.Ifstatementの12行目,9行目のカバレッジが正しいか確認
        int fileNum = sourceClassFilePaths.indexOf("testFLProject.Ifstatement");
            
        StatementStatus line12 = new StatementStatus(testResults, 12, fileNum);
        assertThat(line12.getNumberOfFailedTestsCoveringStatement()).isEqualTo(1);
        assertThat(line12.getNumberOfFailedTestsNotCoveringStatement()).isEqualTo(0);
        assertThat(line12.getNumberOfSuccessedTestsCoveringStatement()).isEqualTo(0);
        assertThat(line12.getNumberOfSuccessedTestsNotCoveringStatement()).isEqualTo(9);

        StatementStatus line9 = new StatementStatus(testResults, 9, fileNum);
        assertThat(line9.getNumberOfFailedTestsCoveringStatement()).isEqualTo(1);
        assertThat(line9.getNumberOfFailedTestsNotCoveringStatement()).isEqualTo(0);
        assertThat(line9.getNumberOfSuccessedTestsCoveringStatement()).isEqualTo(2);
        assertThat(line9.getNumberOfSuccessedTestsNotCoveringStatement()).isEqualTo(7);
        
        
        Directory.delete(new File("./LStmp/"));

    }

}