package jp.posl.jprophet.FL;

import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.FL.strategy.Coefficient;
import jp.posl.jprophet.FL.strategy.Jaccard;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;

public class SuspiciousnessCollectorTest{
    // 入力として用意するテスト用のプロジェクト
    private String projectPath;
    private ProjectConfiguration project;
    private ProjectBuilder projectBuilder = new ProjectBuilder();
    private List<String> SourceClassFilePaths = new ArrayList<String>();
    private List<String> TestClassFilePaths = new ArrayList<String>();
    private TestResults testResults = new TestResults();
    private Coefficient coefficient = new Jaccard();



    @Before public void setup(){
        this.projectPath = "src/test/resources/testFLProject";
        this.project = new ProjectConfiguration(this.projectPath, "./SCtmp/");
        this.SourceClassFilePaths.add("testFLProject.Forstatement");
        this.SourceClassFilePaths.add("testFLProject.Ifstatement");
        this.SourceClassFilePaths.add("testFLProject.App");


        this.TestClassFilePaths.add("testFLProject.IfstatementTest3");
        this.TestClassFilePaths.add("testFLProject.AppTest");
        this.TestClassFilePaths.add("testFLProject.IfstatementTest2");
        this.TestClassFilePaths.add("testFLProject.IfstatementTest4");
        this.TestClassFilePaths.add("testFLProject.ForstatementTest");
        this.TestClassFilePaths.add("testFLProject.IfstatementTest");


        projectBuilder.build(project);
        
    
    }

    /**
     * SuspiciousnessCalculatorが動作しているかどうかのテスト
     */
    
     @Test public void testForSuspiciousnessCalculator(){

        CoverageCollector coverageCollector = new CoverageCollector("SCtmp");

        try{
            //testResults = coverageCollector.exec(sourceClass, testClass);
            testResults = coverageCollector.exec(SourceClassFilePaths, TestClassFilePaths);
        }catch (Exception e){
            System.out.println("例外");
        }

        SuspiciousnessCollector suspiciousnessCalculator = new SuspiciousnessCollector(testResults, coefficient);
        suspiciousnessCalculator.exec();

        //Ifstatementの3行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline3 = suspiciousnessCalculator.getSuspiciousnessList().stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getPath()) && s.getLine() == 3)
            .collect(Collectors.toList());
        assertThat(ifline3.size()).isEqualTo(1);
        double sus3 = 0.2; //1/(1+0+4)
        assertThat(ifline3.get(0).getValue()).isEqualTo(sus3);

        //Ifstatementの6行目の疑惑値 (Jaccard)
        List<Suspiciousness> ifline6 = suspiciousnessCalculator.getSuspiciousnessList().stream()
            .filter(s -> "testFLProject.Ifstatement".equals(s.getPath()) && s.getLine() == 6)
            .collect(Collectors.toList());
        assertThat(ifline6.size()).isEqualTo(1);
        double sus6 = 0; //0/(0+1+1)
        assertThat(ifline6.get(0).getValue()).isEqualTo(sus6);

        deleteDirectory(new File("./SCtmp/"));

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