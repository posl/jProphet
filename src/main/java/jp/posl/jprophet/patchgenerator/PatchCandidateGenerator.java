package jp.posl.jprophet.patchgenerator;

import java.util.List;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.project.Project;

public interface PatchCandidateGenerator {
    public List<PatchCandidate> exec(Project project); 
}