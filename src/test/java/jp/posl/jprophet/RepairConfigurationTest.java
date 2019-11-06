package jp.posl.jprophet;

import org.junit.Test;

import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;

import static org.assertj.core.api.Assertions.assertThat;

public class RepairConfigurationTest {
    private final String projectPath = "src/test/resources/testGradleProject01";
    private final Project project = new GradleProject(this.projectPath);
    private final RepairConfiguration config = new RepairConfiguration("build", "result", project);
    @Test public void testForBuildPath(){
        assertThat(this.config.getBuildPath()).isEqualTo("build");
    }
    @Test public void testForFixedProjectPath(){
        assertThat(this.config.getFixedProjectDirPath()).isEqualTo("result");
    }
}