package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class ProgramChunkTest {
    /**
     * ProgramChunkの生成時に行番号に0以下の値が渡されると例外
     */
    @Test public void testThrowExceptionInConstructor() {
        assertThatThrownBy(() -> new ProgramChunk(-1, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ProgramChunk(0, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * ゲッターのテスト
     */
    @Test public void testGetter() {
        final ProgramChunk chunk = new ProgramChunk(3, 6);
        assertThat(chunk.getBegin()).isEqualTo(3);
        assertThat(chunk.getEnd()).isEqualTo(6);
    }

    /**
     * equalsメソッドがフィールドの値同士の比較ができているかテスト
     */
    @Test public void testEquals() {
        final ProgramChunk chunkA = new ProgramChunk(3, 6);
        final ProgramChunk chunkB = new ProgramChunk(3, 6);
        final ProgramChunk chunkC = new ProgramChunk(1, 2);
        assertThat(chunkA.equals(chunkB)).isEqualTo(true);
        assertThat(chunkA.equals(chunkC)).isEqualTo(false);
    }

    /**
     * equalsメソッドでtrue判定されるオブジェクト同士が同じハッシュコードを返す
     * (実質Objects.hashのテストなのであまり必要がないが一応)
     */
    @Test public void testHashCode() {
        final ProgramChunk chunkA = new ProgramChunk(3, 6);
        final ProgramChunk chunkB = new ProgramChunk(3, 6);
        assertThat(chunkA.equals(chunkB)).isEqualTo(true);
        assertThat(chunkA.hashCode()).isEqualTo(chunkB.hashCode());

        final ProgramChunk chunkC = new ProgramChunk(123, 12345);
        final ProgramChunk chunkD = new ProgramChunk(123, 12345);
        assertThat(chunkC.equals(chunkD)).isEqualTo(true);
        assertThat(chunkC.hashCode()).isEqualTo(chunkD.hashCode());
    }
    
}