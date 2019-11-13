package jp.posl.jprophet.spotbugs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import jp.posl.jprophet.project.GradleProject;


public class SpotBugsResultXMLReaderTest {


    /**
     * 全てのワーニングを取得できているかテスト
     */
    @Test
    public void testForReadAllSpotBugsWarnings() {
        GradleProject stubProject = mock(GradleProject.class);
        when(stubProject.getSrcFilePaths()).thenReturn(List.of("testSBProject01/App2.java")); //取得対象はApp2のみ App2Testは対象外
        final SpotBugsResultXMLReader reader = new SpotBugsResultXMLReader();
        final List<SpotBugsWarning> warnings = reader.readAllSpotBugsWarnings("src/test/resources/SpotBugsResult/result01.xml", stubProject);
        //assertThat(warnings).hasSize(5);   // App2Test以外の全てのバグを取得できているか
        assertThat(warnings).hasSize(6);   // App2Test以外の全てのバグを取得できているか
        
        final SpotBugsWarning target = warnings.get(1); //試しに2番目のバグについて調べる
        assertThat(target.getType()).isEqualTo("NM_METHOD_NAMING_CONVENTION");
        assertThat(target.getFilePath()).isEqualTo("testSBProject01/App2.java");
        assertThat(target.getStartLine()).isEqualTo(13);
        assertThat(target.getEndLine()).isEqualTo(20);

        //for(SpotBugsWarning warning : warnings) {
        //    assertThat(warning.getFilePath()).isEqualTo("testSBProject01/App2.java"); //念のため確認
        //}
        

    }


}