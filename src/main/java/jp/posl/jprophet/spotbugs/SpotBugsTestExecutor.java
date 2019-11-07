package jp.posl.jprophet.spotbugs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.patch.DefaultPatchCandidate;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.TestExecutor;
import jp.posl.jprophet.test.UnitTestExecutor;

public class SpotBugsTestExecutor implements TestExecutor {


    private final String beforeResultFilePath;
    private final static String spotbugsResultFileName = "after";
    private List<SpotBugsFixedResult> fixedResults;

    public SpotBugsTestExecutor(String beforeResultFilePath) {
        this.beforeResultFilePath = beforeResultFilePath;
        this.fixedResults = new ArrayList<SpotBugsFixedResult>();
    }

    @Override
    public boolean exec(RepairConfiguration config) {
        final UnitTestExecutor unitTestExecutor = new UnitTestExecutor();
        final boolean isSuccess = unitTestExecutor.exec(config);
        if(isSuccess) {
            final SpotBugsExecutor spotBugsExecutor = new SpotBugsExecutor(spotbugsResultFileName);
            spotBugsExecutor.exec(config);
            final SpotBugsResultXMLReader spotBugsResultXMLReader = new SpotBugsResultXMLReader();
            final List<SpotBugsWarning> beforeWarnings = spotBugsResultXMLReader.readAllSpotBugsWarnings(beforeResultFilePath);
            final List<SpotBugsWarning> afterWarnings = spotBugsResultXMLReader.readAllSpotBugsWarnings(spotBugsExecutor.getResultFilePath());
            final boolean isFixedAll = compareSpotBugsWarnings(beforeWarnings, afterWarnings);
            return isFixedAll;
        }
        else {
            return false;
        }
    }


    private boolean compareSpotBugsWarnings(List<SpotBugsWarning> before, List<SpotBugsWarning> after) {
        final Set<SpotBugsWarning> beforeSet = new HashSet<SpotBugsWarning>(before);
        final Set<SpotBugsWarning> afterSet = new HashSet<SpotBugsWarning>(after);

        final Set<SpotBugsWarning> fixedSet = new HashSet<SpotBugsWarning>(beforeSet);
        fixedSet.removeAll(afterSet);

        final Set<SpotBugsWarning> unFixedSet = new HashSet<SpotBugsWarning>(beforeSet);
        unFixedSet.retainAll(afterSet);

        final Set<SpotBugsWarning> occurredSet = new HashSet<SpotBugsWarning>(afterSet);
        occurredSet.removeAll(beforeSet);

        final List<SpotBugsWarning> occurredWarnings = new ArrayList<SpotBugsWarning>(occurredSet);

        for (SpotBugsWarning warning : fixedSet) {
            fixedResults.add(new SpotBugsFixedResult(warning, occurredWarnings));
        }

        final boolean isFixedAll = (unFixedSet.size() == 0);
        return isFixedAll;
    }
    
}