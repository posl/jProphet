package jp.posl.jprophet;

import java.util.Optional;

import jp.posl.jprophet.project.Project;

/**
 * リペアの各種設定を保持
 */
public class RepairConfiguration {
    private final String buildDirPath;
    private final String fixedProjectDirPath;
    private final Project targetProject;
    private final String parameterPath;

    /**
     * ビルド先パス，修正済みプロジェクト生成先パス，対象プロジェクトを元に生成する
     * @param buildDirPath
     * @param fixedProjectDirPath
     * @param project
     */
    public RepairConfiguration(String buildDirPath, String fixedProjectDirPath, Project project, String parameterPath) {
        this.buildDirPath = buildDirPath;
        this.fixedProjectDirPath = fixedProjectDirPath;
        this.targetProject = project;
        this.parameterPath = parameterPath;
    }

    /**
     * ビルド先パス，修正済みプロジェクト生成先パス，対象プロジェクトを元に生成する
     * @param buildDirPath
     * @param fixedProjectDirPath
     * @param project
     */
    public RepairConfiguration(String buildDirPath, String fixedProjectDirPath, Project project) {
        this(buildDirPath, fixedProjectDirPath, project, null);
    }

    /**
     * 既存のconfigと新しいProjectから生成する
     * @param config
     * @param newTargetProject
     */
    public RepairConfiguration(RepairConfiguration config, Project newTargetProject) {
        this(config.getBuildPath(), config.getFixedProjectDirPath(), newTargetProject, config.getParameterPath().orElse(null));
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

    public Optional<String> getParameterPath() {
        if(this.parameterPath == null) {
            return Optional.empty();
        }
        else {
            return Optional.of(this.parameterPath);
        }
    }
}