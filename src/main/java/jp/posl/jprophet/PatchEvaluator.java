package jp.posl.jprophet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.FL.SuspiciousnessList;

/**
 * 疑惑値などに応じてパッチを評価する
 * 今後機械学習機能を組み込んだ場合，それらの結果も考慮したソートなどを行う
 */
public class PatchEvaluator {
    /**
     * 修正パッチ候補リストを各修正パッチ候補が
     * 変更した行の疑惑値に応じて降順ソートする
     * 
     * @param patchCandidates ソートしたいパッチ候補リスト
     * @param suspiciousenessList ソートに利用する疑惑値のリスト
     */
    public void descendingSortBySuspiciousness(List<PatchCandidate> patchCandidates, SuspiciousnessList suspiciousenessList){
        Collections.sort(patchCandidates, new Comparator<PatchCandidate>() {
            @Override
            public int compare(PatchCandidate a, PatchCandidate b) {
                final double aSuspiciousnessValue;
                try {
                    final int aLineNumber = a.getLineNumber().orElseThrow();
                    final String aFqn = a.getFqn();
                    final Suspiciousness aSuspiciousness = suspiciousenessList.get(aLineNumber, aFqn).orElseThrow();
                    aSuspiciousnessValue = aSuspiciousness.getValue();
                } catch (NoSuchElementException e) {
                    return 0;
                }
                final double bSuspiciousnessValue;
                try {
                    final int bLineNumber = b.getLineNumber().orElseThrow();
                    final String bFqn = b.getFqn();
                    final Suspiciousness bSuspiciousness = suspiciousenessList.get(bLineNumber, bFqn).orElseThrow();
                    bSuspiciousnessValue = bSuspiciousness.getValue();
                } catch (NoSuchElementException e) {
                    return 0;
                }

                final double diff = bSuspiciousnessValue - aSuspiciousnessValue;
                if(diff > 0){
                    return 1;
                }
                else {
                    return -1;
                }
            }
        });
    }
}