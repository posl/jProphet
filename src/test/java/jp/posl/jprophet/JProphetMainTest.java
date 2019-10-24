package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class JProphetMainTest {
    @Test public void testJProphet(){
        String[] project = {"src/test/resources/testGradleProject01"};
        JProphetMain.main(project);
    }
}