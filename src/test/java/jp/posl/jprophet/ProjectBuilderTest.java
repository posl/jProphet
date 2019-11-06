package jp.posl.jprophet;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.project.GradleProject;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;


public class ProjectBuilderTest {
    private File buildDir;
    private RepairConfiguration config;
    private ProjectBuilder builder;

    /**
     * テスト入力用のプロジェクトの用意
     */
    @Before public void setUpProject(){
        this.buildDir = new File("./tmp/");
        this.config = new RepairConfiguration(buildDir.getPath(), null, new GradleProject("src/test/resources/testGradleProject01"));
        this.builder = new ProjectBuilder();
    }

    /**
     * ビルドが成功したかどうかテスト
     */
    @Test public void testForBuild() {
        boolean isSuccess = this.builder.build(config);
        assertThat(isSuccess).isTrue();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * クラスファイルが生成されているかどうかテスト
     */
    @Test public void testForBuildPath(){
        this.builder.build(config);
        assertThat(new File("./tmp/testGradleProject01").exists()).isTrue();
        assertThat(new File("./tmp/testGradleProject01/App.class").exists()).isTrue();
        assertThat(new File("./tmp/testGradleProject01/AppTest.class").exists()).isTrue();
        assertThat(new File("./tmp/testGradleProject01/App2.class").exists()).isTrue();
        assertThat(new File("./tmp/testGradleProject01/App2Test.class").exists()).isTrue();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

}