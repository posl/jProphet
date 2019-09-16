package jp.posl.jprophet;

import jp.posl.jprophet.ProjectConfiguration;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.posl.jprophet.FaultLocalization;

public class FaultLocalizationTest{
    // 入力として用意するテスト用のプロジェクト
    private String projectPath = "src/test/resources/testGradleProject01";
    private ProjectConfiguration project = new ProjectConfiguration(this.projectPath, "Ftmp");

    /**
     * ソースファイルを正しく取得できているかの検証
     */
    @Test public void testForSourceFilePaths(){
        FaultLocalization faultLocalization = new FaultLocalization(project);
        faultLocalization.exec();
    }

}