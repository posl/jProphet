package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StagedCondGenerator {
    public StagedCondGenerator() {
        
    }
    public List<PatchCandidate> applyConditionTemplate(PatchCandidate patchCandidate){
        return new ArrayList<>(Arrays.asList(patchCandidate));
    }
    
}
