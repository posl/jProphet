package jp.posl.jprophet;

import jp.posl.jprophet.ProjectBuilder;
import org.junit.Test;                                                                                                                                                                  
import static org.assertj.core.api.Assertions.*;

import java.io.File;


public class ProjectBuilderTest {

    @Test public void testForBuild() {
        File outDir = new File("./tmp/");
        ProjectConfiguration project = new ProjectConfiguration("src/test/resources/testGradleProject01", outDir.getPath());
        ProjectBuilder builder = new ProjectBuilder();
        boolean isSuccess = builder.build(project);
        assertThat(isSuccess).isTrue();
        assertThat(new File("./tmp/testGradleProject01").exists()).isTrue();
        deleteDirectory(outDir);
    }

    @Test public void testForBuildPath(){
        File outDir = new File("./tmp/");
        ProjectConfiguration project = new ProjectConfiguration("src/test/resources/testGradleProject01", outDir.getPath());
        ProjectBuilder builder = new ProjectBuilder();
        builder.build(project);
        assertThat(new File("./tmp/testGradleProject01").exists()).isTrue();
        deleteDirectory(outDir);
    }

    private void deleteDirectory(File dir){
        for(File file : dir.listFiles()){
            if(file.isFile())
                file.delete();
            else
                deleteDirectory(file);
        }

        dir.delete();
    }
}