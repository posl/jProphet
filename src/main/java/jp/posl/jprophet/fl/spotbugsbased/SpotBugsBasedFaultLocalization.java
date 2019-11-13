package jp.posl.jprophet.fl.spotbugsbased;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.manualspecification.ManualSpecification;
import jp.posl.jprophet.fl.manualspecification.strategy.*;
import jp.posl.jprophet.spotbugs.SpotBugsExecutor;
import jp.posl.jprophet.spotbugs.SpotBugsResultXMLReader;
import jp.posl.jprophet.spotbugs.SpotBugsWarning;


/**
 * SpotBugsの結果によって疑惑値を決定する
 */
public class SpotBugsBasedFaultLocalization implements FaultLocalization {


    private final RepairConfiguration config;
    private final static String spotbugsResultFileName = "before";
    private final static int suspiciousnessValue = 1;

    

    /**
     * SpotBugsの結果によって疑惑値を決定するクラスの作成
     * @param config 対象プロジェクトのconfig
     */
    public SpotBugsBasedFaultLocalization(RepairConfiguration config) {
        this.config = config;
    }



    /**
     * SpotBugsの結果による疑惑値を返す
     * @return 疑惑値のリスト
     */
    @Override
    public List<Suspiciousness> exec() {

        final SpotBugsExecutor executor = new SpotBugsExecutor(spotbugsResultFileName);
        final SpotBugsResultXMLReader reader = new SpotBugsResultXMLReader();
        final List<SpecificationStrategy> strategies = new ArrayList<SpecificationStrategy>();

        executor.exec(config);
        final List<SpotBugsWarning> warnings =  reader.readAllSpotBugsWarnings(executor.getResultFilePath(), config.getTargetProject());
        for (SpotBugsWarning warning : warnings) {
            int start = warning.getStartLine();
            int end = warning.getEndLine();
            String fqn = warning.getFilePath().replace("/", ".").replace(".java", "");
            if(start == end) {
                strategies.add(new SpecificOneLineBug(fqn, start, suspiciousnessValue));
            }
            else {
                strategies.add(new SpecificBugsByRange(fqn, start, end, suspiciousnessValue));
            }
        }
        ManualSpecification manualSpecification = new ManualSpecification(config, strategies);
        return manualSpecification.exec();

    }
    
}