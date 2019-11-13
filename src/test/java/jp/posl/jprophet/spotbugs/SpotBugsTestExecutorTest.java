package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.project.GradleProject;



public class SpotBugsTestExecutorTest {

    private RepairConfiguration beforeConfig;
    private RepairConfiguration afterConfig01; //一部解消＆新たなワーニング発生バージョン テストは通らない
    private RepairConfiguration afterConfig02; //全てのワーニングが解消されたバージョン　テストは通る


    @Before
    public void setUpProject() {
        final File buildDir = new File("./tmp/");
        this.beforeConfig = new RepairConfiguration(buildDir.getPath(), null, new GradleProject("src/test/resources/testSBProject01"));
        this.afterConfig01 = new RepairConfiguration(buildDir.getPath(), null, new GradleProject("src/test/resources/testSBProject01_fixedOne"));
        this.afterConfig02 = new RepairConfiguration(buildDir.getPath(), null, new GradleProject("src/test/resources/testSBProject01_fixedAll"));
    }


    @Test
    public void testForExec() {
        final SpotBugsExecutor spotBugsExecutor = new SpotBugsExecutor("test");
        spotBugsExecutor.exec(beforeConfig);

        final SpotBugsTestExecutor testExecutor01 = new SpotBugsTestExecutor(spotBugsExecutor.getResultFilePath());
        final boolean isSuccess01 = testExecutor01.exec(afterConfig01).get(0).getIsSuccess();
        assertThat(isSuccess01).isFalse();
        /*
        final SpotBugsFixedResult result01 = testExecutor01.getFixedResults().get(0);
        assertThat(result01.getFixedWarning().getType()).isEqualTo("NM_METHOD_NAMING_CONVENTION");
        assertThat(result01.getFixedWarning().getFilePath()).isEqualTo("testSBProject01/App2.java");
        assertThat(result01.getOccurredNewWarnings().size()).isEqualTo(1);
        */

        final SpotBugsTestExecutor testExecutor02 = new SpotBugsTestExecutor(spotBugsExecutor.getResultFilePath());
        final boolean isSuccess02 = testExecutor02.exec(afterConfig02).get(0).getIsSuccess();
        assertThat(isSuccess02).isTrue();
        //assertThat(testExecutor02.getFixedResults().size()).isEqualTo(6);

    }

}