package jp.posl.jprophet.evaluator;

/**
 * プログラムのステートメント(行)ごとの特徴を表現するクラス
 */
public class StatementFeature {
    /* 代入文 */
    public int assignStmt;
    /* メソッド呼び出し */
    public int methodCallStmt;
    /* ループ構文 */
    public int loopStmt;
    /* if文 */
    public int ifStmt;
    /* return文 */
    public int returnStmt;
    /* break文 */
    public int breakStmt;
    /* continue文 */
    public int continueStmt;

    /**
     * 全要素を全て0で初期化
     */
    public StatementFeature() {
        this(0, 0, 0, 0, 0, 0, 0);
    }

    /**
     * 各要素を与えられた数値で初期化
     * @param assignStmt     AssignStmtの値
     * @param methodCallStmt MethodCallStmtの値 
     * @param loopStmt       LoopStmtの値
     * @param ifStmt         IfStmtの値
     * @param returnStmt     ReturnStmtの値
     * @param breakStmt      ReplaceMethodの値
     * @param continueStmt   InsertSmtの値
     */
    public StatementFeature(int assignStmt, int methodCallStmt, int loopStmt, int ifStmt, int returnStmt, int breakStmt, int continueStmt) {
        this.assignStmt = assignStmt;
        this.methodCallStmt = methodCallStmt;
        this.loopStmt = loopStmt;
        this.ifStmt = ifStmt;
        this.returnStmt = returnStmt;
        this.breakStmt = breakStmt;
        this.continueStmt = continueStmt;
    }
    
}