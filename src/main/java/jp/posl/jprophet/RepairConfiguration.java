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
    private final int patchCompressionRatio;

    /**
     * ビルド先パス，修正済みプロジェクト生成先パス，対象プロジェクト，学習済みパラメータパス，
     * ビルド高速化のためのパッチ圧縮率を元に生成する
     * @param buildDirPath
     * @param fixedProjectDirPath
     * @param project
     * @param parameterPath
     * @param patchCompressionRatio 1から9まで指定可能
     */
    public RepairConfiguration(String buildDirPath, String fixedProjectDirPath, Project project, String parameterPath, int patchCompressionRatio) {
        this.buildDirPath = buildDirPath;
        this.fixedProjectDirPath = fixedProjectDirPath;
        this.targetProject = project;
        this.parameterPath = parameterPath;
        this.patchCompressionRatio = patchCompressionRatio;
    }

    /**
     * ビルド先パス，修正済みプロジェクト生成先パス，対象プロジェクト, 学習済みパラメータパスを元に生成する
     * @param buildDirPath
     * @param fixedProjectDirPath
     * @param project
     * @param parameterPath
     */
    public RepairConfiguration(String buildDirPath, String fixedProjectDirPath, Project project, String parameterPath) {
        this(buildDirPath, fixedProjectDirPath, project, parameterPath, 1);
    }

    /**
     * ビルド先パス，修正済みプロジェクト生成先パス，対象プロジェクトを元に生成する
     * @param buildDirPath
     * @param fixedProjectDirPath
     * @param project
     */
    public RepairConfiguration(String buildDirPath, String fixedProjectDirPath, Project project) {
        this(buildDirPath, fixedProjectDirPath, project, null, 1);
    }

    /**
     * 既存のconfigと新しいProjectから生成する
     * @param config
     * @param newTargetProject
     */
    public RepairConfiguration(RepairConfiguration config, Project newTargetProject) {
        this(config.getBuildPath(), config.getFixedProjectDirPath(), newTargetProject, config.getParameterPath().orElse(null), config.getPatchCompressionRatio());
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

    public int getPatchCompressionRatio() {
        return this.patchCompressionRatio;
    }
}