package MavenFizzBuzz01;

import org.junit.Test;
import static org.junit.Assert.*;

public class FizzBuzzTest {
    FizzBuzz fizzBuzz = new FizzBuzz();

    @Test public void testNotFizzOrBuzz() {
        assertEquals("1", fizzBuzz.call(1));
        assertEquals("127", fizzBuzz.call(127));
    }
    @Test public void testFizz() {
        assertEquals("Fizz", fizzBuzz.call(3));
        assertEquals("Fizz", fizzBuzz.call(333));
    }
    @Test public void testBuzz() {
        assertEquals("Buzz", fizzBuzz.call(5));
        assertEquals("Buzz", fizzBuzz.call(550));
    }
    @Test public void testFizzBuzz() {
        assertEquals("FizzBuzz", fizzBuzz.call(15));
        assertEquals("FizzBuzz", fizzBuzz.call(150));
    }
}
