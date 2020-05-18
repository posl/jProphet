package jp.posl.jprophet.fl.spectrumbased.statement;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;
import jp.posl.jprophet.fl.spectrumbased.strategy.Coefficient;
import jp.posl.jprophet.fl.spectrumbased.coverage.Coverage;
import jp.posl.jprophet.fl.spectrumbased.coverage.TestResults;
import jp.posl.jprophet.fl.spectrumbased.coverage.Coverage.Status;
import jp.posl.jprophet.fl.Suspiciousness;

/**
 * ステートメント(行)ごとの疑惑値の計算
 */
public class SuspiciousnessCollector {

    private List<Suspiciousness> suspiciousnesses;
    final private List<String> sourceFqns;
    final private TestResults testResults;
    final private Coefficient coefficient;

    /**
     * テスト結果(カバレッジ情報を含む)から,全てのテスト対象ファイルの各行ごとの疑惑値を計算
     * @param testResults テスト結果
     */
    public SuspiciousnessCollector(TestResults testResults, List<String> sourceFqns, Coefficient coefficient){
        this.sourceFqns = sourceFqns;
        this.suspiciousnesses = new ArrayList<Suspiciousness>();
        this.testResults = testResults;
        this.coefficient = coefficient;
    }

    /**
     * テスト対象のソースコードの各行に対して疑惑値の計算を行う
     * 計算式はSuspiciousnessStrategyの中
     */
    public void exec(){

        for (String sourceFqn : sourceFqns){
            List<Coverage> failedCoverages = new ArrayList<Coverage>();
            testResults.getFailedTestResults().stream()
                .map(t -> t.getCoverages()
                    .stream()
                    .filter(c -> c.getName().equals(sourceFqn))
                    .collect(Collectors.toList()))
                .forEach(c -> failedCoverages.addAll(c));
            
            List<Coverage> successedCoverages = new ArrayList<Coverage>();
            testResults.getSuccessedTestResults().stream()
                .map(t -> t.getCoverages()
                    .stream()
                    .filter(c -> c.getName().equals(sourceFqn))
                    .collect(Collectors.toList()))
                .forEach(c -> successedCoverages.addAll(c));

            calculateSuspiciousnessOfSource(sourceFqn, failedCoverages, successedCoverages);
        }
    }

    /**
     * 
     * @return 全対象ファイルの疑惑値のリスト
     */
    public List<Suspiciousness> getSuspiciousnesses(){
        return this.suspiciousnesses;
    }

    /**
     * sourceFqnが示すファイルの各行の疑惑値を算出する
     * @param sourceFqn
     * @param failedCoverages
     * @param successedCoverages
     */
    private void calculateSuspiciousnessOfSource(String sourceFqn, List<Coverage> failedCoverages, List<Coverage> successedCoverages) {
        int lineLength = 0;
        if (!failedCoverages.isEmpty()){
            lineLength = failedCoverages.get(0).getLength();
        } else if (!successedCoverages.isEmpty()){
            lineLength = successedCoverages.get(0).getLength();
        }

        for (int i = 1; i <= lineLength; i++) {
            final int line = i;
            
            List<Status>failedCoverageStatuses = failedCoverages.stream()
                .map(c -> c.getStatus(line))
                .collect(Collectors.toList());
            
            List<Status>successedCoverageStatuses = successedCoverages.stream()
                .map(c -> c.getStatus(line))
                .collect(Collectors.toList());
            
            final int numberOfFailedTestsCoveringStatement = countStatus(failedCoverageStatuses, Status.COVERED);
            final int numberOfFailedTestsNotCoveringStatement = countStatus(failedCoverageStatuses, Status.NOT_COVERED);
            final int numberOfSuccessedTestsCoveringStatement = countStatus(successedCoverageStatuses, Status.COVERED);
            final int numberOfSuccessedTestsNotCoveringStatement = countStatus(successedCoverageStatuses, Status.NOT_COVERED);
            
            Suspiciousness suspiciousness = new Suspiciousness(sourceFqn, line, coefficient.calculate(numberOfFailedTestsCoveringStatement, numberOfFailedTestsNotCoveringStatement, numberOfSuccessedTestsCoveringStatement, numberOfSuccessedTestsNotCoveringStatement));
            this.suspiciousnesses.add(suspiciousness);
        }
    }

    private int countStatus(List<Status> list, Status status){
        return list.stream()
            .filter(l -> l.equals(status))
            .collect(Collectors.toList())
            .size();
    }

}