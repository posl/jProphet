package jp.posl.jprophet.evaluator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.PatchCandidate;

public class PatchEvaluatorTest {
    /**
     * 学習済みパラメータを用いない場合
     */
    @Test public void testIfSortedBySuspiciousness(){
        final List<PatchCandidate> candidates = new ArrayList<PatchCandidate>();
        final PatchCandidate candidate1 = mock(PatchCandidate.class);
        when(candidate1.getLineNumber()).thenReturn(Optional.of(1));
        when(candidate1.getFqn()).thenReturn("a");
        final PatchCandidate candidate2 = mock(PatchCandidate.class);
        when(candidate2.getLineNumber()).thenReturn(Optional.of(2));
        when(candidate2.getFqn()).thenReturn("b");
        final PatchCandidate candidate3 = mock(PatchCandidate.class);
        when(candidate3.getLineNumber()).thenReturn(Optional.of(3));
        when(candidate3.getFqn()).thenReturn("c");

        candidates.add(candidate1);
        candidates.add(candidate2);
        candidates.add(candidate3);

        final List<Suspiciousness> suspiciousenesses = new ArrayList<Suspiciousness>();
        suspiciousenesses.add(new Suspiciousness("a", 1, 2));
        suspiciousenesses.add(new Suspiciousness("b", 2, 1));
        suspiciousenesses.add(new Suspiciousness("c", 3, 3));

        final PatchEvaluator evaluator = new PatchEvaluator();
        final RepairConfiguration config = new RepairConfiguration(null, null, null);
        final List<PatchCandidate> sortedCandidates = evaluator.sort(candidates, suspiciousenesses, config);

        assertThat(sortedCandidates.get(0)).isEqualTo(candidate3);
        assertThat(sortedCandidates.get(1)).isEqualTo(candidate1);
        assertThat(sortedCandidates.get(2)).isEqualTo(candidate2);
    }

    @Test public void test() {
        
    }
}

