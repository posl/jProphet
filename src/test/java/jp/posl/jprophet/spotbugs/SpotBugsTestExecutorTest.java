package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.TestExecutor;


public class SpotBugsTestExecutorTest {

    private RepairConfiguration beforeConfig;
    private RepairConfiguration afterConfig;


    @Before
    public void setUpProject() {
        final File buildDir = new File("./tmp/");
        final Project beforeProject = new GradleProject("src/test/resources/testSBProject01");
        this.beforeConfig = new RepairConfiguration(buildDir.getPath(), null, beforeProject);
        final Project afterProject = new GradleProject("src/test/resources/testSBProject02");
        this.afterConfig = new RepairConfiguration(buildDir.getPath(), null, afterProject);
    }


    @Test
    public void testForExec() {
        final SpotBugsExecutor spotBugsExecutor = new SpotBugsExecutor("test");
        spotBugsExecutor.exec(beforeConfig);
        final TestExecutor testExecutor = new SpotBugsTestExecutor(spotBugsExecutor.getResultFilePath());
        testExecutor.exec(afterConfig);
    }

}