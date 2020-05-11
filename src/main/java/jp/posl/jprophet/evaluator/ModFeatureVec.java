package jp.posl.jprophet.evaluator;

/**
 * 修正パッチの変更の特徴ベクトル
 */
public class ModFeatureVec {
    /*  ifガードを伴う制御文の挿入 */
    public int insertControl;
    /* ifガードの挿入 */
    public int insertGuard;
    /* 条件式の置き換え */
    public int replaceCond;
    /* ステートメントの置き換え */
    public int replaceStmt;
    /* ステートメントの挿入 */
    public int insertStmt;

    /**
     * 全要素を全て0で初期化してベクトルを生成
     */
    public ModFeatureVec() {
        insertControl = 0;
        insertGuard = 0;
        replaceCond = 0;
        replaceStmt = 0;
        insertStmt = 0;
    };

    /**
     * 各要素を与えられた数値で初期化してベクトルを生成
     * @param insertControl InsertControlの値
     * @param insertGuard InsertGuardの値
     * @param replaceCond ReplaceCondの値
     * @param replaceStmt ReplaceStmtの値
     * @param insertStmt InsertStmtの値
     */
    public ModFeatureVec(int insertControl, int insertGuard, int replaceCond, int replaceStmt, int insertStmt) {
        this.insertControl = insertControl;
        this.insertGuard = insertGuard;
        this.replaceCond = replaceCond;
        this.replaceStmt = replaceStmt;
        this.insertStmt = insertStmt;
    };

    /**
     * このベクトルの各要素に別の{@code ModFeatureVec}の各要素の数値をそれぞれ加算する 
     * @param addendVec 加算するベクトル
     */
    public void add(ModFeatureVec addendVec) {
        this.insertControl += addendVec.insertControl;
        this.insertGuard += addendVec.insertGuard;
        this.replaceCond += addendVec.replaceCond;
        this.replaceStmt += addendVec.replaceStmt;
        this.insertStmt += addendVec.insertStmt;
    }

}