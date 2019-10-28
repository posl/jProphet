package jp.posl.jprophet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.FL.SuspiciousnessList;

public class PatchEvaluatorTest {
    @Test public void testIfSortedBySuspiciousness(){
        List<PatchCandidate> candidates = new ArrayList<PatchCandidate>();
        PatchCandidateImpl candidate1 = mock(PatchCandidateImpl.class);
        when(candidate1.getLineNumber()).thenReturn(Optional.of(1));
        PatchCandidateImpl candidate2 = mock(PatchCandidateImpl.class);
        when(candidate2.getLineNumber()).thenReturn(Optional.of(2));
        PatchCandidateImpl candidate3 = mock(PatchCandidateImpl.class);
        when(candidate3.getLineNumber()).thenReturn(Optional.of(3));

        candidates.add(candidate1);
        candidates.add(candidate2);
        candidates.add(candidate3);

        SuspiciousnessList suspiciousenesses = new SuspiciousnessList();
        suspiciousenesses.add(new Suspiciousness("", 1, 2));
        suspiciousenesses.add(new Suspiciousness("", 2, 1));
        suspiciousenesses.add(new Suspiciousness("", 3, 3));

        PatchEvaluator evaluator = new PatchEvaluator();
        List<PatchCandidate> sortedCandidates = evaluator.sortPatchCandidates(candidates, suspiciousenesses);
        /*
        assertThat(sortedCandidates.get(0)).isEqualTo(candidate3);
        assertThat(sortedCandidates.get(1)).isEqualTo(candidate1);
        assertThat(sortedCandidates.get(2)).isEqualTo(candidate2);
        */
    }
}

