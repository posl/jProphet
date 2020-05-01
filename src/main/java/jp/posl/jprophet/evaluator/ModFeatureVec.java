package jp.posl.jprophet.evaluator;

public class ModFeatureVec {
    public int insertControl;
    public int insertGuard;
    public int replaceCond;
    public int replaceStmt;
    public int insertStmt;

    public ModFeatureVec() {
        insertControl = 0;
        insertGuard = 0;
        replaceCond = 0;
        replaceStmt = 0;
        insertStmt = 0;
    };

    public ModFeatureVec(int insertControl, int insertGuard, int replaceCond, int replaceStmt, int insertStmt) {
        this.insertControl = insertControl;
        this.insertGuard = insertGuard;
        this.replaceCond = replaceCond;
        this.replaceStmt = replaceStmt;
        this.insertStmt = insertStmt;
    };

    public void add(ModFeatureVec vec) {
        this.insertControl += vec.insertControl;
        this.insertGuard += vec.insertGuard;
        this.replaceCond += vec.replaceCond;
        this.replaceStmt += vec.replaceStmt;
        this.insertStmt += vec.insertStmt;
    }

}