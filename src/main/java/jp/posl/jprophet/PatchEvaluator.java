package jp.posl.jprophet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.posl.jprophet.FL.SuspiciousnessList;

public class PatchEvaluator {
    public List<PatchCandidate> sortPatchCandidates(List<PatchCandidate> patchCandidates, SuspiciousnessList suspiciousenesses){
        Collections.sort(patchCandidates, new Comparator<PatchCandidate>() {
            @Override
            public int compare(PatchCandidate a, PatchCandidate b) {
                return 0;        
            }
        });
        return patchCandidates;
    }

}