package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.Project;
import jp.posl.jprophet.RepairConfiguration;

public class SpotBugsExecutorTest {

    private RepairConfiguration config;
    private final String outDir = "./SBtmp";             // テストプロジェクトのクラスパス
    private final String resultDir = "./SBresult";       // SpotBugsの実行結果を格納するパス


    /**
     * プロジェクトの準備
     */
    @Before
    public void setUpProject() {
        this.config = new RepairConfiguration(outDir, null, new Project("src/test/resources/testSBProject01"));
    }


    /**
     * 結果を出力できているかテスト
     */
    @Test
    public void testForExec() {
        SpotBugsExecutor executor = new SpotBugsExecutor(resultDir);
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