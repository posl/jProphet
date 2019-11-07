package jp.posl.jprophet.spotbugs;

import java.util.List;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.test.TestExecutor;
import jp.posl.jprophet.test.UnitTestExecutor;

public class SpotBugsTestExecutor implements TestExecutor {


    private final String beforeResultFilePath;
    private final static String spotbugsResultFileName = "after";

    public SpotBugsTestExecutor(String beforeResultFilePath) {
        this.beforeResultFilePath = beforeResultFilePath;
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
            compareSpotBugsWarnings(beforeWarnings, afterWarnings);


            return false;
        }
        else {
            return false;
        }
    }


    private void compareSpotBugsWarnings(List<SpotBugsWarning> before, List<SpotBugsWarning> after) {
        System.out.println(before.size());
        System.out.println(after.size());
    }
    
}