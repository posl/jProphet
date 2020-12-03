package jp.posl.jprophet.evaluator.extractor;

import java.util.HashSet;
import java.util.Set;

/**
 * 修正パッチの変更の種類を表現するクラス
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

    final private Set<ModKind> kinds;

    /**
     * 空のModKindを生成
     */
    public ModKinds() {
        this.kinds = new HashSet<>();        
    }

    /**
     * kindsを初期値としたModKindを生成
     * @param kinds 初期値
     */
    public ModKinds(Set<ModKind> kinds) {
        this.kinds = kinds;        
    }

    /**
     * 変更の種類を取得
     * @return 種類のセット
     */
    public Set<ModKind> getKinds() {
        return this.kinds;
    }

    /**
     * 変更の種類を追加
     * @param kinds 追加する変更の種類
     */
    public void add(ModKind kind) {
        this.kinds.add(kind);
    }

    /**
     * 二つの変更の種類を足し合わせる
     * @param kinds 加算する種類
     */
    public void add(ModKinds kinds) {
        this.kinds.addAll(kinds.getKinds());
    }
}