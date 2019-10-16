package jp.posl.jprophet;

import jp.posl.jprophet.ProjectConfiguration;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import jp.posl.jprophet.FL.FullyQualifiedName;
import jp.posl.jprophet.FL.TestResults;
import jp.posl.jprophet.FL.CoverageCollector;
import jp.posl.jprophet.FL.StatementStatus;
import java.io.File;

public class StatementStatusTest{
    // 入力として用意するテスト用のプロジェクト
    private String projectPath;
    private ProjectConfiguration project;
    private ProjectBuilder projectBuilder = new ProjectBuilder();
    private List<String> sourceClassFilePaths = new ArrayList<String>();
    private List<String> testClassFilePaths = new ArrayList<String>();
    private List<FullyQualifiedName> sourceClass = new ArrayList<FullyQualifiedName>();
	private List<FullyQualifiedName> testClass = new ArrayList<FullyQualifiedName>();
    private TestResults testResults = new TestResults();
    private int SCFPsize;
    private int TCFPsize;


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

        SCFPsize = sourceClassFilePaths.size();
        TCFPsize = testClassFilePaths.size();

        projectBuilder.build(project);
         
        for(int k = 0; k < SCFPsize; k++){
			sourceClass.add(new FullyQualifiedName(sourceClassFilePaths.get(k)));
		}
		for(int k = 0; k < TCFPsize; k++){
			testClass.add(new FullyQualifiedName(testClassFilePaths.get(k)));
        }
    
    }

    /**
     * StatementStatusが動作しているかどうかのテスト
     */
    
     @Test public void testForLineStatus(){

        CoverageCollector coverageCollector = new CoverageCollector("LStmp");
        
        try{
            testResults = coverageCollector.exec(sourceClass, testClass);
        }catch (Exception e){
			System.out.println("例外");
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
        
        
        deleteDirectory(new File("./LStmp/"));

    }

    /**
     * ディレクトリをディレクトリの中のファイルごと再帰的に削除する 
     * @param dir 削除対象ディレクトリ
     */
    private void deleteDirectory(File dir){
        if(dir.listFiles() != null){
            for(File file : dir.listFiles()){
                if(file.isFile())
                    file.delete();
                else
                    deleteDirectory(file);
            }
        }
        dir.delete();
    }
}