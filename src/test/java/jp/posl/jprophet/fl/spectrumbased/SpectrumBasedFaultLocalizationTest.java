package jp.posl.jprophet.fl.spectrumbased;

import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.spectrumbased.strategy.Coefficient;
import jp.posl.jprophet.fl.spectrumbased.strategy.Jaccard;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class SpectrumBasedFaultLocalizationTest{
    // 入力として用意するテスト用のプロジェクト
    final private String projectPath = "src/test/resources/testFLProject";
    final private RepairConfiguration config = new RepairConfiguration("FLtmp", null, new GradleProject(projectPath), null);
    private Coefficient coefficient = new Jaccard();

    /**
     * FaultLocalizationが動作しているかどうかのテスト
     */
    @Test public void testForSourceFilePaths(){
        SpectrumBasedFaultLocalization faultLocalization = new SpectrumBasedFaultLocalization(config, coefficient);
        List<Suspiciousness> suspiciousnessList = faultLocalization.exec();

        //疑惑値のリストの中にテスト対象のファイルのFQDNが存在するかチェック
        List<String> pathList = suspiciousnessList.stream()
            .map(s -> s.getFQN())
            .distinct()
            .collect(Collectors.toList());
        assertThat(pathList).contains("testFLProject.Ifstatement");
        assertThat(pathList).contains("testFLProject.App");
        assertThat(pathList).contains("testFLProject.Forstatement");

        //それぞれのテスト対象ファイルの行数が正しいかチェック
        List<Integer> iflineList = suspiciousnessList.stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getFQN()))
            .map(s -> s.getLineNumber())
            .collect(Collectors.toList());
        List<Integer> applineList = suspiciousnessList.stream()
            .filter(s -> "testFLProject.App".equals(s.getFQN()))
            .map(s -> s.getLineNumber())
            .collect(Collectors.toList());
        List<Integer> forlineList = suspiciousnessList.stream()
            .filter(s -> "testFLProject.Forstatement".equals(s.getFQN()))
            .map(s -> s.getLineNumber())
            .collect(Collectors.toList());

        assertThat(iflineList.size()).isEqualTo(14);
        assertThat(applineList.size()).isEqualTo(13);
        assertThat(forlineList.size()).isEqualTo(9);

        //Ifstatementの12行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline12 = suspiciousnessList.stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getFQN()) && s.getLineNumber() == 12)
            .collect(Collectors.toList());
        assertThat(ifline12.size()).isEqualTo(1);
        double sus12 = 1; //1/(1+0+0)
        assertThat(ifline12.get(0).getValue()).isEqualTo(sus12);

        //Ifstatementの9行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline9 = suspiciousnessList.stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getFQN()) && s.getLineNumber() == 9)
            .collect(Collectors.toList());
        assertThat(ifline9.size()).isEqualTo(1);
        double sus9 = (double)1/(double)3; // 1/(1+2+0)
        assertThat(ifline9.get(0).getValue()).isEqualTo(sus9);

        try {
            FileUtils.deleteDirectory(new File("./FLtmp/"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


}