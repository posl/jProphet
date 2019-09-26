package testFLProject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IfstatementTest2 {
    
    @Test public void test3_4() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(3), is(4));
    }
    
}