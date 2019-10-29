package jp.posl.jprophet.FL;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.specification_strategy.*;


public class BugSpecificationTest{
    // 入力として用意するテスト用のプロジェクト
    final private String projectPath = "src/test/resources/testFLProject";
    final private ProjectConfiguration config = new ProjectConfiguration(this.projectPath, "BStmp");
    private List<SpecificationProcess> specificationProcessList = new ArrayList<SpecificationProcess>();


    @Before public void setUp(){
        this.specificationProcessList.add(new SpecificBug("testFLProject.Ifstatement", 7, 0.5));
        this.specificationProcessList.add(new SpecificBugsByRange("testFLProject.Ifstatement", 8, 10, 0.7));
        this.specificationProcessList.add(new SpecificBugsWavy("testFLProject.Ifstatement", 2, 1, 3, 0.1));
        this.specificationProcessList.add(new SpecificBugsWavy("testFLProject.Ifstatement", 16, 1, 4, 0.2));
    }
    /**
     * BugSpecification動作しているかどうかのテスト
     */
    @Test public void testForBugSpecification(){
        BugSpecification bugSpecification = new BugSpecification(config, this.specificationProcessList);
        bugSpecification.exec();
    }


}
