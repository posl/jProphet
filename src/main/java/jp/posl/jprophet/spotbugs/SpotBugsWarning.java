package jp.posl.jprophet.spotbugs;

/**
 * SpotBugsによるワーニングの情報をまとめたクラス
 */
public class SpotBugsWarning {

    private final String type;
    private final String filePath;
    private final int startLine;
    private final int endLine;
    

    /**
     * SpotBugsによるワーニングの情報をまとめたクラスの生成
     * @param type         ワーニングの種類
     * @param filePath     ワーニングを含むソースファイルパス（対象プロジェクトに対する相対パス）
     * @param start        ワーニングを含む箇所の始めの行
     * @param end          ワーニングを含む箇所の終わりの行
     */
    public SpotBugsWarning(final String type, final String filePath, final int start, final int end) {
        this.type = type;
        this.filePath = filePath;
        this.startLine = start;
        this.endLine = end;
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
     * @return the startLine
     */
    public int getStartLine() {
        return this.startLine;
    }

    /**
     * @return the finishLine
     */
    public int getEndLine() {
        return this.endLine;
    }


    /**
     * 2つのワーニングが同じものを指しているのか確認する
     * @param other　比較対象のワーニングクラス
     * @return　同じであればTrue、そうでなければFalseを返す
     */
    @Override
    public boolean equals(Object obj) {
        SpotBugsWarning other = (SpotBugsWarning) obj;
        if(!this.type.equals(other.type)) return false;
        if(!this.filePath.equals(other.filePath)) return false;
        if(this.startLine != other.startLine) return false;
        if(this.endLine != other.endLine) return false;
        return true;
    }



    /**
     * ハッシュコードを返す
     * @return ハッシュコード値
     */
    @Override
    public int hashCode() {
        return (type + filePath).hashCode() + startLine + endLine;
    }


    @Override
    public String toString() {
        return String.format("[%s] in %s %d~%d", type, filePath, startLine, endLine);
    }
    

}
