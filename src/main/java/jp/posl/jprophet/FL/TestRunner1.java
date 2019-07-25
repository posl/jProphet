package jp.posl.jprophet.FL;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import jp.posl.jprophet.FL.TestJunit1;

public class TestRunner1 {
   public void runtest1() {
      Result result = JUnitCore.runClasses(TestJunit1.class);
		
      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(result.wasSuccessful());
   }
}