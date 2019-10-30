package jp.posl.jprophet.spotbugs;

public class BugInstance {

    private final String type;
    private final String filePath;
    private final int positionStart;
    private final int positionEnd;
    

    /**
     * SpotBugsによるワーニングの情報をまとめたクラス
     * @param type         ワーニングの種類
     * @param filePath     ワーニングを含むソースファイルパス（対象プロジェクトに対する相対パス）
     * @param start        ワーニングを含む箇所の始めの行
     * @param end          ワーニングを含む箇所の終わりの行
     */
    public BugInstance(final String type, final String filePath, final int start, final int end){
        this.type = type;
        this.filePath = filePath;
        this.positionStart = start;
        this.positionEnd = end;
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
