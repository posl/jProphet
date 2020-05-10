package jp.posl.jprophet.fl.spectrumbased.statement;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;
import jp.posl.jprophet.fl.spectrumbased.strategy.Coefficient;
import jp.posl.jprophet.fl.spectrumbased.coverage.Coverage;
import jp.posl.jprophet.fl.spectrumbased.coverage.TestResults;
import jp.posl.jprophet.fl.Suspiciousness;

/**
 * ステートメント(行)ごとの疑惑値の計算
 */
public class SuspiciousnessCollector {

    private List<Suspiciousness> suspiciousnesses;
    final private List<String> sourceFqns;
    final private int numberOfSuccessedTests;
    final private int numberOfFailedTests;
    final private TestResults testResults;
    final private Coefficient coefficient;

    /**
     * テスト結果(カバレッジ情報を含む)から,全てのテスト対象ファイルの各行ごとの疑惑値を計算
     * @param testResults テスト結果
     */
    public SuspiciousnessCollector(TestResults testResults, List<String> sourceFqns, Coefficient coefficient){
        this.sourceFqns = sourceFqns;
        this.suspiciousnesses = new ArrayList<Suspiciousness>();
        this.numberOfSuccessedTests = testResults.getSuccessedTestResults().size();
        this.numberOfFailedTests = testResults.getFailedTestResults().size();
        this.testResults = testResults;
        this.coefficient = coefficient;
    }

    /**
     * テスト対象のソースコードの各行に対して疑惑値の計算を行う
     * 計算式はSuspiciousnessStrategyの中
     */
    public void exec(){

        for (String sourceFqn : sourceFqns){
            System.out.println(sourceFqn);
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

    public List<Suspiciousness> getSuspiciousnesses(){
        return this.suspiciousnesses;
    }

    private void calculateSuspiciousnessOfSource(String sourceFqn, List<Coverage> failedCoverages, List<Coverage> successedCoverages) {
        int lineLength = 0;
        if (!failedCoverages.isEmpty()){
            lineLength = failedCoverages.get(0).getLength();
        } else if (!successedCoverages.isEmpty()){
            lineLength = successedCoverages.get(0).getLength();
        }

        for (int i = 1; i <= lineLength; i++) {
            final int line = i;
            Map<Integer, Integer> failedCoverageStatus = failedCoverages.stream()
                .map(c -> c.getStatusOfLine().get(line))
                .collect(
                    Collectors.groupingBy(
                            //MapのキーにはListの要素をそのままセットする
                    Function.identity(),
                            //Mapの値にはListの要素を1に置き換えて、それをカウントするようにする
                    Collectors.summingInt(s->1)) 
                );
            
            Map<Integer, Integer> successedCoverageStatus = successedCoverages.stream()
                .map(c -> c.getStatusOfLine().get(line))
                .collect(
                    Collectors.groupingBy(
                            //MapのキーにはListの要素をそのままセットする
                    Function.identity(),
                            //Mapの値にはListの要素を1に置き換えて、それをカウントするようにする
                    Collectors.summingInt(s->1)) 
                );

            int numberOfFailedTestsCoveringStatement = failedCoverageStatus.get(2) == null ? 0 : failedCoverageStatus.get(2);
            int numberOfFailedTestsNotCoveringStatement = failedCoverageStatus.get(1) == null ? 0 : failedCoverageStatus.get(1);
            int numberOfSuccessedTestsCoveringStatement = successedCoverageStatus.get(2) == null ? 0 : successedCoverageStatus.get(2);
            int numberOfSuccessedTestsNotCoveringStatement = successedCoverageStatus.get(1) == null ? 0 : successedCoverageStatus.get(1);
            Suspiciousness suspiciousness = new Suspiciousness(sourceFqn, line, coefficient.calculate(numberOfFailedTestsCoveringStatement, numberOfFailedTestsNotCoveringStatement, numberOfSuccessedTestsCoveringStatement, numberOfSuccessedTestsNotCoveringStatement));
            this.suspiciousnesses.add(suspiciousness);
        }


    }

}