package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.posl.jprophet.patch.PatchCandidate;

public class StagedCondGenerator {
    public StagedCondGenerator() {
        
    }
    public List<PatchCandidate> applyConditionTemplate(PatchCandidate patchCandidate){
        return new ArrayList<>(Arrays.asList(patchCandidate));
    }
    
}
