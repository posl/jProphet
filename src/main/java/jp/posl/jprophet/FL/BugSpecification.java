package jp.posl.jprophet.FL;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.ProjectConfiguration;
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
    private ProjectConfiguration config;

    public BugSpecification(ProjectConfiguration config){
        this.config = config;
    }

    public List<Suspiciousness> exec(){
        //対象ソースコードの行数,FQNを取得

        Path srcDir;

        try {
            srcDir =  Paths.get(config.getProjectPath() + "/src/main");
            List<File> srcFilelist = Files.walk(srcDir).map(path -> path.toFile()).filter(file -> file.isFile()).collect(Collectors.toList());

            for (File sourceFile : srcFilelist){
                final String sourceName = sourceFile.getPath();
                final String fqn = sourceName.replace(config.getProjectPath() + "/src/main/java/", "").replace("/", ".").replace(".java", "");
                int numOfLines = calculateNumberOfLines(sourceFile);
                for (int i = 1; i <= numOfLines; i++){
                    this.suspiciousnessList.add(new Suspiciousness(fqn, i, 0));
                }
            }
            /*
            for (String sourceName : config.getSourceFilePaths()){            
                for (int i = 1; i <= calculateNumberOfLines(sourceName); i++){
                    suspiciousnessList.add(new Suspiciousness(sourceName, i, 0));
                }
            }
            */
        }catch (NullPointerException | IOException e) {
            System.out.println("error");
        }
        
        //任意の行の疑惑値を変更
        return suspiciousnessList;
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

    /*
    private int calculateNumberOfLines(String sourceName){
        int lineNumber = 0;
        try {
            File file = new File(sourceName);
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
    */
}