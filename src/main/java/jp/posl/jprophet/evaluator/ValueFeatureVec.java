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

    public ValueFeatureVec() {
        this(false, false, false, false, false, false, false, false, false, false, ValueType.OTHER, Scope.FIELD);
    }
    public ValueFeatureVec(boolean constant, boolean condition, boolean ifStmt, boolean loop,
            boolean commutativeOp, boolean binaryOpL, boolean binaryOpR, boolean unaryOp,
            boolean replacedByMod, boolean parameter, ValueType type, Scope scope) {
        this.constant = constant;
        this.condition = condition;
        this.ifStmt = ifStmt;
        this.loop = loop;
        this.commutativeOp = commutativeOp;
        this.binaryOpL = binaryOpL;
        this.binaryOpR = binaryOpR;
        this.unaryOp = unaryOp;
        this.replacedByMod = replacedByMod;
        this.parameter = parameter;
        this.type = type;
        this.scope = scope;
    }
}
