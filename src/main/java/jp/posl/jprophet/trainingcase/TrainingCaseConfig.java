package jp.posl.jprophet.trainingcase;

import java.util.List;

import jp.posl.jprophet.operation.AstOperation;

/**
 * トレーニングケース生成のための設定をまとめたクラス
 */
public class TrainingCaseConfig {
    final private String dirPathContainsPatchFiles;
    final private String originalDirName;
    final private String fixedDirName;
    final private String exportPath;
    final private List<AstOperation> operations;

    /**
     * @param dirPathContainsPatchFiles トレーニングケース生成元のパッチファイル群が存在するディレクトリ名
     * @param originalDirName パッチディレクトリ下の修正前ファイルが存在するディレクトリ名
     * @param fixedDirName パッチディレクトリ下の修正後ファイルが存在するディレクトリ名
     * @param exportPath トレーニングケース出力先のパス
     * @param operations ダミーパッチ生成に利用する修正テンプレートのリスト
     */
    public TrainingCaseConfig(String dirPathContainsPatchFiles, String originalDirName, String fixedDirName, String exportPath, List<AstOperation> operations) {
        this.dirPathContainsPatchFiles = dirPathContainsPatchFiles;
        this.originalDirName = originalDirName;
        this.fixedDirName = fixedDirName;
        this.exportPath = exportPath;
        this.operations = operations;
    }

    /**
     * @return トレーニングケース生成元のパッチファイル群が存在するディレクトリ名
     */
    public String getDirPathContainsPatchFiles() {
        return this.dirPathContainsPatchFiles;
    }

    /**
     * @return パッチディレクトリ下の修正前ファイルが存在するディレクトリ名
     */
    public String getOriginalDirName() {
        return this.originalDirName;
    }

    /**
     * @return パッチディレクトリ下の修正後ファイルが存在するディレクトリ名
     */
    public String getFixedDirName() {
        return this.fixedDirName;
    }

    /**
     * @return トレーニングケース出力先のパス
     */
    public String getExportPath() {
        return this.exportPath;
    }

    /**
     * @return トレーニングケース出力先のパス
     */
    public List<AstOperation> getOperations() {
        return this.operations;
    }
} 