package jp.posl.jprophet.FL;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import jp.posl.jprophet.ProjectConfiguration;


public class BugSpecificationTest{
    // 入力として用意するテスト用のプロジェクト
    final private String projectPath = "src/test/resources/testFLProject";
    final private ProjectConfiguration config = new ProjectConfiguration(this.projectPath, "BStmp");

    /**
     * BugSpecification動作しているかどうかのテスト
     */
    @Test public void testForBugSpecification(){
        BugSpecification bugSpecification = new BugSpecification(config);
        bugSpecification.exec();
        bugSpecification.specificBug("testFLProject.Ifstatement", 7, 0.5);
        bugSpecification.specificBugsByRange("testFLProject.Ifstatement", 8, 10, 0.7);
        bugSpecification.specificBugsWavy("testFLProject.Ifstatement", 2, 1, 3, 0.1);
        bugSpecification.specificBugsWavy("testFLProject.Ifstatement", 16, 1, 4, 0.2);
    }


}
