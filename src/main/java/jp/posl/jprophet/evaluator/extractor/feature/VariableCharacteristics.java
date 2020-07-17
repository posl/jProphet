package jp.posl.jprophet.evaluator.extractor.feature;

import java.util.HashSet;
import java.util.Set;

/**
 * 修正パッチの変数の特徴を表現するクラス
 */
public class VariableCharacteristics {
    public enum VarChar{
        /* booleanかどうか */
        BOOLEAN,
        /* 数値型かどうか */
        NUM,
        /* Stringかどうか */
        STRING,
        /* オブジェクト型かどうか */
        OBJECT,
        /* クラスのフィールドかどうか */
        FIELD,
        /* ローカル変数かどうか */
        LOCAL,
        /* 関数の仮引数かどうか */
        ARGUMENT,
        /* 定数かどうか */
        CONSTANT,
        /* 条件式中の変数かどうか */
        IN_CONDITION,
        /* if文中の変数かどうか */
        IN_IF_STMT,
        /* ループ構文中の変数かどうか */
        IN_LOOP,
        /* 代入文中の変数かどうか */
        IN_ASSIGN_STMT,
        /* 実引数かどうか */
        PARAMETER,
        /* 被単項演算子かどうか */
        UNARY_OPERAND,
        /* 被可換演算子かどうか */
        COMMUTATIVE_OPERAND,
        /* 被二項演算子の左辺の変数かどうか */
        NONCOMMUTATIVE_OPERAND_LEFT,
        /* 被二項演算子の右辺の変数かどうか */
        NONCOMMUTATIVE_OPERAND_RIGHT
    }

    final private Set<VarChar> chars;

    /**
     * 特徴を持たない空のVariableCharacteristicsを生成
     */
    public VariableCharacteristics() {
        this.chars = new HashSet<>();
    }

    /**
     * typesを初期値としたVariableCharacteristicsを生成
     * @param varChars 初期値
     */
    public VariableCharacteristics(Set<VarChar> varChars) {
        this.chars = varChars;
    }

    /**
     * 変数の特徴を取得
     * @return 特徴のセット
     */
    public Set<VarChar> getTypes() {
        return this.chars;
    }

    /**
     * 変数の特徴を追加
     * @param chars 追加する変数の種類
     */
    public void add(VarChar chars) {
        this.chars.add(chars);
    }

    /**
     * 二つの変数の特徴を足し合わせる
     * @param varCharacteristics 加算する特徴
     */
    public void add(VariableCharacteristics varCharacteristics) {
        this.chars.addAll(varCharacteristics.getTypes());
    }
}
