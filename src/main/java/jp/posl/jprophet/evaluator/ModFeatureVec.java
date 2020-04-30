package jp.posl.jprophet.evaluator;

public class ModFeatureVec {
    public int insertControl = 0;
    public int insertGuard = 0;
    public int replaceCond = 0;
    public int replaceStmt = 0;
    public int insertStmt = 0;

    public void add(ModFeatureVec vec) {
        this.insertControl += vec.insertControl;
        this.insertGuard += vec.insertGuard;
        this.replaceCond += vec.replaceCond;
        this.replaceStmt += vec.replaceStmt;
        this.insertStmt += vec.insertStmt;
    }

}