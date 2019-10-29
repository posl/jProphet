package jp.posl.jprophet.FL;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.specification_strategy.SpecificationProcess;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class BugSpecification implements FaultLocalization{

    private List<Suspiciousness> suspiciousnessList = new ArrayList<Suspiciousness>();
    final private List<SpecificationProcess> specificationProcessList;
    final private ProjectConfiguration config;

    public BugSpecification(ProjectConfiguration config, List<SpecificationProcess> specificationProcessList){
        this.config = config;
        this.specificationProcessList = specificationProcessList;
        init();
    }

    /**
     * suspiciousnessListを返す関数
     */
    public List<Suspiciousness> exec(){
        for (SpecificationProcess specificationProcess : specificationProcessList){
            this.suspiciousnessList = specificationProcess.calculate(this.suspiciousnessList);
        }
        return this.suspiciousnessList;
    }

    /**
     * 対象のソースコードの行数とFQNを取得し,suspiciousnessListを疑惑値0で初期化する
     */
    private void init(){
        //対象ソースコードの行数,FQNを取得

        Path srcDir;

        try {
            srcDir =  Paths.get(config.getProjectPath() + "/src/main");
            List<File> srcFilelist = Files.walk(srcDir).map(path -> path.toFile()).filter(file -> file.isFile()).collect(Collectors.toList());

            for (File sourceFile : srcFilelist){
                final String sourceFQN = sourceFile.getPath();
                final String fqn = sourceFQN.replace(config.getProjectPath() + "/src/main/java/", "").replace("/", ".").replace(".java", "");
                int numOfLines = calculateNumberOfLines(sourceFile);
                for (int i = 1; i <= numOfLines; i++){
                    this.suspiciousnessList.add(new Suspiciousness(fqn, i, 0));
                }
            }
            
        }catch (NullPointerException | IOException e) {
            System.out.println("error");
        }
    }

    private int calculateNumberOfLines(File sourceFile){
        int lineNumber = 0;
        try {
            File file = sourceFile;
            if (file.exists()){
                FileReader fr = new FileReader(file);
                LineNumberReader lnr = new LineNumberReader(fr);

                while (lnr.readLine() != null){
                    lineNumber++;
                }

                lnr.close();
            }

        }catch (IOException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return lineNumber;

    }

    /**
     * 一箇所の疑惑値を変更する
     * @param fqn 変更したいファイルのfqn
     * @param line 変更したいファイルの行番号
     * @param value 変更後の疑惑値
     */
    public void specificBug(String fqn, int line, double value){
        List<Suspiciousness> suspiciousness = this.suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getPath()) && s.getLine() == line)
            .collect(Collectors.toList());
        
        if (suspiciousness.size() == 1){
            int index = suspiciousnessList.indexOf(suspiciousness.get(0));
            suspiciousnessList.set(index, new Suspiciousness(fqn, line, value));
        }
    }

    /**
     * ある範囲の疑惑値をまとめて変更する
     * @param fqn 変更したいファイルのfqn
     * @param startLine 変更したい範囲の始めの行番号
     * @param finishLine 変更したい範囲の終わりの行番号
     * @param value 変更後の疑惑値
     */
    public void specificBugsByRange(String fqn, int startLine,int finishLine, double value){
        List<Suspiciousness> startSuspiciousness = this.suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getPath()) && s.getLine() == startLine)
            .collect(Collectors.toList());
        List<Suspiciousness> finishSuspiciousness = this.suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getPath()) && s.getLine() == finishLine)
            .collect(Collectors.toList());
        
        if (startSuspiciousness.size() == 1 && finishSuspiciousness.size() == 1){
            int startIndex = suspiciousnessList.indexOf(startSuspiciousness.get(0));
            int finishIndex = suspiciousnessList.indexOf(finishSuspiciousness.get(0));

            for (int index = startIndex; index <= finishIndex; index++){
                suspiciousnessList.set(index, new Suspiciousness(fqn, startLine - startIndex + index, value));
            }
        }
    }

    /**
     * ある行を中心にして上下何行かの疑惑値を変更
     * @param fqn 変更したいファイルのfqn
     * @param line 変更したいファイルの中心の行
     * @param value 中心の行の変更後の疑惑値
     * @param range 中心の行から+-何行を変更するか
     * @param width 中心から1行ずれるごとにどれだけ疑惑値が下がるか
     */
    public void specificBugsWavy(String fqn, int line, double value, int range, double width){

        List<Suspiciousness> suspiciousness = this.suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getPath()) && s.getLine() == line)
            .collect(Collectors.toList());
        
        if (suspiciousness.size() == 1){
            int index = suspiciousnessList.indexOf(suspiciousness.get(0));
            suspiciousnessList.set(index, new Suspiciousness(fqn, line, value));
            for (int i = 1; i <= range; i++){
                if (index + i <= suspiciousnessList.size()){
                    if (suspiciousnessList.get(index + i).getPath().equals(fqn) && value - width * i >= 0){
                        suspiciousnessList.set(index + i, new Suspiciousness(fqn, line + i, value - width * i));
                    }
                }
                if (index - i >= 0){
                    if (suspiciousnessList.get(index - i).getPath().equals(fqn) && value - width * i >= 0){
                        suspiciousnessList.set(index - i, new Suspiciousness(fqn, line - i, value - width * i));
                    }
                }
            }
        }
    }
}