package testFLProject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IfstatementTest {
    @Test public void test11() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(1), is(2));
    }
    @Test public void test12() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(2), is(4));
    }
    @Test public void test13() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(3), is(4));
    }
    @Test public void test14() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(4), is(5));
    }
}