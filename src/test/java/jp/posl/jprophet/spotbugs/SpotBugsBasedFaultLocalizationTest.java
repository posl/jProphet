package jp.posl.jprophet.spotbugs;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import jp.posl.jprophet.Project;
import jp.posl.jprophet.RepairConfiguration;


public class SpotBugsBasedFaultLocalizationTest {


    @Test
    public void testForExec() {
        final String outDir = "./SBtmp";             // テストプロジェクトのクラスパス
        final RepairConfiguration config = new RepairConfiguration(outDir, null, new Project("src/test/resources/testSBProject01"));
        final SpotBugsBasedFaultLocalization fl = new SpotBugsBasedFaultLocalization(config);
        fl.exec();

    }

}