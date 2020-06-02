package jp.posl.jprophet.evaluator;

/**
 * 修正パッチの変更の特徴を表現するクラス
 */
public class ModFeature {
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
     * 全要素を全て0で初期化
     */
    public ModFeature() {
        this(0, 0, 0, 0, 0, 0);
    };

    /**
     * 各要素を与えられた数値で初期化して生成
     * @param insertControl InsertControlの値
     * @param insertGuard   InsertGuardの値
     * @param replaceCond   ReplaceCondの値
     * @param replaceVar    ReplaceVarの値
     * @param replaceMethod ReplaceMethodの値
     * @param insertStmt    InsertStmtの値
     */
    public ModFeature(int insertControl, int insertGuard, int replaceCond, int replaceVar, int replaceMethod, int insertStmt) {
        this.insertControl = insertControl;
        this.insertGuard = insertGuard;
        this.replaceCond = replaceCond;
        this.replaceVar = replaceVar;
        this.replaceMethod = replaceMethod;
        this.insertStmt = insertStmt;
    };

    /**
     * 各フィールドに別の{@code ModFeature}の各フィールドの数値をそれぞれ加算する 
     * @param addend 加算するModFeature
     */
    public void add(ModFeature addend) {
        this.insertControl += addend.insertControl;
        this.insertGuard += addend.insertGuard;
        this.replaceCond += addend.replaceCond;
        this.replaceVar += addend.replaceVar;
        this.replaceMethod += addend.replaceMethod;
        this.insertStmt += addend.insertStmt;
    }

}