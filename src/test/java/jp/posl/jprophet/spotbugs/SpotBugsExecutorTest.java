package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.ProjectConfiguration;

public class SpotBugsExecutorTest {

    private ProjectConfiguration testProject;
    private File outDir;                        // テストプロジェクトのクラスパス
    private File resultDir;                     // SpotBugsの実行結果を格納するパス


    @Before
    public void setUpProject() {
        this.outDir = new File("./src/test/resources/testSBproject01/build/classes");
        this.resultDir = new File("./src/test/resources/testSBproject01/build/reports/spotbugs");
        this.testProject = new ProjectConfiguration("src/test/resources/testSBProject01", outDir.getPath());
    }

    @Test
    public void testForExec() {
        
        SpotBugsExecutor executor = new SpotBugsExecutor(testProject, resultDir);
        executor.exec();

        /*
        assertThat(new File("./result/App-warnings.txt").exists()).isTrue();    //ソースファイルは実行対象         assertThat(new File("./result/App-warnings.txt").exists()).isTrue();    //ソースファイルは実行対象 
        assertThat(new File("./result/App2-warnings.txt").exists()).isTrue();
        assertThat(new File("./result/App3-warnings.txt").exists()).isTrue(); 
        assertThat(new File("./result/AppTest-warnings.txt").exists()).isFalse();   //テストファイルは実行対象外
        */

        assertThat(new File(executor.getResultFilePath()).exists()).isTrue();
        deleteDirectory(resultDir);
        deleteDirectory(outDir);

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