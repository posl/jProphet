package jp.posl.jprophet.trainingcase;

import java.util.List;

import jp.posl.jprophet.operation.AstOperation;

public class TrainingCaseConfig {
    final private String dirPath;
    final private String originalDirName;
    final private String fixedDirName;
    final private String outputPath;
    final private List<AstOperation> operations;

    public TrainingCaseConfig(String dirPath, String originalDirName, String fixedDirName, String outputPath, List<AstOperation> operations) {
        this.dirPath = dirPath;
        this.originalDirName = originalDirName;
        this.fixedDirName = fixedDirName;
        this.outputPath = outputPath;
        this.operations = operations;
    }

    public String getDirPath() {
        return this.dirPath;
    }

    public String getOriginalDirName() {
        return this.originalDirName;
    }

    public String getFixedDirName() {
        return this.fixedDirName;
    }

    public String getOutputPath() {
        return this.outputPath;
    }

    public List<AstOperation> getOperations() {
        return this.operations;
    }
} 