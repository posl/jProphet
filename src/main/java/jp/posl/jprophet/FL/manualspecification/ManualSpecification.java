package jp.posl.jprophet.fl.manualspecification;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.manualspecification.strategy.SpecificationStrategy;
import jp.posl.jprophet.Project;
import jp.posl.jprophet.fl.FaultLocalization;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/**
 * バグの位置を指定して,その行の疑惑値を変化させる
 */
public class ManualSpecification implements FaultLocalization{

    final private List<SpecificationStrategy> specificationStrategyList;
    final private Project project;

    /**
     * バグの位置を指定して,その行の疑惑値を変化させる
     * @param project
     * @param specificationStrategyList 疑惑値の変更リスト
     */
    public ManualSpecification(RepairConfiguration repairConfigulation, List<SpecificationStrategy> specificationStrategyList){
        this.project = repairConfigulation.getTargetProject();
        this.specificationStrategyList = specificationStrategyList;
    }

    /**
     * suspiciousnessListを書き換えて返す関数
     */
    public List<Suspiciousness> exec(){
        List<Suspiciousness> suspiciousnessList = new ArrayList<Suspiciousness>();
        initSuspiciousnessList(suspiciousnessList);

        for (SpecificationStrategy specificationStrategy : this.specificationStrategyList){
            specificationStrategy.calculate(suspiciousnessList);
        }

        return suspiciousnessList;
    }

    /**
     * 対象のソースコードの行数とFQNを取得し,suspiciousnessListを疑惑値0で初期化する
     * @param suspiciousnessList
     */
    private void initSuspiciousnessList(List<Suspiciousness> suspiciousnessList){
        Path srcDir;

        try {
            srcDir =  Paths.get(this.project.getProjectPath() + "/src/main");
            List<File> srcFilelist = Files.walk(srcDir).map(path -> path.toFile()).filter(file -> file.isFile()).collect(Collectors.toList());

            for (File sourceFile : srcFilelist){
                final String sourceFQN = sourceFile.getPath();
                final String fqn = sourceFQN.replace(this.project.getProjectPath() + "/src/main/java/", "").replace("/", ".").replace(".java", "");
                int numOfLines = calculateNumberOfLines(sourceFile);
                for (int i = 1; i <= numOfLines; i++){
                    suspiciousnessList.add(new Suspiciousness(fqn, i, 0));
                }
            }
            
        }catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * ファイルの総行数を取得する
     * @param sourceFile
     * @return ファイルの総行数
     */
    private int calculateNumberOfLines(File sourceFile){
        int numOfLines = 0;
        try{
            numOfLines = FileUtils.readLines(sourceFile, "utf-8").size();
        }catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return numOfLines;
    }


}