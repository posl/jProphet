package jp.posl.jprophet.evaluator;

public class ValueFeature {
    public boolean constant;
    public boolean condition;
    public boolean ifStmt;
    public boolean loop;
    public boolean parameter;
    public boolean assign;
    public boolean commutativeOp;
    public boolean noncommutativeOpL;
    public boolean noncommutativeOpR;
    public boolean unaryOp;
    public VarType type;
    public Scope scope;
    enum VarType {
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

    public ValueFeature() {
        this(false, false, false, false, false, false, false, false, false, VarType.OTHER, Scope.FIELD);
    }

    public ValueFeature(boolean constant, boolean condition, boolean ifStmt, boolean loop,
            boolean commutativeOp, boolean noncommutativeOpL, boolean noncommutativeOpR,
            boolean unaryOp, boolean parameter, VarType type, Scope scope) {
        this.constant = constant;
        this.condition = condition;
        this.ifStmt = ifStmt;
        this.loop = loop;
        this.commutativeOp = commutativeOp;
        this.noncommutativeOpL = noncommutativeOpL;
        this.noncommutativeOpR = noncommutativeOpR;
        this.unaryOp = unaryOp;
        this.parameter = parameter;
        this.type = type;
        this.scope = scope;
    }
}
