package jp.posl.jprophet.spotbugs; //後で変更

import java.util.List;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.FL.FaultLocalization;
import jp.posl.jprophet.FL.Suspiciousness;

public class SpotBugsBasedFaultLocalization implements FaultLocalization {



    private final RepairConfiguration config;
    private final String resultPath = "./SB_result";


    public SpotBugsBasedFaultLocalization(RepairConfiguration config) {
        this.config = config;
            
    }

    @Override
    public List<Suspiciousness> exec() {

        final SpotBugsExecutor executor = new SpotBugsExecutor(resultPath);
        final SpotBugsResultXMLReader reader = new SpotBugsResultXMLReader();

        executor.exec(config);
        final List<BugInstance> warnings =  reader.readAllBugInstances(executor.getResultFilePath());

        for (BugInstance warning : warnings) {
            System.out.println(warning.getType());
            System.out.println(warning.getFilePath());
            System.out.println(warning.getPositionStart());
            System.out.println(warning.getPositionEnd());
            System.out.println("//////////////////////");
        }


        return null;

    }
    
}