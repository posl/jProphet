package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.RepairConfiguration;

public class SpotBugsExecutorTest {


    /**
     * 結果を出力できているかテスト
     */
    @Test
    public void testForExec() {
        SpotBugsExecutor executor = new SpotBugsExecutor("result");

        final RepairConfiguration config = new RepairConfiguration("./tmp/SBout", null, new GradleProject("src/test/resources/testSBProject01"));
        executor.exec(config);
        assertThat(new File(executor.getResultFilePath()).exists()).isTrue();
        try {
			FileUtils.deleteDirectory(new File("./tmp"));
		} catch (IOException e) {
            System.err.println(e.getMessage());
			e.printStackTrace();
		}
    }

}