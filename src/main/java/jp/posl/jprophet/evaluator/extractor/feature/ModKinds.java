package jp.posl.jprophet.evaluator.extractor.feature;

import java.util.HashSet;
import java.util.Set;

/**
 * 修正パッチの変更の特徴を表現するクラス
 */
public class ModKinds {
    public enum ModKind {
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

    final private Set<ModKind> types;

    /**
     * 特徴を持たない空のModKindを生成
     */
    public ModKinds() {
        this.types = new HashSet<>();        
    }

    /**
     * typesを初期値としたModKindを生成
     * @param types 初期値
     */
    public ModKinds(Set<ModKind> types) {
        this.types = types;        
    }

    /**
     * 変更の特徴を取得
     * @return 特徴のセット
     */
    public Set<ModKind> getTypes() {
        return this.types;
    }

    /**
     * 変更の特徴を追加
     * @param types 追加する変更の種類
     */
    public void add(ModKind type) {
        this.types.add(type);
    }

    /**
     * 二つの変更の特徴を足し合わせる
     * @param kinds 加算する特徴
     */
    public void add(ModKinds kinds) {
        this.types.addAll(kinds.getTypes());
    }
}