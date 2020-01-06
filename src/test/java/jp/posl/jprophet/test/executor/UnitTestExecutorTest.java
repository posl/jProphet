package jp.posl.jprophet.test.executor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.MavenProject;
import jp.posl.jprophet.project.Project;

public class UnitTestExecutorTest {

    private TestExecutor testExecutor;
    private Project correctProject;
    private Project errorProject;
    private RepairConfiguration correctConfig;
    private RepairConfiguration errorConfig;
    private File buildDir;

    /**
     * テスト入力用のプロジェクトの用意
     */
    @Before
    public void setUpProject() {
        this.buildDir = new File("./tmp/");
        this.correctProject = new MavenProject("src/test/resources/math");
        this.correctConfig = new RepairConfiguration(buildDir.getPath(), null, correctProject);
        this.errorProject = new GradleProject("src/test/resources/testGradleProject02");
        this.errorConfig = new RepairConfiguration(buildDir.getPath(), null, errorProject);
        this.testExecutor = new UnitTestExecutor();
    }

    /**
     * テスト実行が成功したかどうかテスト
     */
    @Test
    public void testForExecute() {

        boolean isSuccess01 = this.testExecutor.exec(correctConfig).canEndRepair();
        assertThat(isSuccess01).isTrue();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        /*
        boolean isSuccess02 = this.testExecutor.exec(errorConfig).canEndRepair();
        assertThat(isSuccess02).isFalse();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        */
    }
}
