package jp.posl.jprophet.test.executor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.spotbugs.SpotBugsExecutor;
import jp.posl.jprophet.test.result.TestResult;



public class SpotBugsTestExecutorTest {

    private RepairConfiguration beforeConfig;
    private RepairConfiguration afterConfig01; //一部解消＆新たなワーニング発生バージョン テストは通らない
    private RepairConfiguration afterConfig02; //全てのワーニングが解消されたバージョン テストは通る


    /**
     * テスト入力用のプロジェクトの用意
     */
    @Before
    public void setUpProject() {
        final File buildDir = new File("./tmp/");
        this.beforeConfig = new RepairConfiguration(buildDir.getPath(), null, new GradleProject("src/test/resources/testSBProject01"));
        this.afterConfig01 = new RepairConfiguration(buildDir.getPath(), null, new GradleProject("src/test/resources/testSBProject01_fixedOne"));
        this.afterConfig02 = new RepairConfiguration(buildDir.getPath(), null, new GradleProject("src/test/resources/testSBProject01_fixedAll"));
    }


    /**
     * テスト実行が成功したかどうかテスト
     */
    @Test
    public void testForExec() {
        final String resultFileName = "test";
        final SpotBugsExecutor spotBugsExecutor = new SpotBugsExecutor();
        spotBugsExecutor.exec(beforeConfig, resultFileName);

        final SpotBugsTestExecutor testExecutor = new SpotBugsTestExecutor(SpotBugsExecutor.getResultFilePath(resultFileName));
        final List<TestResult> results01 = testExecutor.exec(afterConfig01);
        final Map<String, String> resultMap01 = results01.get(0).toStringMap();
        assertThat(results01.size()).isEqualTo(1);
        assertThat(resultMap01.get("isSuccess")).isEqualTo("false");
        assertThat(resultMap01.get("type")).isEqualTo("NM_METHOD_NAMING_CONVENTION");
        assertThat(resultMap01.get("numOfOccurredWarning")).isEqualTo("1");
        
        final List<TestResult> results02 = testExecutor.exec(afterConfig02);
        //final Map<String, String> resultMap02 = results02.get(0).toStringMap();
        assertThat(results02.size()).isEqualTo(6);

    }

}