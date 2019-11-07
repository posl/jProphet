package jp.posl.jprophet;

import org.junit.Test;

public class JProphetMainTest {
    @Test public void testJProphet(){
        String[] project = {"src/test/resources/testGradleProject01"};
        JProphetMain.main(project);
    }
}