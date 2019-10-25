package jp.posl.jprophet;

import java.util.List;
import org.apache.commons.io.FileUtils;

public class ProgramGenerator {

    public ProgramGenerator() {
        
    }
    
    public ProjectConfiguration applyPatch(ProjectConfiguration projectConfiguration, RepairCandidate repairCandidate) {
        // List<String> fixedFilePaths = repairCandidate.getFixedFilePaths();
        // FileUtils.copyDirectory(project, "./output/");
        return new ProjectConfiguration(projectConfiguration.getProjectPath() , projectConfiguration.getBuildPath());
    }

}
