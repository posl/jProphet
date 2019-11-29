package testSBProject02;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import testSBProject02.RoughConstantValue;


public class RoughConstantValueTest {


    
    @Test
    public void TestForCalcCircleArea() {  //あえて大文字にしてみる　このワーニングは対象外
        RoughConstantValue roughConstantValue = new RoughConstantValue();
        assertEquals((int)roughConstantValue.calcCircleArea(50), 314);
        


    }
    

}

