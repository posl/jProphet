package testFLProject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IfstatementTest2 {
    
    @Test public void test2() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(0), is(1));
    }
    
}