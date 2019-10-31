package jp.posl.jprophet.fl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * List<Suspiciousness> のラッパークラス
 * 行番号とファイルパスをインデックスに値を取得できるようにした
 */
public class SuspiciousnessList {
    private final List<Suspiciousness> suspiciousnesses;

    /**
     * 空のSuspiciousnessListを作成
     */
    public SuspiciousnessList(){
        this.suspiciousnesses = new ArrayList<Suspiciousness>();
    }

    /**
     * List<Suspiciousness>を元に作成
     * @param suspiciousnesses
     */
    public SuspiciousnessList(List<Suspiciousness> suspiciousnesses) {
        this.suspiciousnesses = suspiciousnesses;
    }

    /**
     * staticファクトリ
     * 不変なリストを生成する（要素の追加削除置換ができない）
     * List.ofの転送
     * @param suspiciousnesses 生成したいリストの要素の可変長引数
     * @return SuspiciousnessList
     */
    public static SuspiciousnessList of(Suspiciousness... suspiciousnesses){
        return new SuspiciousnessList(List.of(suspiciousnesses));
    }

    /**
     * 行番号とファイルパスを元に疑惑値を取得
     * @param lineNumber j行番号
     * @param path ファイルパス
     * @return 疑惑値
     */
    public Optional<Suspiciousness> get(int lineNumber, String path){
        for(Suspiciousness suspiciousness : this.suspiciousnesses){
            if(suspiciousness.getLineNumber() == lineNumber && suspiciousness.getFQN() == path){
                return Optional.of(suspiciousness);
            }
        }
        return Optional.empty();
    }

    /**
     * リストに新しい疑惑値を追加 
     * Listのaddメソッドの転送
     * @param suspiciousness 追加したい疑惑値
     * @return true（Collections.add のドキュメントによるとtrueを返すっぽいので
）
     */
    public boolean add(Suspiciousness suspiciousness){
        this.suspiciousnesses.add(suspiciousness);
        return true;
    }

    /**
     * stream()の転送
     * @return stream
     */
    public Stream<Suspiciousness> stream(){
        return this.suspiciousnesses.stream();
    }

    /**
     * リストを取得
     * @return リスト
     */
    public List<Suspiciousness> toList(){
        return this.suspiciousnesses;
    }

}