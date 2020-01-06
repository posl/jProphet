package jp.posl.jprophet;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.MavenProject;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;


public class ProjectBuilderTest {
    private File buildDir;
    private RepairConfiguration gradleConfig, mavenConfig;
    private ProjectBuilder builder;

    /**
     * テスト入力用のプロジェクトの用意
     */
    @Before public void setUpProject(){
        this.buildDir = new File("./tmp/");
        this.gradleConfig = new RepairConfiguration(buildDir.getPath(), null, new GradleProject("src/test/resources/testGradleProject01"));
        this.mavenConfig = new RepairConfiguration(buildDir.getPath(), null, new MavenProject("src/test/resources/lang"));
        this.builder = new ProjectBuilder();
    }

    /**
     * gradleプロジェクトのビルドが成功したかどうかテスト
     */
    @Test public void testForBuildGradleProject() {
        boolean isSuccess = this.builder.build(gradleConfig);
        assertThat(isSuccess).isTrue();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * gradleプロジェクトのクラスファイルが生成されているかどうかテスト
     */
    @Test public void testForBuildGradleProjectPath(){
        this.builder.build(gradleConfig);
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

    /**
     * mavenプロジェクトのビルドが成功したかどうかテスト
     */
    @Test public void testForBuildMavenProject() {
        boolean isSuccess = this.builder.build(mavenConfig);
        assertThat(isSuccess).isTrue();
        
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        
    }

    /**
     * mavenプロジェクトのクラスファイルが生成されているかどうかテスト
     */
    
     @Test public void testForBuildMavenProjectPath(){
        this.builder.build(mavenConfig);
        assertThat(new File("./tmp/testMavenProject01").exists()).isTrue();
        assertThat(new File("./tmp/testMavenProject01/App.class").exists()).isTrue();
        assertThat(new File("./tmp/testMavenProject01/AppTest.class").exists()).isTrue();
        assertThat(new File("./tmp/testMavenProject01/App2.class").exists()).isTrue();
        assertThat(new File("./tmp/testMavenProject01/App2Test.class").exists()).isTrue();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
}