package jp.posl.jprophet.test.executor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.spectrumbased.ExecutionTest;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;

public class UnitTestExecutorTest {

    private TestExecutor testExecutor;
    private Project correctProject;
    private Project errorProject;
    private Project loopProject;
    private RepairConfiguration correctConfig;
    private RepairConfiguration errorConfig;
    private RepairConfiguration loopConfig;
    private File buildDir;

    /**
     * テスト入力用のプロジェクトの用意
     */
    @Before
    public void setUpProject() {
        this.buildDir = new File("./tmp/");
        this.correctProject = new GradleProject("src/test/resources/testGradleProject01");
        this.correctConfig = new RepairConfiguration(buildDir.getPath(), null, correctProject, null);
        this.errorProject = new GradleProject("src/test/resources/testGradleProject02");
        this.errorConfig = new RepairConfiguration(buildDir.getPath(), null, errorProject, null);
        this.loopProject = new GradleProject("src/test/resources/testGradleProject03");
        this.loopConfig = new RepairConfiguration(buildDir.getPath(), null, loopProject, null);
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

        boolean isSuccess02 = this.testExecutor.exec(errorConfig).canEndRepair();
        assertThat(isSuccess02).isFalse();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        //無限ループ等で実行時間が長くなった場合の処理
        boolean isSuccess03 = this.testExecutor.exec(loopConfig).canEndRepair();
        assertThat(isSuccess03).isFalse();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 実行するテストを指定して成功したかどうかテスト
     */
    @Test
    public void testForExecuteWithExecutionTest() {

        List<ExecutionTest> executionTests = new ArrayList<ExecutionTest>();
        executionTests.add(new ExecutionTest("source", Arrays.asList("testGradleProject01.App2Test", "testGradleProject01.AppTest")));
        executionTests.add(new ExecutionTest("source2", Arrays.asList("testGradleProject01.App2Test", "testGradleProject01.AppTest")));
        boolean isSuccess01 = this.testExecutor.exec(correctConfig, executionTests).canEndRepair();
        assertThat(isSuccess01).isTrue();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        List<ExecutionTest> executionTests2 = new ArrayList<ExecutionTest>();
        executionTests2.add(new ExecutionTest("source", Arrays.asList("testGradleProject01.App2Test")));
        executionTests2.add(new ExecutionTest("source2", Arrays.asList("testGradleProject01.App2Test")));
        boolean isSuccess02 = this.testExecutor.exec(correctConfig, executionTests2).canEndRepair();
        assertThat(isSuccess02).isTrue();
        try {
            FileUtils.deleteDirectory(this.buildDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
