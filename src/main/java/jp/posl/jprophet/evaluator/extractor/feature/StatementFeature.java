package jp.posl.jprophet.evaluator.extractor.feature;

import java.util.HashSet;
import java.util.Set;

/**
 * プログラムのステートメント(行)ごとの特徴を表現するクラス
 */
public class StatementFeature {
    public enum StatementType {
        /* 代入文 */
        ASSIGN,
        /* メソッド呼び出し */
        METHOD_CALL,
        /* ループ構文 */
        LOOP,
        /* if文 */
        IF,
        /* return文 */
        RETURN,
        /* break文 */
        BREAK,
        /* continue文 */
        CONTINUE
    };

    final private Set<StatementType> types;

    /**
     * 特徴を持たない空のStatementFeatureを生成
     */
    public StatementFeature() {
        this.types = new HashSet<>();        
    }

    /**
     * typesを初期値としたStatementFeatureを生成
     * @param types 初期値
     */
    public StatementFeature(Set<StatementType> types) {
        this.types = types;        
    }

    /**
     * ステートメントの特徴を取得
     * @return 特徴のセット
     */
    public Set<StatementType> getTypes() {
        return this.types;
    }

    /**
     * ステートメントの特徴を追加
     * @param types 追加するステートメントの種類
     */
    public void add(StatementType type) {
        this.types.add(type);
    }
}