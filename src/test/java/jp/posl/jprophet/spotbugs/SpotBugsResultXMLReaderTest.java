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
        when(stubProject.getSrcFileFqns()).thenReturn(List.of("testSBProject01.App2")); //取得対象はApp2のみ App2Testは対象外
        final SpotBugsResultXMLReader reader = new SpotBugsResultXMLReader();
        final List<SpotBugsWarning> warnings = reader.readAllSpotBugsWarnings("src/test/resources/SpotBugsResult/result01.xml", stubProject);
        assertThat(warnings).hasSize(5);   // App2Test以外の全てのバグを取得できているか
        
        final SpotBugsWarning target = warnings.get(1); //試しに2番目のバグについて調べる
        assertThat(target.getType()).isEqualTo("NM_METHOD_NAMING_CONVENTION");
        assertThat(target.getFqn()).isEqualTo("testSBProject01.App2");
        assertThat(target.getStartLine()).isEqualTo(13);
        assertThat(target.getEndLine()).isEqualTo(20);

        for(SpotBugsWarning warning : warnings) {
            assertThat(warning.getFqn()).isEqualTo("testSBProject01.App2"); //念のため確認
        }
        

    }


}