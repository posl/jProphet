package jp.posl.jprophet.evaluator;

/**
 * 修正パッチの変数の特徴を表現するクラス
 */
public class ValueFeature {
    /* booleanかどうか */
    public boolean boolType = false;
    /* 数値型かどうか */
    public boolean numType = false;
    /* Stringかどうか */
    public boolean stringType = false;
    /* オブジェクト型かどうか */
    public boolean objectType = false;
    /* クラスのフィールドかどうか */
    public boolean field = false;
    /* ローカル変数かどうか */
    public boolean local = false;
    /* 関数の仮引数かどうか */
    public boolean argument = false;
    /* 定数かどうか */
    public boolean constant = false;
    /* 条件式中の変数かどうか */
    public boolean condition = false;
    /* if文中の変数かどうか */
    public boolean ifStmt = false;
    /* ループ構文中の変数かどうか */
    public boolean loop = false;
    /* 実引数かどうか */
    public boolean parameter = false;
    /* 代入文中の変数かどうか */
    public boolean assign = false;
    /* 被単項演算子かどうか */
    public boolean unaryOp = false;
    /* 被可換演算子かどうか */
    public boolean commutativeOp = false;
    /* 被二項演算子の左辺の変数かどうか */
    public boolean noncommutativeOpL = false;
    /* 被二項演算子の右辺の変数かどうか */
    public boolean noncommutativeOpR = false;

    /**
     * 各フィールド同士の論理和を取り更新する
     * @param feature 対象の特徴
     */
    public void add(ValueFeature feature) {
        this.boolType          = this.boolType   || feature.boolType;
        this.numType           = this.numType    || feature.numType;
        this.stringType        = this.stringType || feature.stringType;
        this.objectType        = this.objectType || feature.objectType;
        this.field             = this.field      || feature.field;
        this.local             = this.local      || feature.local;
        this.argument          = this.argument   || feature.argument;
        this.constant          = this.constant   || feature.constant;
        this.condition         = this.condition  || feature.condition;
        this.ifStmt            = this.ifStmt     || feature.ifStmt;
        this.loop              = this.loop       || feature.loop;
        this.parameter         = this.parameter  || feature.parameter;
        this.assign            = this.assign     || feature.assign;
        this.unaryOp           = this.unaryOp    || feature.unaryOp;
        this.commutativeOp     = this.commutativeOp || feature.commutativeOp;
        this.noncommutativeOpL = this.noncommutativeOpL || feature.noncommutativeOpL;
        this.noncommutativeOpR = this.noncommutativeOpR || feature.noncommutativeOpR;
    }
}
