package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class ProgramChankTest {
    @Test public void testThrowExceptionInConstructor() {
        assertThatThrownBy(() -> new ProgramChank(-1, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test public void testGetter() {
        final ProgramChank chank = new ProgramChank(3, 6);
        assertThat(chank.getBegin()).isEqualTo(3);
        assertThat(chank.getEnd()).isEqualTo(6);
    }

    @Test public void testEquals() {
        final ProgramChank chankA = new ProgramChank(3, 6);
        final ProgramChank chankB = new ProgramChank(3, 6);
        final ProgramChank chankC = new ProgramChank(1, 2);
        assertThat(chankA.equals(chankB)).isEqualTo(true);
        assertThat(chankA.equals(chankC)).isEqualTo(false);
    }

    @Test public void testHashCode() {
        final ProgramChank chankA = new ProgramChank(3, 6);
        final ProgramChank chankB = new ProgramChank(3, 6);
        assertThat(chankA.equals(chankB)).isEqualTo(true);
        assertThat(chankA.hashCode()).isEqualTo(chankB.hashCode());

        final ProgramChank chankC = new ProgramChank(123, 12345);
        final ProgramChank chankD = new ProgramChank(123, 12345);
        assertThat(chankC.equals(chankD)).isEqualTo(true);
        assertThat(chankC.hashCode()).isEqualTo(chankD.hashCode());
    }
    
}