package jp.posl.jprophet;


public class ProgramGenerator {

    public ProgramGenerator() {
        
    }
    
    public ProjectConfiguration applyPatch(ProjectConfiguration projectConfiguration, RepairCandidate repairCandidate) {
        return new ProjectConfiguration(projectConfiguration.getProjectPath() , projectConfiguration.getBuildPath());
    }

}
