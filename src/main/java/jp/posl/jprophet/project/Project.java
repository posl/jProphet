package jp.posl.jprophet.project;

import java.util.List;

public interface Project {

    /**
     * プロジェクトのソースファイルのFileLocatorオブジェクトを全て取得 
     * @return ソースファイルのFileLocatorのリスト
     */
    public List<FileLocator> getSrcFileLocators(); 

    /**
     * プロジェクトのテストファイルのFileLocatorオブジェクトを全てを取得
     * @return テストファイルのFileLocatorのリストj
     */
    public List<FileLocator> getTestFileLocators(); 

    /**
     * プロジェクトのソースファイルのパスを全て取得 
     * @return ソースファイルのリスト
     */
    public List<String> getSrcFilePaths(); 

    /**
     * プロジェクトのテストファイルのパスを全てを取得
     * @return テストファイルのパスのリスト
     */
    public List<String> getTestFilePaths(); 

    /**
     * プロジェクトのソースファイルのFQNを全て取得 
     * @return ソースファイルのfqnのリスト
     */
    public List<String> getSrcFileFqns(); 

    /**
     * プロジェクトのテストファイルのFQNを全てを取得
     * @return テストファイルのFQNのリスト
     */
    public List<String> getTestFileFqns(); 

    /**
     * プロジェクトのビルドに必要なクラスパスを取得
     * @return クラスパスの一覧
     */
    public List<String> getClassPaths(); 

    /**
     * プロジェクトのパスを取得
     * @return プロジェクトのパス
     */
    public String getRootPath();
}