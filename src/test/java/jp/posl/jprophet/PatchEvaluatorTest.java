package jp.posl.jprophet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.patch.AstPatchCandidate;
import jp.posl.jprophet.patch.PatchCandidate;

public class PatchEvaluatorTest {
    /**
     * 疑惑値リストの通りにパッチ候補が降順ソートされているかテスト
     */
    @Test public void testIfSortedBySuspiciousness(){
        List<PatchCandidate> candidates = new ArrayList<PatchCandidate>();
        PatchCandidate candidate1 = mock(AstPatchCandidate.class);
        when(candidate1.getLineNumber()).thenReturn(Optional.of(1));
        when(candidate1.getFqn()).thenReturn("a");
        PatchCandidate candidate2 = mock(AstPatchCandidate.class);
        when(candidate2.getLineNumber()).thenReturn(Optional.of(2));
        when(candidate2.getFqn()).thenReturn("b");
        PatchCandidate candidate3 = mock(AstPatchCandidate.class);
        when(candidate3.getLineNumber()).thenReturn(Optional.of(3));
        when(candidate3.getFqn()).thenReturn("c");

        candidates.add(candidate1);
        candidates.add(candidate2);
        candidates.add(candidate3);

        List<Suspiciousness> suspiciousenesses = new ArrayList<Suspiciousness>();
        suspiciousenesses.add(new Suspiciousness("a", 1, 2));
        suspiciousenesses.add(new Suspiciousness("b", 2, 1));
        suspiciousenesses.add(new Suspiciousness("c", 3, 3));

        PatchEvaluator evaluator = new PatchEvaluator();
        evaluator.descendingSortBySuspiciousness(candidates, suspiciousenesses);

        assertThat(candidates.get(0)).isEqualTo(candidate3);
        assertThat(candidates.get(1)).isEqualTo(candidate1);
        assertThat(candidates.get(2)).isEqualTo(candidate2);
    }
}

