package jp.posl.jprophet.project;

import java.util.NoSuchElementException;

import jp.posl.jprophet.RepairConfiguration;

public class ProjectFactory {
    public Project create(RepairConfiguration config, String projectPath) {
        if(config.getTargetProject() instanceof GradleProject) {
            return new GradleProject(projectPath);
        }      
        throw new NoSuchElementException();
    }
}

