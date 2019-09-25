package testFLProject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IfstatementTest3 {
    
    @Test public void test3() {
        Ifstatement ifTest = new Ifstatement();
        assertThat(ifTest.plus(3), is(3));
    }
    
}