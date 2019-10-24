package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StagedCondGenerator {
    public StagedCondGenerator() {
        
    }
    public List<RepairCandidate> applyConditionTemplate(RepairCandidate repairCandidate){
        return new ArrayList<>(Arrays.asList(repairCandidate));
    }
    
}
