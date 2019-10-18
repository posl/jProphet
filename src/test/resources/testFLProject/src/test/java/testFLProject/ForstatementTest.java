package testFLProject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ForstatementTest {
    
    @Test public void Ftest0_1() {
        Forstatement forTest = new Forstatement();
        assertThat(forTest.kaijo(0), is(1));
    }
    
    @Test public void Ftest1_1() {
        Forstatement forTest = new Forstatement();
        assertThat(forTest.kaijo(1), is(1));
    }

    @Test public void Ftest2_2() {
        Forstatement forTest = new Forstatement();
        assertThat(forTest.kaijo(2), is(2));
    }

    @Test public void Ftest3_6() {
        Forstatement forTest = new Forstatement();
        assertThat(forTest.kaijo(3), is(6));
    }
}