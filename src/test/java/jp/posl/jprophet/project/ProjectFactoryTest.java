package jp.posl.jprophet.project;

import org.junit.Test;

import jp.posl.jprophet.RepairConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectFactoryTest{
    /**
     * GradleプロジェクトのProjectオブジェクトが生成できるかテスト
     */
    @Test public void testForGradleProject(){
        final String buildDir = "./tmp/"; 
        final String resultDir = "./result/"; 
        final String projectPath = "src/test/resources/FizzBuzz01";
        final Project project = new GradleProject(projectPath);
        final RepairConfiguration config = new RepairConfiguration(buildDir, resultDir, project);
        final ProjectFactory projectFactory = new ProjectFactory();

        final Project newProject = projectFactory.create(config, projectPath);
        assertThat(newProject instanceof GradleProject).isTrue();
    }
}