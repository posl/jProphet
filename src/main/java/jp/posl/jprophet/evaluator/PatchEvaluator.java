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
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.exporter.CSVExporter;

/**
 * 
 * 疑惑値などに応じてパッチを評価する
 * 今後機械学習機能を組み込んだ場合，それらの結果も考慮したソートなどを行う
 */
public class PatchEvaluator {
    /** 
     * パッチ候補，FLによる疑惑値，疑惑値による順位，スコアの四つを合わせたクラス
     * 疑惑値と学習パラメータによって算出されるスコアによるパッチ候補のソートに用いる
    */
    public static class PatchForEval {
        final public PatchCandidate patch;
        final private double suspiciousness;
        private Integer rank;
        private Double score;
        public PatchForEval(PatchCandidate patch, double suspiciousness, int rank, double score) {
            this.patch = patch;
            this.suspiciousness = suspiciousness;
            this.rank = rank;
            this.score = score;
        }
        public PatchForEval(PatchCandidate patch, double suspiciousness) {
            this.patch = patch;
            this.suspiciousness = suspiciousness;
            this.rank = null;
            this.score = null;
        }
        public PatchCandidate getPatch() {
            return this.patch;
        }
        public double getSuspiciousness() {
            return this.suspiciousness;
        }
        public Optional<Integer> getRank() {
            if (this.rank == null) {
                return Optional.empty();
            }
            return Optional.of(this.rank);
        }
        public Optional<Double> getScore() {
            if (this.score == null) {
                return Optional.empty();
            }
            return Optional.of(this.score);
        }
        public void setRank(int rank) {
            if (rank < 0) {
                throw new IllegalArgumentException();
            }
            this.rank = rank;
        }
        public void setScore(double score) {
            this.score = score;
        }
    }

    /**
     * パッチ候補をProphetの学習モデルとフォルトローカリゼーション（FL）により算出された疑惑値を用いてランク付けしソートする
     * @param candidates パッチ候補のリスト
     * @param suspiciousnessList FLの疑惑値のリスト
     * @param config 修正の設定
     * @return ソート済みのパッチ候補リスト
     */
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
                final List<PatchForEval> candidatesForEvalSortedBySuspiciousness = candidates.stream()
                    .map(candidate -> new PatchForEval(candidate, this.getSuspiciousnessValueFromPatch((PatchCandidate)candidate, suspiciousnessList)))
                    .sorted(Comparator.comparingDouble(candidateWithFLRank -> ((PatchForEval)candidateWithFLRank).getSuspiciousness()).reversed())
                    .collect(Collectors.toList());
                for (int i = 0, rank = 1; i < candidatesForEvalSortedBySuspiciousness.size(); i++) {
                    final PatchForEval candidate = candidatesForEvalSortedBySuspiciousness.get(i);
                    candidate.setRank(rank);
                    candidate.setScore(this.calculateScore(candidate.getPatch(), rank, parameter));
                    if (i != candidatesForEvalSortedBySuspiciousness.size() - 1) {
                        if (candidate.getSuspiciousness() > candidatesForEvalSortedBySuspiciousness.get(i + 1).getSuspiciousness()) {
                            rank++;
                        }
                    }
                }
                final List<PatchCandidate> candidatesSortedByScore = candidatesForEvalSortedBySuspiciousness.stream()
                    .sorted(Comparator.comparingDouble(candidateWithFLRank -> ((PatchForEval)candidateWithFLRank).getScore().orElseThrow()).reversed())
                    .filter(candidate -> candidate.getScore().get() > 0)
                    .map(candidateWithFLRank -> candidateWithFLRank.getPatch())
                    .collect(Collectors.toList());

                CSVExporter ce = new CSVExporter("result/", "patchScore.csv");
                ce.exportAllPatchScore(candidatesForEvalSortedBySuspiciousness.stream()
                    .sorted(Comparator.comparingDouble(candidateWithFLRank -> ((PatchForEval)candidateWithFLRank).getScore().orElseThrow()).reversed())
                    .filter(candidate -> candidate.getScore().get() > 0)
                    .collect(Collectors.toList()));
                ce.export();

                return candidatesSortedByScore;
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

    /**
     * Prophetの学習アルゴリズムに基づいてパッチのスコアを計算する
     * @param patchCandidate パッチ候補
     * @param suspiciousness FL疑惑値
     * @param parameter 学習済みパラメータ
     * @return スコア
     */
    private double calculateScore(PatchCandidate patchCandidate, int suspiciousnessRank, List<Double> parameter) {
        try {
            final double beta = 0.02;
            final double A = Math.pow((1 - beta), suspiciousnessRank);
            final FeatureExtractor extractor = new FeatureExtractor();
            final List<Double> vec = extractor.extract((PatchCandidate)patchCandidate).asDoubleList();
            if (vec.size() != parameter.size()) {
                throw new IllegalArgumentException();
            }
            double sum = 0;
            for (int i = 0; i < vec.size(); i++) {
                sum += vec.get(i) * parameter.get(i);
            }
            final double B = Math.exp(sum);
            return A * B;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * パッチ候補の修正対象のステートメントの疑惑値を取得 
     * @param  patch 修正パッチ候補
     * @return 疑惑値 
     * @throws NoSuchElementException NodeのRangeが取れなかった場合と
     *         疑惑値リストに対応する疑惑値が存在しない場合に発生
     */
    private double getSuspiciousnessValueFromPatch(PatchCandidate patch, List<Suspiciousness> suspiciousnessList) throws NoSuchElementException {
        final Optional<Integer> lineNumber = patch.getLineNumber();
        if(!lineNumber.isPresent()) {
            throw new NoSuchElementException("Line number not found in " + patch.getFqn());
        }
        final String fqn = patch.getFqn();
        final Optional<Suspiciousness> suspiciousness = this.findSuspiciousness(suspiciousnessList, lineNumber.orElseThrow(), fqn);
        if (!suspiciousness.isPresent()) {
            throw new NoSuchElementException("Target suspiciousness not found. \n fqn:" + fqn + ", line number: " + lineNumber.orElseThrow());
        }
        return suspiciousness.orElseThrow().getValue();
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