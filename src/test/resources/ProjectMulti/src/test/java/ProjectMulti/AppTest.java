/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ProjectMulti;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test public void testAppHasAGreeting() {
        App classUnderTest = new App();
        System.out.println(classUnderTest.get());
        assertEquals(classUnderTest.get(), 1);
    }
}