package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class JProphetMainTest {
    /**
     * FizzBuzz01プロジェクトのバグが治るかテスト
     * Value Replace Operationのみで治るバグ 
     */
    @Test public void testFizzBuzz(){
        String[] project = {"src/test/resources/FizzBuzz01"};
        JProphetMain.main(project);
        File file = new File("src/test/resources/FizzBuzz01");
        assertThat(file.exists()).isTrue();
        try {
            FileUtils.deleteDirectory(new File("./result/"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}