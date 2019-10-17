package testGradleProject02;

import org.junit.Test;
import static org.junit.Assert.*;

public class App2Test {
    @Test public void testAppHasAGreeting() {
        App2 classUnderTest = new App2();
        assertNotNull("app should have a greeting", classUnderTest.getGreeting());
    }
}