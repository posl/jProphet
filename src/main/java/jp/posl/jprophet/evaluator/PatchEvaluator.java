package jp.posl.jprophet.evaluator;

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
            /**
             * @{inheritDoc}
             */
            @Override
            public int compare(PatchCandidate a, PatchCandidate b) {
                final double suspiciousnessValueOfA;
                final double suspiciousnessValueOfB;
                try {
                    suspiciousnessValueOfA = that.getSuspiciousnessValueFromPatch(a, suspiciousnessList); 
                    suspiciousnessValueOfB = that.getSuspiciousnessValueFromPatch(b, suspiciousnessList); 
                } catch (NoSuchElementException e) {
                    return 0;
                }

                final double diff = suspiciousnessValueOfB - suspiciousnessValueOfA;
                if(diff > 0){
                    return 1;
                } else if(diff < 0){
                    return -1;
                } else {
                    return 0;
                }
            }

        });
    }
    /**
     * パッチ候補の修正対象のステートメントの疑惑値を取得 
     * @param  patch 修正パッチ候補
     * @return 疑惑値 
     * @throws NoSuchElementException NodeのRangeが取れなかった場合と
     *         疑惑値リストに対応する疑惑値が存在しない場合に発生
     */
    private double getSuspiciousnessValueFromPatch(PatchCandidate patch, List<Suspiciousness> suspiciousnessList) throws NoSuchElementException{
        final int lineNumber = patch.getLineNumber().orElseThrow();
        final String fqn = patch.getFqn();
        final Suspiciousness suspiciousness = this.findSuspiciousness(suspiciousnessList, lineNumber, fqn).orElseThrow();
        return suspiciousness.getValue();
    }

    /**
     * Suspiciousnessリストから行番号とファイルパスを元に疑惑値を取得
     * @param targetSuspiciousnesses 検索対象
     * @param lineNumber 行番号
     * @param fqn ファイルのFQN
     * @return 疑惑値
     */
    private Optional<Suspiciousness> findSuspiciousness(List<Suspiciousness> targetSuspiciousnesses, int lineNumber, String fqn){
        for(Suspiciousness suspiciousness : targetSuspiciousnesses){
            if(suspiciousness.getLineNumber() == lineNumber && suspiciousness.getFQN().equals(fqn)){
                return Optional.of(suspiciousness);
            }
        }
        return Optional.empty();
    }
}