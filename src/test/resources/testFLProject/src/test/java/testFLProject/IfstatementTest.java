package testFLProject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IfstatementTest {
    @Test public void test1_2() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(1), is(2));
    }
    @Test public void test2_3() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(2), is(3));
    }
}