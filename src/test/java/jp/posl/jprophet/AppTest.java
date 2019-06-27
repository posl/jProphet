package testGradle;

import jp.posl.jprophet.TestExecutor;;
import org.junit.Test;                                                                                                                                                                  
import static org.junit.Assert.*;

public class AppTest {
    @Test public void testAppHasAGreeting() {
        TestExecutor testExecutor = new TestExecutor();
        assertNotNull("app should have a greeting", classUnderTest.getGreeting());
    }   
}