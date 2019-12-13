package jp.posl.jprophet.project;

import java.util.NoSuchElementException;

import jp.posl.jprophet.RepairConfiguration;

/**
 * Projectオブジェクトの生成を行うクラス
 */
public class ProjectFactory {
    /**
     * リペアの設定とプロジェクトのパスを元にProjectを生成
     * <p>TODO: 対象のディレクトリ構造を元にプロジェクトの種類を判断するようにして
     * configを必要無くしたい</p>
     * @param config 修正対象のプロジェクトの情報を含む設定 
     * @param projectPath 新しいプロジェクトのパス
     * @return
     */
    public Project create(RepairConfiguration config, String projectPath) {
        if(config.getTargetProject() instanceof GradleProject) {
            return new GradleProject(projectPath);
        }
        if(config.getTargetProject() instanceof MavenProject) {
            return new MavenProject(projectPath);
        }
        throw new IllegalArgumentException(); 
    }
}

