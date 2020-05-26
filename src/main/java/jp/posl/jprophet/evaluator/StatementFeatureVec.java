package jp.posl.jprophet.evaluator;

/**
 * プログラムのステートメント(行)ごとの特徴を表現するベクトル 
 */
public class StatementFeatureVec {
    /* 代入文 */
    public int assignStmt = 0;
    /* メソッド呼び出し */
    public int methodCallStmt = 0;
    /* ループ構文 */
    public int loopStmt = 0;
    /* if文 */
    public int ifStmt = 0;
    /* return文 */
    public int returnStmt = 0;
    /* break文 */
    public int breakStmt = 0;
    /* continue文 */
    public int continueStmt = 0;

    /**
     * 全要素を全て0で初期化されたベクトルを生成
     */
    public StatementFeatureVec() {
        /* 何もしない */
    }

    /**
     * 各要素を与えられた数値で初期化してベクトルを生成
     * @param assignStmt     AssignStmtの値
     * @param methodCallStmt MethodCallStmtの値 
     * @param loopStmt       LoopStmtの値
     * @param ifStmt         IfStmtの値
     * @param returnStmt     ReturnStmtの値
     * @param breakStmt      ReplaceMethodの値
     * @param continueStmt   InsertSmtの値
     */
    public StatementFeatureVec(int assignStmt, int methodCallStmt, int loopStmt, int ifStmt, int returnStmt, int breakStmt, int continueStmt) {
        this.assignStmt = assignStmt;
        this.methodCallStmt = methodCallStmt;
        this.loopStmt = loopStmt;
        this.ifStmt = ifStmt;
        this.returnStmt = returnStmt;
        this.breakStmt = breakStmt;
        this.continueStmt = continueStmt;
    }
    
}