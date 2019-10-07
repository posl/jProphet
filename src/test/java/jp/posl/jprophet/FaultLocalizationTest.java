package jp.posl.jprophet;

import jp.posl.jprophet.ProjectConfiguration;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jp.posl.jprophet.FaultLocalization;
import jp.posl.jprophet.FL.Suspiciousness;
import java.io.File;

public class FaultLocalizationTest{
    // 入力として用意するテスト用のプロジェクト
    private String projectPath = "src/test/resources/testFLProject";
    private ProjectConfiguration project = new ProjectConfiguration(this.projectPath, "FLtmp");

    /**
     * FaultLocalizationが動作しているかどうかのテスト
     */
    @Test public void testForSourceFilePaths(){
        List<Suspiciousness> list = new ArrayList<Suspiciousness>();
        FaultLocalization faultLocalization = new FaultLocalization(project);
        list = faultLocalization.exec();

        //疑惑値のリストの中にテスト対象のファイルのFQDNが存在するかチェック
        List<String> pathList = list.stream()
            .map(s -> s.getPath())
            .distinct()
            .collect(Collectors.toList());
        assertThat(pathList).contains("testFLProject.Ifstatement");
        assertThat(pathList).contains("testFLProject.App");
        assertThat(pathList).contains("testFLProject.Forstatement");

        //それぞれのテスト対象ファイルの行数が正しいかチェック
        List<Integer> iflineList = list.stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getPath()))
            .map(s -> s.getLine())
            .collect(Collectors.toList());
        List<Integer> applineList = list.stream()
            .filter(s -> "testFLProject.App".equals(s.getPath()))
            .map(s -> s.getLine())
            .collect(Collectors.toList());
        List<Integer> forlineList = list.stream()
            .filter(s -> "testFLProject.Forstatement".equals(s.getPath()))
            .map(s -> s.getLine())
            .collect(Collectors.toList());

        assertThat(iflineList.size()).isEqualTo(14);
        assertThat(applineList.size()).isEqualTo(13);
        assertThat(forlineList.size()).isEqualTo(9);

        //Ifstatementの12行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline12 = list.stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getPath()) && s.getLine() == 12)
            .collect(Collectors.toList());
        assertThat(ifline12.size()).isEqualTo(1);
        double sus12 = 1; //1/(1+0+0)
        assertThat(ifline12.get(0).getValue()).isEqualTo(sus12);

        //Ifstatementの9行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline9 = list.stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getPath()) && s.getLine() == 9)
            .collect(Collectors.toList());
        assertThat(ifline9.size()).isEqualTo(1);
        double sus9 = (double)1/(double)3; // 1/(1+2+0)
        assertThat(ifline9.get(0).getValue()).isEqualTo(sus9);

        deleteDirectory(new File("./FLtmp/"));
    }

    /**
     * ディレクトリをディレクトリの中のファイルごと再帰的に削除する 
     * @param dir 削除対象ディレクトリ
     */
    private void deleteDirectory(File dir){
        if(dir.listFiles() != null){
            for(File file : dir.listFiles()){
                if(file.isFile())
                    file.delete();
                else
                    deleteDirectory(file);
            }
        }
        dir.delete();
    }

}