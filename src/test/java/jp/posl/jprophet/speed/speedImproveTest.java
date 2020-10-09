package jp.posl.jprophet.speed;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.junit.Test;

import jp.posl.jprophet.PatchedProjectGenerator;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.executor.MemoryClassLoader;
import jp.posl.jprophet.test.executor.TestExecutor;
import jp.posl.jprophet.test.executor.UnitTestExecutor;

public class speedImproveTest {
    @Test
    public void testBuild() {
        final String projectPath = "src/test/resources/projectsForSpeedTune/testGradleProjectForSpeed";
        final String targetFilePath = "src/test/resources/projectsForSpeedTune/testGradleProjectForSpeed/src/main/java/testGradleProjectForSpeed/App.java";
        final String buildDir = "./temp/";
        final String resultDir = "./result/";
        final Project originalProject  = new GradleProject(projectPath);
        final RepairConfiguration config = new RepairConfiguration(buildDir, resultDir, originalProject);
        final TestExecutor executor = new UnitTestExecutor();
        final ProjectBuilder builder = new ProjectBuilder();
        for (int i = 0; i < 1000; i++) {
            // executor.exec(config);
            builder.build(config);
        }
    }

    @Test public void testBuildMulti() {
        final String projectPath = "src/test/resources/projectsForSpeedTune/ProjectMulti";
        final String buildDir = "./temp/";
        final String resultDir = "./result/";
        final Project originalProject  = new GradleProject(projectPath);
        final RepairConfiguration config = new RepairConfiguration(buildDir, resultDir, originalProject);
        final UnitTestExecutor executor = new UnitTestExecutor();
        final ProjectBuilder builder = new ProjectBuilder();
        for (int i = 0; i < 500; i++) {
            try {
                builder.build(config);
                // executor.getClassLoader(config.getBuildPath());
                // List<Class<?>> testClasses = executor.loadTestClass(config.getTargetProject());
                // executor.runAllTestClass(testClasses);
                try {
                    final byte[] content = Files.readAllBytes(Paths.get("temp/ProjectMulti/App.class"));
                    short vs1 = 0x2;
                    short vs2 = 0xbb;
                    short vs3 = 0x3;
                    short vs4 = 0xa5;
                    byte b0x2 = (byte) vs1;
                    byte b0xbb = (byte) vs2;
                    byte b0x3 = (byte) vs3;
                    byte b0xa5 = (byte) vs4;
                    for (int j = 0; j < content.length; j++) {
                        if (content[j] == b0x2) {
                            if (content[j + 1] == b0xbb) {
                                content[j] = b0x3;
                                content[j + 1] = b0xa5;
                                break;
                            }
                        }
                    }
                    final StringBuilder sb = new StringBuilder();
                    for (final byte value: content) {
                        sb.append(String.format("%02X", value));
                    }
                    final String str = sb.toString();
                    final int index = str.indexOf("02BB");
                    Files.write(Paths.get("temp/ProjectMulti/App.class"), content);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // executor.getClassLoader(config.getBuildPath());
                // testClasses = executor.loadTestClass(config.getTargetProject());
                // executor.runAllTestClass(testClasses);
            }
            catch (Exception e) {
            // catch (MalformedURLException | ClassNotFoundException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void rewrite() {
    }

    @Test
    public void test() {
        try {
            final byte[] content = Files.readAllBytes(Paths.get("src/test/resources/projectsForSpeedTune/ProjectMulti/src/main/java/ProjectMulti/App.java"));
            short vs1 = 0x2;
            short vs2 = 0xbb;
            short vs3 = 0x3;
            short vs4 = 0xa5;
            byte b0x2 = (byte) vs1;
            byte b0xbb = (byte) vs2;
            byte b0x3 = (byte) vs3;
            byte b0xa5 = (byte) vs4;
            for (int i = 0; i < content.length; i++) {
                if (content[i] == b0x2) {
                    if (content[i + 1] == b0xbb) {
                        content[i] = b0x3;
                        content[i + 1] = b0xa5;
                        break;
                    }
                }
            }
            final StringBuilder sb = new StringBuilder();
            for (final byte value: content) {
                sb.append(String.format("%02X", value));
            }
            final String str = sb.toString();
            final int index = str.indexOf("02BB");
            Files.write(Paths.get("src/test/resources/binary/output/testForJavap.class"), content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
