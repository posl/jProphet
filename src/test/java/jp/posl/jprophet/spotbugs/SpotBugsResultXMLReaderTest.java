package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;


public class SpotBugsResultXMLReaderTest {


    /**
     * 全てのワーニングを取得できているかテスト
     */
    @Test
    public void testForReadAllSpotBugsWarnings() {
        final SpotBugsResultXMLReader reader = new SpotBugsResultXMLReader();
        final List<SpotBugsWarning> instances = reader.readAllSpotBugsWarnings("src/test/resources/SpotBugsResult/result01.xml");
        assertThat(instances).hasSize(5);   // 全てのバグを取得できているか
        
        final SpotBugsWarning target = instances.get(1); //試しに2番目のバグについて調べる
        assertThat(target.getType()).isEqualTo("NM_METHOD_NAMING_CONVENTION");
        assertThat(target.getFilePath()).isEqualTo("testSBProject01/App2.java");
        assertThat(target.getStartLine()).isEqualTo(13);
        assertThat(target.getEndLine()).isEqualTo(20);
        

    }


}