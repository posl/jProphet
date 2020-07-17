package jp.posl.jprophet.evaluator.extractor.feature;

import java.util.HashSet;
import java.util.Set;

/**
 * 修正パッチの変数の特徴を表現するクラス
 */
public class VariableFeature {
    public enum VarType {
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

    final private Set<VarType> types;

    /**
     * 特徴を持たない空のVariableFeatureを生成
     */
    public VariableFeature() {
        this.types = new HashSet<>();
    }

    /**
     * typesを初期値としたVariableFeatureを生成
     * @param types 初期値
     */
    public VariableFeature(Set<VarType> types) {
        this.types = types;
    }

    /**
     * 変数の特徴を取得
     * @return 特徴のセット
     */
    public Set<VarType> getTypes() {
        return this.types;
    }

    /**
     * 変数の特徴を追加
     * @param types 追加する変数の種類
     */
    public void add(VarType type) {
        this.types.add(type);
    }

    /**
     * 二つの変数の特徴を足し合わせる
     * @param feature 加算する特徴
     */
    public void add(VariableFeature feature) {
        this.types.addAll(feature.getTypes());
    }
}
