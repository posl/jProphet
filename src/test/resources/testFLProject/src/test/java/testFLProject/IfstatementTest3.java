package testFLProject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IfstatementTest3 {
    
    @Test public void test4_5() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(4), is(5));
    }
    
}