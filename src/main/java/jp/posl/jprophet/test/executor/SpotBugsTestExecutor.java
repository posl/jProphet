package jp.posl.jprophet.test.executor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.spotbugs.SpotBugsExecutor;
import jp.posl.jprophet.spotbugs.SpotBugsResultXMLReader;
import jp.posl.jprophet.spotbugs.SpotBugsWarning;
import jp.posl.jprophet.test.result.TestResult;
import jp.posl.jprophet.test.result.SpotBugsTestResult;
import jp.posl.jprophet.test.result.TestExecutorResult;

/**
 * SpotBugsによってテスト検証を行うクラス
 */
public class SpotBugsTestExecutor implements TestExecutor {


    private final String beforeResultFilePath;
    private final static String spotbugsResultFileName = "after";

    /**
     * SpotBugsTestExecutorのコンストラクタ
     * @param beforeResultFilePath 修正前のプロジェクトのSpotBugs適用結果ファイル
     */
    public SpotBugsTestExecutor(String beforeResultFilePath) {
        this.beforeResultFilePath = beforeResultFilePath;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TestExecutorResult exec(RepairConfiguration config) {
        /*
        final UnitTestExecutor unitTestExecutor = new UnitTestExecutor();
        System.out.println("testing...");
        final boolean isPassedUnitTest = unitTestExecutor.exec(config).canEndRepair();
        */
        final SpotBugsExecutor spotBugsExecutor = new SpotBugsExecutor();
        System.out.println("checking spotbugs...");
        spotBugsExecutor.exec(config, spotbugsResultFileName);
        final SpotBugsResultXMLReader spotBugsResultXMLReader = new SpotBugsResultXMLReader();
        final List<SpotBugsWarning> beforeWarnings = spotBugsResultXMLReader.readAllSpotBugsWarnings(beforeResultFilePath, config.getTargetProject());
        final List<SpotBugsWarning> afterWarnings = spotBugsResultXMLReader.readAllSpotBugsWarnings(SpotBugsExecutor.getResultFilePath(spotbugsResultFileName), config.getTargetProject());
        return createResults(beforeWarnings, afterWarnings, config);
    }


    /**
     * 修正前後の結果から差分を取得し結果を作成する
     * @param before 修正前のワーニングリスト
     * @param after 修正後のワーニングリスト
     * @return テスト結果のリスト
     */
    private TestExecutorResult createResults(List<SpotBugsWarning> before, List<SpotBugsWarning> after, RepairConfiguration config) {
        final Set<SpotBugsWarning> beforeSet = new HashSet<SpotBugsWarning>(before);
        final Set<SpotBugsWarning> afterSet = new HashSet<SpotBugsWarning>(after);

        final Set<SpotBugsWarning> fixedSet = new HashSet<SpotBugsWarning>(beforeSet);
        fixedSet.removeAll(afterSet);

        final Set<SpotBugsWarning> occurredSet = new HashSet<SpotBugsWarning>(afterSet);
        occurredSet.removeAll(beforeSet);

        final List<String> occurredWarnings = occurredSet.stream().map(warning -> warning.getType()).collect(Collectors.toList());

        List<TestResult> testResults = new ArrayList<TestResult>();
        System.out.println("fixed:" + fixedSet.size() + "  occured:" + occurredSet.size());
        if(fixedSet.size() > 0 && occurredWarnings.size() == 0) {
            final UnitTestExecutor unitTestExecutor = new UnitTestExecutor();
            System.out.println("testing...");
            final boolean passed = unitTestExecutor.exec(config).canEndRepair();
            testResults = fixedSet.stream().map(warning -> new SpotBugsTestResult(passed, warning, occurredWarnings.size())).collect(Collectors.toList());
        }
        else {
            testResults.add(new SpotBugsTestResult(false, new SpotBugsWarning("---", "", 0, 0), 0));
        }

        return new TestExecutorResult(false, testResults);
    }
}