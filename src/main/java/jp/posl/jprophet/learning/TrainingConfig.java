package jp.posl.jprophet.learning;

import java.util.List;

import jp.posl.jprophet.operation.AstOperation;

public class TrainingConfig {
    final private String dirPath;
    final private String originalDirName;
    final private String fixedDirName;
    final private List<AstOperation> operations;

    public TrainingConfig(String dirPath, String originalDirName, String fixedDirName, List<AstOperation> operations) {
        this.dirPath = dirPath;
        this.originalDirName = originalDirName;
        this.fixedDirName = fixedDirName;
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

    public List<AstOperation> getOperations() {
        return this.operations;
    }
}