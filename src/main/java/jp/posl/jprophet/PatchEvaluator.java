package jp.posl.jprophet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.patch.PatchCandidate;

/**
 * 疑惑値などに応じてパッチを評価する
 * 今後機械学習機能を組み込んだ場合，それらの結果も考慮したソートなどを行う
 */
public class PatchEvaluator {
    /**
     * 修正パッチ候補リストを各修正パッチ候補が
     * 変更した行の疑惑値に応じて降順ソートする
     * TODO: ASTノードがカバーする範囲の先頭の行番号に応じた処理を行っているので
     * TODO: 今後patchCandidate.getLineNumber()が範囲を返すようになったら
     * TODO: それに応じた処理にしなくてはならない 
     * 
     * @param patchCandidates ソートしたいパッチ候補リスト
     * @param suspiciousnessList ソートに利用する疑惑値のリスト
     */
    public void descendingSortBySuspiciousness(List<PatchCandidate> patchCandidates, List<Suspiciousness> suspiciousnessList){
        PatchEvaluator that = this;
        Collections.sort(patchCandidates, new Comparator<PatchCandidate>() {
            @Override
            public int compare(PatchCandidate a, PatchCandidate b) {
                final double aSuspiciousnessValue;
                try {
                    final int aLineNumber = a.getLineNumber().orElseThrow();
                    final String aFqn = a.getFqn();
                    final Suspiciousness aSuspiciousness = that.findSuspiciousness(suspiciousnessList, aLineNumber, aFqn).orElseThrow();
                    aSuspiciousnessValue = aSuspiciousness.getValue();
                } catch (NoSuchElementException e) {
                    return 0;
                }
                final double bSuspiciousnessValue;
                try {
                    final int bLineNumber = b.getLineNumber().orElseThrow();
                    final String bFqn = b.getFqn();
                    final Suspiciousness bSuspiciousness = that.findSuspiciousness(suspiciousnessList, bLineNumber, bFqn).orElseThrow();
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

    /**
     * Suspiciousnessリストから行番号とファイルパスを元に疑惑値を取得
     * @param targetSuspiciousnesses 検索対象
     * @param lineNumber 行番号
     * @param path ファイルパス
     * @return 疑惑値
     */
    private Optional<Suspiciousness> findSuspiciousness(List<Suspiciousness> targetSuspiciousnesses, int lineNumber, String path){
        for(Suspiciousness suspiciousness : targetSuspiciousnesses){
            if(suspiciousness.getLineNumber() == lineNumber && suspiciousness.getFQN() == path){
                return Optional.of(suspiciousness);
            }
        }
        return Optional.empty();
    }
}