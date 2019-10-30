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
    private File outDir;                        // テストプロジェクトのクラスパス
    private File resultDir;                     // SpotBugsの実行結果を格納するパス


    @Before
    public void setUpProject() {
        this.outDir = new File("./SBtmp");
        this.resultDir = new File("./SBresult");
        this.config = new RepairConfiguration(outDir.getPath(), null, new Project("src/test/resources/testSBProject01"));
    }

    @Test
    public void testForExec() {
        
        SpotBugsExecutor executor = new SpotBugsExecutor(config, resultDir);
        executor.exec();

        assertThat(new File(executor.getResultFilePath()).exists()).isTrue();

        try {
			FileUtils.deleteDirectory(resultDir);
            FileUtils.deleteDirectory(outDir);
		} catch (IOException e) {
            System.err.println(e.getMessage());
			e.printStackTrace();
		}

    }


}