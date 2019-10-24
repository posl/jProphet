package jp.posl.jprophet;

import static org.assertj.core.api.Assertions.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.test.TestExecutor;
import jp.posl.jprophet.util.Directory;

public class TestExecutorTest {

    private TestExecutor testExecutor;
    private ProjectConfiguration correctProject, errorProject;
    private File outDir;

    /**
     * テスト入力用のプロジェクトの用意
     */
    @Before
    public void setUpProject() {
        this.outDir = new File("./tmp/");
        this.correctProject = new ProjectConfiguration("src/test/resources/testGradleProject01", outDir.getPath());
        this.errorProject = new ProjectConfiguration("src/test/resources/testGradleProject02", outDir.getPath());
        this.testExecutor = new TestExecutor();
    }

    /**
     * テスト実行が成功したかどうかテスト
     */
    @Test
    public void testForExecute() {

        boolean isSuccess01 = this.testExecutor.run(correctProject);
        assertThat(isSuccess01).isTrue();
        Directory.delete(this.outDir);

        boolean isSuccess02 = this.testExecutor.run(errorProject);
        assertThat(isSuccess02).isFalse();
        Directory.delete(this.outDir);
    }
}
