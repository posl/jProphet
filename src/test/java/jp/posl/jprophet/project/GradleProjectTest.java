package jp.posl.jprophet.project;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import java.util.List;

public class GradleProjectTest{
    // 入力として用意するテスト用のプロジェクト
    private final String projectPath = "src/test/resources/testGradleProject01";
    private final Project project = new GradleProject(this.projectPath);

    
    /**
     * ソースファイルのFileLocatorを正しく取得できているかの検証
     */
    @Test public void testForSrcFileLocators(){
        FileLocator expectedFileLocator1 = new FileLocator(
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java",
            "testGradleProject01.App"
        );
        FileLocator expectedFileLocator2 = new FileLocator(
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App2.java",
            "testGradleProject01.App2"
        );
        
        List<FileLocator> actualFileLocators = this.project.getSrcFileLocators();
        assertThat(actualFileLocators).extracting((f) -> tuple(f.getPath(), f.getFqn()))
            .contains(tuple(expectedFileLocator1.getPath(), expectedFileLocator1.getFqn()))
            .contains(tuple(expectedFileLocator2.getPath(), expectedFileLocator2.getFqn()));
    }

    /**
     * テストファイルのFileLocatorを正しく取得できているかの検証
     */
    @Test public void testForTestFileLocators(){
        FileLocator expectedFileLocator1 = new FileLocator(
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/AppTest.java",
            "testGradleProject01.AppTest"
        );
        FileLocator expectedFileLocator2 = new FileLocator(
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/App2Test.java",
            "testGradleProject01.App2Test"
        );
        
        List<FileLocator> actualFileLocators = this.project.getTestFileLocators();
        assertThat(actualFileLocators).extracting((f) -> tuple(f.getPath(), f.getFqn()))
            .contains(tuple(expectedFileLocator1.getPath(), expectedFileLocator1.getFqn()))
            .contains(tuple(expectedFileLocator2.getPath(), expectedFileLocator2.getFqn()));
    }

    /**
     * ソースファイルのパスを正しく取得できているかの検証
     */
    @Test public void testForSrcFilePaths(){
        List<String> expectedSrcFilePaths = List.of(
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java",
            "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App2.java"
        );
         
        assertThat(this.project.getSrcFilePaths()).containsOnlyElementsOf(expectedSrcFilePaths);
    }

    /**
     * テストファイルのパスを正しく取得できているかの検証
     */
    @Test public void testForTestFilePaths(){
        List<String> expectedTestFilePaths = List.of(
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/AppTest.java",
            "src/test/resources/testGradleProject01/src/test/java/testGradleProject01/App2Test.java"
        );

        assertThat(this.project.getTestFilePaths()).containsOnlyElementsOf(expectedTestFilePaths);
    }

    /**
     * ソースファイルのFQNを正しく取得できているかの検証
     */
    @Test public void testForSrcFileFqns(){
        List<String> expectedSrcFileFqns = List.of(
            "testGradleProject01.App",
            "testGradleProject01.App2"
        );
         
        assertThat(this.project.getSrcFileFqns()).containsOnlyElementsOf(expectedSrcFileFqns);
    }

    /**
     * テストファイルのFQNを正しく取得できているかの検証
     */
    @Test public void testForTestFileFqns(){
        List<String> expectedTestFileFqns = List.of(
            "testGradleProject01.AppTest",
            "testGradleProject01.App2Test"
        );

        assertThat(this.project.getTestFileFqns()).containsOnlyElementsOf(expectedTestFileFqns);
    }

    /**
     * クラスパスを正しく取得できているかの検証
     */
    @Test public void testForClasspaths(){
        List<String> expectedClassPaths = List.of("src/main/resources/junit-4.11.jar");
        assertThat(this.project.getClassPaths()).containsOnlyElementsOf(expectedClassPaths);
    }
}