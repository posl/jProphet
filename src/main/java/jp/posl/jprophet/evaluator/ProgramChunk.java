package jp.posl.jprophet.evaluator;

import java.util.Objects;

/**
 * 連続するコードの塊を行数を元に表現するクラス
 */
public class ProgramChunk {
    final private int begin;
    final private int end;

    /**
     * チャンクを生成
     * @param begin チャンクの先頭行番号
     * @param end チャンクの末尾行番号
     * @throws IllegalArgumentException 行数が負の数の場合
     */
    public ProgramChunk(int begin, int end) {
        if(begin < 1 || end < 1) {
            throw new IllegalArgumentException("Must be greater than zero.");
        }
        this.begin = begin;
        this.end = end;
    }

    /**
     * @return チャンクの先頭行番号
     */
    public int getBegin() {
        return this.begin;
    }

    /**
     * @return チャンクの末尾行番号
     */
    public int getEnd() {
        return this.end;
    }

    /**
     * フィールドの整数値が完全一致する場合に{@code true}を返す．
     * Mapのキーとして本クラスのインスタンスを利用するためにオーバライドした
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(!(obj instanceof ProgramChunk)) {
            return false;
        }

        final ProgramChunk chunk = (ProgramChunk)obj;
        if(chunk.getBegin() == this.begin && chunk.getEnd() == this.end) {
            return true;
        }
        return false;
    }

    /**
     * {@code equals}メソッドと同じくMapのキーとして本クラスのインスタンスを利用するためにオーバライドした
     * <p>
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.begin, this.end);
    }
}