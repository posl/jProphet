package jp.posl.jprophet.project;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradleProjectTest{
    // 入力として用意するテスト用のプロジェクト
    private final String projectPath = "src/test/resources/testGradleProject01";
    private final Project project = new GradleProject(this.projectPath);

    
    /**
     * ソースファイルのFileLocatorを正しく取得できているかの検証
     */
    @Test public void testForSrcFileLocators(){
        List<String> expectedSrcFilePaths = new ArrayList<String>(Arrays.asList(
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java",
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App2.java"
        ));
        List<String> expectedSrcFileFqns = new ArrayList<String>(Arrays.asList(
            "testGradleProject01.App",
            "testGradleProject01.App2"
        ));

         
        List<FileLocator> actualFileLocators = this.project.getSrcFileLocators();
        assertThat(actualFileLocators.get(0).getPath()).contains(expectedSrcFilePaths.get(0));
        assertThat(actualFileLocators.get(1).getPath()).contains(expectedSrcFilePaths.get(1));
        assertThat(actualFileLocators.get(0).getFqn()).contains(expectedSrcFileFqns.get(0));
        assertThat(actualFileLocators.get(1).getFqn()).contains(expectedSrcFileFqns.get(1));
    }

    /**
     * テストファイルのFileLocatorを正しく取得できているかの検証
     */
    @Test public void testForTestFileLocators(){
        List<String> expectedTestFilePaths = new ArrayList<String>(Arrays.asList(
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/AppTest.java",
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/App2Test.java"
        ));
        List<String> expectedTestFileFqns = new ArrayList<String>(Arrays.asList(
            "testGradleProject01.AppTest",
            "testGradleProject01.App2Test"
        ));
         
        List<FileLocator> actualFileLocators = this.project.getTestFileLocators();
        // assertThat(actualFileLocators.get(0).getPath()).contains(expectedTestFilePaths.get(1));
        assertThat(actualFileLocators.get(1).getPath()).contains(expectedTestFilePaths.get(0));
        assertThat(actualFileLocators.get(0).getFqn()).contains(expectedTestFileFqns.get(1));
        assertThat(actualFileLocators.get(1).getFqn()).contains(expectedTestFileFqns.get(0));
    }

    /**
     * ソースファイルのパスを正しく取得できているかの検証
     */
    @Test public void testForSrcFilePaths(){
        List<String> expectedSrcFilePaths = new ArrayList<String>(Arrays.asList(
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java",
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App2.java"
        ));
         
        assertThat(this.project.getSrcFilePaths()).containsOnlyElementsOf(expectedSrcFilePaths);
    }

    /**
     * テストファイルのパスを正しく取得できているかの検証
     */
    @Test public void testForTestFilePaths(){
        List<String> expectedTestFilePaths = new ArrayList<String>(Arrays.asList(
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/AppTest.java",
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/App2Test.java"
        ));

        assertThat(this.project.getTestFilePaths()).containsOnlyElementsOf(expectedTestFilePaths);
    }

    /**
     * ソースファイルのFQNを正しく取得できているかの検証
     */
    @Test public void testForSrcFileFqns(){
        List<String> expectedSrcFileFqns = new ArrayList<String>(Arrays.asList(
            "testGradleProject01.App",
            "testGradleProject01.App2"
        ));
         
        assertThat(this.project.getSrcFileFqns()).containsOnlyElementsOf(expectedSrcFileFqns);
    }

    /**
     * テストファイルのFQNを正しく取得できているかの検証
     */
    @Test public void testForTestFileFqns(){
        List<String> expectedTestFileFqns = new ArrayList<String>(Arrays.asList(
            "testGradleProject01.AppTest",
            "testGradleProject01.App2Test"
        ));

        assertThat(this.project.getTestFileFqns()).containsOnlyElementsOf(expectedTestFileFqns);
    }

    /**
     * クラスパスを正しく取得できているかの検証
     */
    @Test public void testForClasspaths(){
        List<String> expectedClassPaths = new ArrayList<String>(Arrays.asList("src/main/resources/junit-4.11.jar"));
        assertThat(this.project.getClassPaths()).containsOnlyElementsOf(expectedClassPaths);
    }
}