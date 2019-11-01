package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import jp.posl.jprophet.Project;
import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.Suspiciousness;


public class SpotBugsBasedFaultLocalizationTest {

    /**
     * SpotBugsの結果によって疑惑値を決定できるかテスト
     */
    @Test
    public void testForExec() {
        final String outDir = "./SBtmp";             // テストプロジェクトのクラスパス
        final RepairConfiguration config = new RepairConfiguration(outDir, null, new Project("src/test/resources/testSBProject01"));
        final SpotBugsBasedFaultLocalization fl = new SpotBugsBasedFaultLocalization(config);
        final List<Suspiciousness> suspiciousnessList = fl.exec();

        for(int i = 13; i <= 20; i++) {
            assertThat(getSuspiciousness(suspiciousnessList, "testSBProject01.App2", i).getValue()).isEqualTo(1);
        }
        assertThat(getSuspiciousness(suspiciousnessList, "testSBProject01.hoge.App3", 8).getValue()).isEqualTo(1);
        
        assertThat(getSuspiciousness(suspiciousnessList, "testSBProject01.App", 8).getValue()).isEqualTo(0);
        assertThat(getSuspiciousness(suspiciousnessList, "testSBProject01.App2", 24).getValue()).isEqualTo(0);

        try {
			FileUtils.deleteDirectory(new File(outDir));
		} catch (IOException e) {
            System.err.println(e.getMessage());
			e.printStackTrace();
        }
        SpotBugsExecutor.deleteResultDirectory();


    }

    //ManualSpecificationTestのメソッドをそのままコピー
    private Suspiciousness getSuspiciousness(List<Suspiciousness> suspiciousnessList, String fqn, int line){
        List<Suspiciousness> suspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getFQN()) && s.getLine() == line)
            .collect(Collectors.toList());
        return suspiciousness.get(0);
    }
}