package jp.posl.jprophet.evaluator.extractor.feature;

import java.util.HashSet;
import java.util.Set;

/**
 * 修正パッチの変更の特徴を表現するクラス
 */
public class ModFeature {
    public enum ModType {
        /* ifガードを伴う制御文の挿入 */
        INSERT_CONTROL,
        /* ifガードの挿入 */
        INSERT_GUARD,
        /* 条件式の置き換え */
        REPLACE_COND,
        /* 変数の置き換え */
        REPLACE_VAR,
        /* メソッドの置き換え */
        REPLACE_METHOD,
        /* ステートメントの挿入 */
        INSERT_STMT
    }

    final private Set<ModType> types;

    /**
     * 特徴を持たない空のModFeatureを生成
     */
    public ModFeature() {
        this.types = new HashSet<>();        
    }

    /**
     * typesを初期値としたModFeatureを生成
     * @param types 初期値
     */
    public ModFeature(Set<ModType> types) {
        this.types = types;        
    }

    /**
     * 変更の特徴を取得
     * @return 特徴のセット
     */
    public Set<ModType> getTypes() {
        return this.types;
    }

    /**
     * 変更の特徴を追加
     * @param types 追加する変更の種類
     */
    public void add(ModType type) {
        this.types.add(type);
    }

    /**
     * 二つの変更の特徴を足し合わせる
     * @param feature 加算する特徴
     */
    public void add(ModFeature feature) {
        this.types.addAll(feature.getTypes());
    }
}