package jp.posl.jprophet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import jp.posl.jprophet.FL.SuspiciousnessList;

public class PatchEvaluator {
    public List<PatchCandidate> sortPatchCandidates(List<PatchCandidate> patchCandidates, SuspiciousnessList suspiciousenessList){
        Collections.sort(patchCandidates, new Comparator<PatchCandidate>() {
            @Override
            public int compare(PatchCandidate a, PatchCandidate b) {
                double aSuspiciousnessValue;
                try {
                    aSuspiciousnessValue = suspiciousenessList.get(a.getLineNumber().orElseThrow(), a.getFQN()).orElseThrow().getValue();
                } catch (NoSuchElementException e) {
                    return 0;
                }
                double bSuspiciousnessValue;
                try {
                    bSuspiciousnessValue = suspiciousenessList.get(a.getLineNumber().orElseThrow(), a.getFQN()).orElseThrow().getValue();
                } catch (NoSuchElementException e) {
                    return 0;
                }

                double diff = aSuspiciousnessValue - bSuspiciousnessValue;
                if(diff > 0){
                    return 1;
                }
                else {
                    return 0;
                }
            }
        });
        return patchCandidates;
    }
}