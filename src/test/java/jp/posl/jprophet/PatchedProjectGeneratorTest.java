package jp.posl.jprophet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;
import jp.posl.jprophet.project.GradleProject;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import org.apache.commons.io.FileUtils;

public class PatchedProjectGeneratorTest{
    private String projectPath;
    private String targetFilePath;
    private String targetFileFqn;
    private List<String> projectFilePaths; 
    private String buildDir;
    private String resultDir;
    private RepairConfiguration config;
    private PatchedProjectGenerator patchedProjectGenerator;
    private String parameterPath;

    @Before public void setUp(){
        this.projectPath = "src/test/resources/testGradleProject01";
        this.targetFilePath = "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java";
        this.targetFileFqn = "testGradleProject01.App";
        this.projectFilePaths = List.of(
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
        );
        this.buildDir = "./temp/";
        this.resultDir = "./result/";
        this.parameterPath = "parameters/para.csv";
        this.config = new RepairConfiguration(this.buildDir, this.resultDir, new GradleProject(this.projectPath), this.parameterPath);
        this.patchedProjectGenerator = new PatchedProjectGenerator(this.config);
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(Paths.get(this.targetFilePath));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        Node targetNodeBeforeFix = compilationUnit.findRootNode().getChildNodes().get(1).getChildNodes().get(1).getChildNodes().get(2).getChildNodes().get(0);
        Node targetNodeAfterFix = NodeUtility.initTokenRange(new ExpressionStmt(new MethodCallExpr("hoge"))).get();
        PatchCandidate patchCandidate = new PatchCandidate(new OperationDiff(ModifyType.INSERT, targetNodeBeforeFix, targetNodeAfterFix), this.targetFilePath, this.targetFileFqn, AstOperation.class, 1);
        this.patchedProjectGenerator.applyPatch(patchCandidate);
       
    }

    
    /**
     * ファイルが全て生成されているかテスト
     * パッチが適用されているかは見ない
     */
    @Test public void testIfFilesIsGenerated(){
        for(String projectFilePath: this.projectFilePaths){
            assertThat(Files.exists(Paths.get(this.resultDir + projectFilePath))).isTrue();
        }
    }

    /**
     * 生成されたプロジェクトにてパッチが適用されているかテスト
     */
    @Test public void testIfGeneratedProjectIsPatched(){
        List<String> lines;
        try {
            lines = FileUtils.readLines(new File(resultDir + projectFilePaths.get(0)), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        final int modifiedLineNumber = 7;
        assertThat(lines.get(modifiedLineNumber)).contains("hoge();");
    }


    @After public void cleanUp(){
        try {
            FileUtils.deleteDirectory(new File(this.resultDir));
            FileUtils.deleteDirectory(new File(this.resultDir));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}