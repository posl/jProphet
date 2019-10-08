package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;                                                                                                                                                                  
import static org.assertj.core.api.Assertions.*;
import java.io.File;


public class ProjectBuilderTest {
    private File outDir;
    private ProjectConfiguration project;
    private ProjectBuilder builder;

    /**
     * テスト入力用のプロジェクトの用意
     */
    @Before public void setUpProject(){
        this.outDir = new File("./tmp/");
        this.project = new ProjectConfiguration("src/test/resources/testGradleProject01", outDir.getPath());
        this.builder = new ProjectBuilder();
    }

    /**
     * ビルドが成功したかどうかテスト
     */
    @Test public void testForBuild() {
        boolean isSuccess = this.builder.build(project);
        assertThat(isSuccess).isTrue();
        deleteDirectory(this.outDir);
    }

    /**
     * クラスファイルが生成されているかどうかテスト
     */
    @Test public void testForBuildPath(){
        this.builder.build(project);
        assertThat(new File("./tmp/testGradleProject01").exists()).isTrue();
        assertThat(new File("./tmp/testGradleProject01/App.class").exists()).isTrue();
        assertThat(new File("./tmp/testGradleProject01/AppTest.class").exists()).isTrue();
        assertThat(new File("./tmp/testGradleProject01/App2.class").exists()).isTrue();
        assertThat(new File("./tmp/testGradleProject01/App2Test.class").exists()).isTrue();
        deleteDirectory(this.outDir);
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