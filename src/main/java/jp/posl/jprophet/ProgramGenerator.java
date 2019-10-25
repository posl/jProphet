package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ProgramGenerator {

    public ProgramGenerator() {
        
    }
    
    public ProjectConfiguration applyPatch(ProjectConfiguration projectConfiguration, RepairCandidate repairCandidate) {
        // List<String> fixedFilePaths = repairCandidate.getFixedFilePaths();
        File originalProjectDir = new File(projectConfiguration.getProjectPath());
        File outputDir = new File(projectConfiguration.getBuildPath() + FilenameUtils.getBaseName(projectConfiguration.getProjectPath()));
        try {
            FileUtils.copyDirectory(originalProjectDir, outputDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return new ProjectConfiguration(projectConfiguration.getProjectPath() , projectConfiguration.getBuildPath());
    }

}
