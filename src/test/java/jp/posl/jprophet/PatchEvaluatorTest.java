package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jp.posl.jprophet.FL.Suspiciousness;

public class PatchEvaluatorTest {
    @Test public void test(){
        List<PatchCandidate> candidates = new ArrayList<PatchCandidate>();
        List<Suspiciousness> suspiciousenesses = new ArrayList<Suspiciousness>();
        PatchEvaluator evaluator = new PatchEvaluator();
        List<PatchCandidate> sortedCandidates = evaluator.sortPatchCandidates(candidates, suspiciousenesses);
    }
}

