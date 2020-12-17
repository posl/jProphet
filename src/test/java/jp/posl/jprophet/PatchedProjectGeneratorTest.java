package jp.posl.jprophet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;

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
    // private String projectPath;
    // private String targetFilePath;
    // private String targetFileFqn;
    // private List<String> projectFilePaths; 
    // private String buildDir;
    private String resultDir = "./result/";
    // private RepairConfiguration config;
    // private PatchedProjectGenerator patchedProjectGenerator;
    // private String parameterPath;

    @Before public void setUp(){
        // this.projectPath = "src/test/resources/testGradleProject01";
        // this.targetFilePath = "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java";
        // this.targetFileFqn = "testGradleProject01.App";
        // this.projectFilePaths = List.of(
        //     "testGradleProject01/src/main/java/testGradleProject01/App.java",
        //     "testGradleProject01/src/main/java/testGradleProject01/App2.java",
        //     "testGradleProject01/src/test/java/testGradleProject01/AppTest.java",
        //     "testGradleProject01/src/test/java/testGradleProject01/App2Test.java",
        //     "testGradleProject01/gradle/wrapper/gradle-wrapper.jar",
        //     "testGradleProject01/gradle/wrapper/gradle-wrapper.properties",
        //     "testGradleProject01/.gitignore",
        //     "testGradleProject01/build.gradle",
        //     "testGradleProject01/gradlew",
        //     "testGradleProject01/gradlew.bat",
        //     "testGradleProject01/settings.gradle"
        // );
        // this.buildDir = "./temp/";
        // this.resultDir = "./result/";
        // this.parameterPath = "parameters/para.csv";
        // this.config = new RepairConfiguration(this.buildDir, this.resultDir, new GradleProject(this.projectPath), this.parameterPath);
        // this.patchedProjectGenerator = new PatchedProjectGenerator(this.config);
        // CompilationUnit compilationUnit;
        // try {
        //     compilationUnit = JavaParser.parse(Paths.get(this.targetFilePath));
        // }
        // catch (IOException e){
        //     e.printStackTrace();
        //     fail(e.getMessage());
        //     return;
        // }
        // Node targetNodeBeforeFix = compilationUnit.findRootNode().getChildNodes().get(1).getChildNodes().get(1).getChildNodes().get(2).getChildNodes().get(0);
        // Node targetNodeAfterFix = NodeUtility.initTokenRange(new ExpressionStmt(new MethodCallExpr("hoge"))).get();
        // PatchCandidate patchCandidate = new PatchCandidate(new OperationDiff(ModifyType.INSERT, targetNodeBeforeFix, targetNodeAfterFix), this.targetFilePath, this.targetFileFqn, AstOperation.class, 1);
        // this.patchedProjectGenerator.applyPatch(patchCandidate);
    }


    /**
     * ファイルが全て生成されているかテスト
     * パッチが適用されているかは見ない
     */
    @Test public void testIfFilesIsGenerated(){
        final String projectPath = "src/test/resources/testGradleProject01";
        final String targetFilePath = "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java";
        final String targetFileFqn = "testGradleProject01.App";
        final List<String> projectFilePaths = List.of(
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
        final String buildDir = "./temp/";
        final String parameterPath = "parameters/para.csv";
        final RepairConfiguration config = new RepairConfiguration(buildDir, resultDir, new GradleProject(projectPath), parameterPath);
        final PatchedProjectGenerator patchedProjectGenerator = new PatchedProjectGenerator(config);
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(Paths.get(targetFilePath));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        final Node targetNodeBeforeFix = compilationUnit.findRootNode().getChildNodes().get(1).getChildNodes().get(1).getChildNodes().get(2).getChildNodes().get(0);
        final Node targetNodeAfterFix = NodeUtility.initTokenRange(new ExpressionStmt(new MethodCallExpr("hoge"))).get();
        final PatchCandidate patchCandidate = new PatchCandidate(new OperationDiff(ModifyType.INSERT, targetNodeBeforeFix, targetNodeAfterFix), targetFilePath, targetFileFqn, AstOperation.class, 1);
        patchedProjectGenerator.applyPatch(patchCandidate);

        for (String projectFilePath: projectFilePaths){
            assertThat(Files.exists(Paths.get(this.resultDir + projectFilePath))).isTrue();
        }
    }

    /**
     * 生成されたプロジェクトにてパッチが適用されているかテスト
     */
    @Test public void testIfGeneratedProjectIsPatched(){
        final String projectPath = "src/test/resources/testGradleProject01";
        final String targetFilePath = "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java";
        final String targetFileFqn = "testGradleProject01.App";
        final List<String> projectFilePaths = List.of(
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
        final String buildDir = "./temp/";
        final String parameterPath = "parameters/para.csv";
        final RepairConfiguration config = new RepairConfiguration(buildDir, this.resultDir, new GradleProject(projectPath), parameterPath);
        final PatchedProjectGenerator patchedProjectGenerator = new PatchedProjectGenerator(config);
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(Paths.get(targetFilePath));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        final Node targetNodeBeforeFix = compilationUnit.findRootNode().getChildNodes().get(1).getChildNodes().get(1).getChildNodes().get(2).getChildNodes().get(0);
        final Node targetNodeAfterFix = NodeUtility.initTokenRange(new ExpressionStmt(new MethodCallExpr("hoge"))).get();
        final PatchCandidate patchCandidate = new PatchCandidate(new OperationDiff(ModifyType.INSERT, targetNodeBeforeFix, targetNodeAfterFix), targetFilePath, targetFileFqn, AstOperation.class, 1);
        patchedProjectGenerator.applyPatch(patchCandidate);

        List<String> lines;
        try {
            lines = FileUtils.readLines(new File(this.resultDir + projectFilePaths.get(0)), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        final int modifiedLineNumber = 7;
        assertThat(lines.get(modifiedLineNumber)).contains("hoge();");
    }

    @Test public void testMultiApply() {
        final String projectPath = "src/test/resources/testGradleProject01";
        final String targetFilePath = "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java";
        final String targetFileFqn = "testGradleProject01.App";
        final String buildDir = "./temp/";
        final String parameterPath = "parameters/para.csv";
        final RepairConfiguration config = new RepairConfiguration(buildDir, this.resultDir, new GradleProject(projectPath), parameterPath);
        final PatchedProjectGenerator patchedProjectGenerator = new PatchedProjectGenerator(config);
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(Paths.get(targetFilePath));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        final Node insertBeforeThisNode = compilationUnit.findRootNode().getChildNodes().get(1).getChildNodes().get(1).getChildNodes().get(2).getChildNodes().get(0);
        final Node nodeToInsert1 = NodeUtility.initTokenRange(new ExpressionStmt(new MethodCallExpr("hoge1"))).get();
        final Node nodeToInsert2 = NodeUtility.initTokenRange(new ExpressionStmt(new MethodCallExpr("hoge2"))).get();
        final Node nodeToInsert3 = NodeUtility.initTokenRange(new ExpressionStmt(new MethodCallExpr("hoge3"))).get();
        final PatchCandidate patchCandidate1 = new PatchCandidate(new OperationDiff(ModifyType.INSERT, insertBeforeThisNode, nodeToInsert1), targetFilePath, targetFileFqn, AstOperation.class, 1);
        final PatchCandidate patchCandidate2 = new PatchCandidate(new OperationDiff(ModifyType.INSERT, insertBeforeThisNode, nodeToInsert2), targetFilePath, targetFileFqn, AstOperation.class, 1);
        final PatchCandidate patchCandidate3 = new PatchCandidate(new OperationDiff(ModifyType.INSERT, insertBeforeThisNode, nodeToInsert3), targetFilePath, targetFileFqn, AstOperation.class, 1);
        final List<PatchCandidate> patchCandidates = List.of(patchCandidate1, patchCandidate2, patchCandidate3);
        final Project project = patchedProjectGenerator.applyMultiPatch(patchCandidates);

    }

    @Test public void testMultiApplyForMultiFiles() {
        final String projectPath = "src/test/resources/testGradleProject01";
        final String targetFilePath1 = "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App.java";
        final String targetFileFqn1 = "testGradleProject01.App";
        final String targetFilePath2 = "src/test/resources/testGradleProject01/src/main/java/testGradleProject01/App2.java";
        final String targetFileFqn2 = "testGradleProject01.App2";
        final String buildDir = "./temp/";
        final String parameterPath = "parameters/para.csv";
        final RepairConfiguration config = new RepairConfiguration(buildDir, this.resultDir, new GradleProject(projectPath), parameterPath);
        final PatchedProjectGenerator patchedProjectGenerator = new PatchedProjectGenerator(config);
        CompilationUnit compilationUnit1;
        CompilationUnit compilationUnit2;
        try {
            compilationUnit1 = JavaParser.parse(Paths.get(targetFilePath1));
            compilationUnit2 = JavaParser.parse(Paths.get(targetFilePath2));
        }
        catch (IOException e){
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }
        final Node insertBeforeThisNode1 = compilationUnit1.findRootNode().getChildNodes().get(1).getChildNodes().get(1).getChildNodes().get(2).getChildNodes().get(0);
        final Node nodeToInsert1 = NodeUtility.initTokenRange(new ExpressionStmt(new MethodCallExpr("hoge1"))).get();
        final Node insertBeforeThisNode2 = compilationUnit2.findRootNode().getChildNodes().get(1).getChildNodes().get(1).getChildNodes().get(2).getChildNodes().get(0);
        final Node nodeToInsert2 = NodeUtility.initTokenRange(new ExpressionStmt(new MethodCallExpr("hoge2"))).get();

        final PatchCandidate patchCandidate1 = new PatchCandidate(new OperationDiff(ModifyType.INSERT, insertBeforeThisNode1, nodeToInsert1), targetFilePath1, targetFileFqn1, AstOperation.class, 1);
        final PatchCandidate patchCandidate2 = new PatchCandidate(new OperationDiff(ModifyType.INSERT, insertBeforeThisNode2, nodeToInsert2), targetFilePath2, targetFileFqn2, AstOperation.class, 1);

        final List<PatchCandidate> patchCandidates = List.of(patchCandidate1, patchCandidate2);
        final Project project = patchedProjectGenerator.applyMultiPatch(patchCandidates);


    }

    @Test public void test() {
        final String targetSource = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        boolean flag = false;\n")
            .append("        if (hoge) {\n")
            .append("            fa = \"b\";\n")
            .append("        }\n")
            .append("    }\n")
            .append("}\n")
            .toString();

        final CompilationUnit cu = JavaParser.parse(targetSource);
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