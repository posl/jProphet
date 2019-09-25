package testFLProject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IfstatementTest4 {
    
    @Test public void test4() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(4), is(6));
    }
    
}