package jp.posl.jprophet;

import jp.posl.jprophet.ProjectConfiguration;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectConfigurationTest{
    // 入力として用意するテスト用のプロジェクト
    private String projectPath = "src/test/resources/testGradleProject01";
    private ProjectConfiguration project = new ProjectConfiguration(this.projectPath, "temp");

    /**
     * ソースファイルを正しく取得できているかの検証
     */
    @Test public void testForSourceFilePaths(){
        List<String> expectedSourceFilePaths = new ArrayList<String>(Arrays.asList(
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java",
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App2.java"
         ));

        assertThat(this.project.getSourceFilePaths()).containsOnlyElementsOf(expectedSourceFilePaths);
    }

    /**
     * テストファイルを正しく取得できているかの検証
     */
    @Test public void testForTestFilePaths(){
        List<String> expectedTestFilePaths = new ArrayList<String>(Arrays.asList(
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/AppTest.java",
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/App2Test.java"
         ));

        assertThat(this.project.getTestFilePaths()).containsOnlyElementsOf(expectedTestFilePaths);
    }

    /**
     * クラスパスを正しく取得できているかの検証
     */
    @Test public void testForClasspaths(){
        List<String> expectedClassPaths = new ArrayList<String>(Arrays.asList("src/main/resources/junit-4.11.jar"));
        assertThat(this.project.getClassPaths()).containsOnlyElementsOf(expectedClassPaths);
    }

    /**
     * ビルド時のクラスファイル出力先のパスを正しく取得できているかの検証
     */
    @Test public void testForBuildPath(){
        assertThat(this.project.getBuildPath()).isEqualTo("temp");
    }
}