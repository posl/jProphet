package jp.posl.jprophet;

import java.util.List;

import jp.posl.jprophet.patch.PatchCandidate;

public class StagedCondGenerator {
    public StagedCondGenerator() {
        
    }
    public List<PatchCandidate> applyConditionTemplate(PatchCandidate patchCandidate){
        return List.of(patchCandidate);
    }
    
}
