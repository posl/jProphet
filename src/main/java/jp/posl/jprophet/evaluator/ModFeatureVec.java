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
    /* 変数の置き換え */
    public int replaceVar;
    /* メソッドの置き換え */
    public int replaceMethod;
    /* ステートメントの挿入 */
    public int insertStmt;

    /**
     * 全要素を全て0で初期化してベクトルを生成
     */
    public ModFeatureVec() {
        insertControl = 0;
        insertGuard = 0;
        replaceCond = 0;
        replaceVar = 0;
        replaceMethod = 0;
        insertStmt = 0;
    };

    /**
     * 各要素を与えられた数値で初期化してベクトルを生成
     * @param insertControl InsertControlの値
     * @param insertGuard   InsertGuardの値
     * @param replaceCond   ReplaceCondの値
     * @param replaceVar    ReplaceVarの値
     * @param replaceMethod ReplaceMethodの値
     * @param insertStmt    InsertStmtの値
     */
    public ModFeatureVec(int insertControl, int insertGuard, int replaceCond, int replaceVar, int replaceMethod, int insertStmt) {
        this.insertControl = insertControl;
        this.insertGuard = insertGuard;
        this.replaceCond = replaceCond;
        this.replaceVar = replaceVar;
        this.replaceMethod = replaceMethod;
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
        this.replaceVar += addendVec.replaceVar;
        this.replaceMethod += addendVec.replaceMethod;
        this.insertStmt += addendVec.insertStmt;
    }

}