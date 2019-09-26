package testFLProject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IfstatementTest4 {
    
    @Test public void test0_0() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(0), is(0));
    }
    
}