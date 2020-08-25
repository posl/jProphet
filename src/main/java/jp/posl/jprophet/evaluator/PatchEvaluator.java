package jp.posl.jprophet.evaluator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.evaluator.extractor.FeatureExtractor;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.patch.DefaultPatchCandidate;
import jp.posl.jprophet.patch.PatchCandidate;

/**
 * 疑惑値などに応じてパッチを評価する
 * 今後機械学習機能を組み込んだ場合，それらの結果も考慮したソートなどを行う
 */
public class PatchEvaluator {
    static class PatchWithScore {
        final private PatchCandidate patch;
        final private double score;
        public PatchWithScore(PatchCandidate patch, double score) {
            this.patch = patch;
            this.score = score;
        }
        public PatchCandidate getPatch() {
            return this.patch;
        }
        public double getScore() {
            return this.score;
        }
    }

    public List<PatchCandidate> sort(List<PatchCandidate> candidates, List<Suspiciousness> suspiciousnessList, RepairConfiguration config) {
        if(config.getParameterPath().isPresent()) {
            final String parameterPath = config.getParameterPath().orElseThrow();
            try {
                final List<String> lines = Files.readAllLines(Paths.get(parameterPath), StandardCharsets.UTF_8);
                if (lines.size() != 1) {
                    System.exit(-1);
                }
                final List<Double> parameter = Arrays.asList(lines.get(0).split(",")).stream()
                    .map(String::trim)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
                final List<PatchCandidate> sortedPatchCandidates = candidates.stream()
                    .sorted(Comparator.comparingDouble(candidate ->
                        this.calculateScore(candidate, suspiciousnessList, parameter)))
                    .collect(Collectors.toList());
                return sortedPatchCandidates;
            } catch (IOException | IllegalFormatException e) {
                e.printStackTrace();
                System.exit(-1);
                return Collections.emptyList();
            }
        }
        else {
            final List<PatchCandidate> sortedPatchCandidates = candidates.stream()
                .sorted(Comparator.comparingDouble(candidate -> this.getSuspiciousnessValueFromPatch(candidate, suspiciousnessList)))
                .collect(Collectors.toList());
            Collections.reverse(sortedPatchCandidates);
            return sortedPatchCandidates;
        }
    }

    private double calculateScore(PatchCandidate patchCandidate, List<Suspiciousness> suspiciousnessList, List<Double> parameter) {
        final double suspiciousness = this.getSuspiciousnessValueFromPatch(patchCandidate, suspiciousnessList);
        final double beta = 0.02;
        final double A = Math.pow((1 - beta), suspiciousness);
        final FeatureExtractor extractor = new FeatureExtractor();
        final List<Double> vec = extractor.extract((DefaultPatchCandidate)patchCandidate).asDoubleList();
        if (vec.size() != parameter.size()) {
            throw new IllegalArgumentException();
        }
        double sum = 0;
        for (int i = 0; i < vec.size(); i++) {
            sum += vec.get(i) * parameter.get(i);
        }
        final double B = Math.exp(sum);
        
        return A * B;
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