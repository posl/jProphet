package jp.posl.jprophet;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import org.apache.commons.io.FileUtils;

public class ProgramGeneratorTest{
    @Test public void test(){
        final String projectPath = "src/test/resources/testGradleProject01";
        final String targetFilePath = "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java";
        final List<String> projectFilePaths = new ArrayList<String>(Arrays.asList(
            "testGradleProject01/src/main/java/testGradleProject01/App.java",
            "testGradleProject01/src/main/java/testGradleProject01/App2.java",
            "testGradleProject01/src/test/java/testGradleProject01/AppTest.java",
            "testGradleProject01/src/test/java/testGradleProject01/App2Test.java",
            "gradle/wrapper/gradle-wrapper.jar",
            "gradle/wrapper/gradle-wrapper.properties",
            ".gitignore",
            "build.gradle",
            "gradlew",
            "gradle.bat",
            "settings.gradle"
        ));
        final String outDir = "./output/";
        final ProjectConfiguration project = new ProjectConfiguration(projectPath, outDir);
        ProgramGenerator programGenerator = new ProgramGenerator();
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(Paths.get(targetFilePath));
        }
        catch (IOException e){
            e.printStackTrace();
            return;
        }
        ((ClassOrInterfaceDeclaration)compilationUnit.findRootNode().getChildNodes().get(1)).setModifier(Modifier.STATIC, true);
        RepairCandidate repairCandidate = new ConcreteRepairCandidate(compilationUnit, new ArrayList<String>(Arrays.asList("targetFilePath")));
        programGenerator.applyPatch(project, repairCandidate);

        for(String projectFilePath: projectFilePaths){
            //assertThat(Files.exists(Paths.get(outDir + projectFilePath))).isTrue();
        }
    }
}