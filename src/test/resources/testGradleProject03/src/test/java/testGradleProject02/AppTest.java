/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package testGradleProject02;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test public void testAppHasAGreeting() { //エラーが起こるはず
        App classUnderTest = new App();
        assertNotNull("app should have a greeting", classUnderTest.getGreeting());
    }
}
