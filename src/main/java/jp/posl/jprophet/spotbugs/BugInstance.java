package jp.posl.jprophet.spotbugs;

public class BugInstance {

    private final String type;
    private final String filePath;
    private final int positionStart;
    private final int positionEnd;
    

    /**
     * SpotBugsによるワーニングの情報をまとめたクラス
     * @param _type         ワーニングの種類
     * @param _filePath     ワーニングを含むソースファイルパス（対象プロジェクトに対する相対パス）
     * @param _start        ワーニングを含む箇所の始めの行
     * @param _end          ワーニングを含む箇所の終わりの行
     */
    public BugInstance(final String _type, final String _filePath, final int _start, final int _end){
        this.type = _type;
        this.filePath = _filePath;
        this.positionStart = _start;
        this.positionEnd = _end;
    }


    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }


    /**
     * @return the filePath
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * @return the positionStart
     */
    public int getPositionStart() {
        return this.positionStart;
    }

    /**
     * @return the positionEnd
     */
    public int getPositionEnd() {
        return this.positionEnd;
    }

}
