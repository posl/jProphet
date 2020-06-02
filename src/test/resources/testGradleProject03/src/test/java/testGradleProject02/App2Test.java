package testGradleProject02;

import org.junit.Test;
import static org.junit.Assert.*;

public class App2Test {

    @Test public void testMethod1() {
        App2 classUnderTest = new App2();
        classUnderTest.method();
    }
    @Test public void testLoop() {
        App2 classUnderTest = new App2();
        classUnderTest.loop();
    }

    @Test public void testMethod2() {
        App2 classUnderTest = new App2();
        classUnderTest.method();
    }
}