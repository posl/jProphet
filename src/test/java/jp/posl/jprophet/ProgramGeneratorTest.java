package jp.posl.jprophet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.fail;

import java.io.File;
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
    private String projectPath;
    private String targetFilePath;
    private List<String> projectFilePaths; 
    private String outDir;
    private ProjectConfiguration project;
    private ProgramGenerator programGenerator;

    @Before public void setUp(){
        this.projectPath = "src/test/resources/testGradleProject01";
        this.targetFilePath = "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java";
        this.projectFilePaths = new ArrayList<String>(Arrays.asList(
            "testGradleProject01/src/main/java/testGradleProject01/App.java",
            "testGradleProject01/src/main/java/testGradleProject01/App2.java",
            "testGradleProject01/src/test/java/testGradleProject01/AppTest.java",
            "testGradleProject01/src/test/java/testGradleProject01/App2Test.java",
            "testGradleProject01/gradle/wrapper/gradle-wrapper.jar",
            "testGradleProject01/gradle/wrapper/gradle-wrapper.properties",
            "testGradleProject01/.gitignore",
            "testGradleProject01/build.gradle",
            "testGradleProject01/gradlew",
            "testGradleProject01/gradlew.bat",
            "testGradleProject01/settings.gradle"
        ));
        this.outDir = "./output/";
        this.project = new ProjectConfiguration(projectPath, outDir);
        this.programGenerator = new ProgramGenerator();
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(Paths.get(this.targetFilePath));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        ((ClassOrInterfaceDeclaration)compilationUnit.findRootNode().getChildNodes().get(1)).setModifier(Modifier.STATIC, true);
        RepairCandidate repairCandidate = new ConcreteRepairCandidate(compilationUnit, this.targetFilePath);
        this.programGenerator.applyPatch(project, repairCandidate);
        
    }

    @Test public void testIfFilesIsGenerated(){
        for(String projectFilePath: this.projectFilePaths){
            assertThat(Files.exists(Paths.get(this.outDir + projectFilePath))).isTrue();
        }
    }

    @Test public void testIfGeneratedProjectIsPatched(){
        List<String> lines;
        try {
            lines = FileUtils.readLines(new File(outDir + projectFilePaths.get(0)), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        final int modifiedLineNumber = 5;
         assertThat(lines.get(modifiedLineNumber)).contains("static");
    }

    @After public void cleanUp(){
        try {
            FileUtils.deleteDirectory(new File(this.outDir));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}