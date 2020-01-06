package testMavenProject01;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import testMavenProject01.RoughConstantValue;


public class RoughConstantValueTest {


    
    @Test
    public void TestForCalcCircleArea() { 
        RoughConstantValue roughConstantValue = new RoughConstantValue();
        assertEquals((int)roughConstantValue.calcCircleArea(50), 314);
        


    }
    

}

