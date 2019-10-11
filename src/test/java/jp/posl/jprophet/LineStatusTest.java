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
import jp.posl.jprophet.FL.LineStatus;
import java.io.File;

public class LineStatusTest{
    // 入力として用意するテスト用のプロジェクト
    private String projectPath;
    private ProjectConfiguration project;
    private ProjectBuilder projectBuilder = new ProjectBuilder();
    private List<String> SourceClassFilePaths = new ArrayList<String>();
    private List<String> TestClassFilePaths = new ArrayList<String>();
    private List<FullyQualifiedName> sourceClass = new ArrayList<FullyQualifiedName>();
	private List<FullyQualifiedName> testClass = new ArrayList<FullyQualifiedName>();
    private TestResults testresults = new TestResults();
    private int SCFPsize;
    private int TCFPsize;


    @Before public void setup(){
        this.projectPath = "src/test/resources/testFLProject";
        this.project = new ProjectConfiguration(this.projectPath, "./LStmp/");
        this.SourceClassFilePaths.add("testFLProject.Forstatement");
        this.SourceClassFilePaths.add("testFLProject.Ifstatement");
        this.SourceClassFilePaths.add("testFLProject.App");


        this.TestClassFilePaths.add("testFLProject.IfstatementTest3");
        this.TestClassFilePaths.add("testFLProject.AppTest");
        this.TestClassFilePaths.add("testFLProject.IfstatementTest2");
        this.TestClassFilePaths.add("testFLProject.IfstatementTest4");
        this.TestClassFilePaths.add("testFLProject.ForstatementTest");
        this.TestClassFilePaths.add("testFLProject.IfstatementTest");

        SCFPsize = SourceClassFilePaths.size();
        TCFPsize = TestClassFilePaths.size();

        projectBuilder.build(project);
         
        for(int k = 0; k < SCFPsize; k++){
			sourceClass.add(new FullyQualifiedName(SourceClassFilePaths.get(k)));
		}
		for(int k = 0; k < TCFPsize; k++){
			testClass.add(new FullyQualifiedName(TestClassFilePaths.get(k)));
        }
    
    }

    /**
     * LineStatusが動作しているかどうかのテスト
     */
    
     @Test public void testForLineStatus(){

        CoverageCollector executor = new CoverageCollector("TEtmp");
        
        try{
            testresults = executor.exec(sourceClass, testClass);

            //testFLProject.Ifstatementの12行目,9行目のカバレッジが正しいか確認
            int filenum = SourceClassFilePaths.indexOf("testFLProject.Ifstatement");
            
            LineStatus line12 = new LineStatus(testresults, 12, filenum);
            assertThat(line12.NCF).isEqualTo(1);
            assertThat(line12.NUF).isEqualTo(0);
            assertThat(line12.NCS).isEqualTo(0);
            assertThat(line12.NUS).isEqualTo(9);

            LineStatus line9 = new LineStatus(testresults, 9, filenum);
            assertThat(line9.NCF).isEqualTo(1);
            assertThat(line9.NUF).isEqualTo(0);
            assertThat(line9.NCS).isEqualTo(2);
            assertThat(line9.NUS).isEqualTo(7);

        }catch (Exception e){
			System.out.println("例外");
        }
        
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