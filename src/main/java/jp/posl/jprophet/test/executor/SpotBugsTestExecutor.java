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
import jp.posl.jprophet.test.result.SpotBugsTestResult;
import jp.posl.jprophet.test.result.TestResult;

public class SpotBugsTestExecutor implements TestExecutor {


    private final String beforeResultFilePath;
    private final static String spotbugsResultFileName = "after";
    //private List<SpotBugsFixedResult> fixedResults;

    public SpotBugsTestExecutor(String beforeResultFilePath) {
        this.beforeResultFilePath = beforeResultFilePath;
        //this.fixedResults = new ArrayList<SpotBugsFixedResult>();
    }

    @Override
    public List<TestResult> exec(RepairConfiguration config) {
        final UnitTestExecutor unitTestExecutor = new UnitTestExecutor();
        final boolean isSuccess = unitTestExecutor.exec(config).get(0).getIsSuccess();
        if(isSuccess) {
            final SpotBugsExecutor spotBugsExecutor = new SpotBugsExecutor(spotbugsResultFileName);
            spotBugsExecutor.exec(config);
            final SpotBugsResultXMLReader spotBugsResultXMLReader = new SpotBugsResultXMLReader();
            final List<SpotBugsWarning> beforeWarnings = spotBugsResultXMLReader.readAllSpotBugsWarnings(beforeResultFilePath);
            final List<SpotBugsWarning> afterWarnings = spotBugsResultXMLReader.readAllSpotBugsWarnings(spotBugsExecutor.getResultFilePath());
            return createResult(beforeWarnings, afterWarnings);
        }
        else {
            return new ArrayList<TestResult>();
        }
    }


    private List<TestResult> createResult(List<SpotBugsWarning> before, List<SpotBugsWarning> after) {
        final Set<SpotBugsWarning> beforeSet = new HashSet<SpotBugsWarning>(before);
        final Set<SpotBugsWarning> afterSet = new HashSet<SpotBugsWarning>(after);

        final Set<SpotBugsWarning> fixedSet = new HashSet<SpotBugsWarning>(beforeSet);
        fixedSet.removeAll(afterSet);

        final Set<SpotBugsWarning> unFixedSet = new HashSet<SpotBugsWarning>(beforeSet);
        unFixedSet.retainAll(afterSet);

        final Set<SpotBugsWarning> occurredSet = new HashSet<SpotBugsWarning>(afterSet);
        occurredSet.removeAll(beforeSet);

        final boolean isFixedAll = (unFixedSet.size() == 0);
        final int numOfOccurredWarnings = occurredSet.size();

        return fixedSet.stream().map(warning -> new SpotBugsTestResult(isFixedAll, warning, numOfOccurredWarnings)).collect(Collectors.toList());
    }
    
}