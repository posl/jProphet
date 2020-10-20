package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.RepairConfiguration;

public class SpotBugsExecutorTest {


    /**
     * 結果を出力できているかテスト
     */
    @Test
    public void testForExec() {
        final String resultFileName = "result";
        SpotBugsExecutor executor = new SpotBugsExecutor();
        final RepairConfiguration config = new RepairConfiguration("./tmp/SBout", null, new GradleProject("src/test/resources/testSBProject01"), null);
        executor.exec(config, resultFileName);
        assertThat(new File(SpotBugsExecutor.getResultFilePath(resultFileName)).exists()).isTrue();
        try {
            FileUtils.deleteDirectory(new File("./tmp"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

}