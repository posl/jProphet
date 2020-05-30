package jp.posl.jprophet.evaluator;

public class ValueFeatureVec {
    public boolean constant = false;
    public boolean condition = false;
    public boolean ifStmt = false;
    public boolean loop = false;
    public boolean commutativeOp = false;
    public boolean binaryOpL = false;
    public boolean binaryOpR = false;
    public boolean unaryOp = false;
    public boolean replacedByMod = false;
    public boolean parameter = false;
    public ValueType type = ValueType.OTHER;
    public Scope scope = Scope.FIELD;
    enum ValueType {
        BOOLEAN,
        NUM,
        STRING,
        OBJECT,
        OTHER
    }
    enum Scope {
        FIELD,
        LOCAL,
        ARGUMENT
    }
}
