package jp.posl.jprophet.evaluator;

public class StatementFeatureVec {
    public int assignStmt = 0;
    public int methodCallStmt = 0;
    public int loopStmt = 0;
    public int ifStmt = 0;
    public int returnStmt = 0;
    public int breakStmt = 0;
    public int continueStmt = 0;

    public StatementFeatureVec() {}

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