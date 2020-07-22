package jp.posl.jprophet.learning;

import java.util.List;

import jp.posl.jprophet.operation.AstOperation;

public class TrainingConfig {
    final private String dirPath;
    final private String originalFileName;
    final private String fixedFileName;
    final private List<AstOperation> operations;
    public TrainingConfig(String dirPath, String originalFileName, String fixedFileName, List<AstOperation> operations) {
        this.dirPath = dirPath;
        this.originalFileName = originalFileName;
        this.fixedFileName = fixedFileName;
        this.operations = operations;
    }
    public String getDirPath() {
        return this.dirPath;
    }
    public String getOriginalFileName() {
        return this.originalFileName;
    }
    public String getFixedFileName() {
        return this.fixedFileName;
    }
    public List<AstOperation> getOperations() {
        return this.operations;
    }
}