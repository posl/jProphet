package jp.posl.jprophet;

/**
 * リペアの各種設定を保持
 */
public class RepairConfiguration {
    private String buildDirPath;
    private String fixedProjectDirPath;
    private Project targetProject;

    public RepairConfiguration(String buildDirPath, String fixedProjectDirPath, Project targetProject) {
        this.buildDirPath = buildDirPath;
        this.fixedProjectDirPath = fixedProjectDirPath;
        this.targetProject = targetProject;
    }

    /**
     * プロジェクトのビルド時のクラスファイルの出力先のパスを取得
     * @return ビルド先のパス
     */
    public String getBuildPath(){
        return this.buildDirPath;
    }

    /**
     * 修正が完了したプロジェクトの出力先パスを取得
     * @return 修正結果の出力先パス
     */
    public String getFixedProjectDirPath(){
        return this.fixedProjectDirPath;
    }

    public Project getTargetProject(){
        return this.targetProject;
    }
}