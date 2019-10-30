package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import jp.posl.jprophet.Project;
import jp.posl.jprophet.RepairConfiguration;

public class SpotBugsExecutorTest {


    /**
     * 結果を出力できているかテスト
     */
    @Test
    public void testForExec() {
        final String outDir = "./SBtmp";             // テストプロジェクトのクラスパス
        final String resultDir = "./SBresult";       // SpotBugsの実行結果を格納するパス
        SpotBugsExecutor executor = new SpotBugsExecutor(resultDir);
        final RepairConfiguration config = new RepairConfiguration(outDir, null, new Project("src/test/resources/testSBProject01"));
        executor.exec(config);
        assertThat(new File(executor.getResultFilePath()).exists()).isTrue();
        try {
			FileUtils.deleteDirectory(new File(resultDir));
            FileUtils.deleteDirectory(new File(outDir));
		} catch (IOException e) {
            System.err.println(e.getMessage());
			e.printStackTrace();
		}
    }

}