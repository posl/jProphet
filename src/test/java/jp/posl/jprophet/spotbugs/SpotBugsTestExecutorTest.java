package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.TestExecutor;


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
        final TestExecutor testExecutor = new SpotBugsTestExecutor(spotBugsExecutor.getResultFilePath());
        final boolean isSuccess01 = testExecutor.exec(afterConfig01);
        assertThat(isSuccess01).isFalse();
        final boolean isSuccess02 = testExecutor.exec(afterConfig02);
        assertThat(isSuccess02).isTrue();

    }

}