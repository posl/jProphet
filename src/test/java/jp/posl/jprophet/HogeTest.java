package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.evaluator.PatchEvaluator;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.spectrumbased.SpectrumBasedFaultLocalization;
import jp.posl.jprophet.fl.spectrumbased.strategy.Coefficient;
import jp.posl.jprophet.fl.spectrumbased.strategy.Jaccard;
import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.MavenProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.executor.TestExecutor;
import jp.posl.jprophet.test.executor.UnitTestExecutor;
import jp.posl.jprophet.test.exporter.CSVTestResultExporter;
import jp.posl.jprophet.test.exporter.PatchDiffExporter;
import jp.posl.jprophet.test.exporter.TestResultExporter;
import jp.posl.jprophet.test.result.TestResultStore;
import jp.posl.jprophet.operation.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

public class HogeTest {
    @Test public void test() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("    private void ma() {\n")
            .append("        Fuga hoge = new Fuga();\n")
            .append("        hoge = \"str\";\n")
            .append("        method();\n")
            .append("        if (hoge) return \"hoge\";\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        final CompilationUnit cu = JavaParser.parse(src);
        final MethodCallExpr m = cu.findFirst(MethodCallExpr.class).orElseThrow();
        final Statement s = m.findParent(Statement.class).orElseThrow();
        return;
    }

    @Test public void testThesis() {
        final List<AstOperation> operations = new ArrayList<AstOperation>(Arrays.asList(
            new CondRefinementOperation()
            // new CondIntroductionOperation(),
            // new CtrlFlowIntroductionOperation(),
            // new VariableReplacementOperation()
            // new MethodReplacementOperation(),
            // new CopyReplaceOperation()
        ));
        final String buildDir = "./tmp/";
        final String resultDir = "./result/";
        final String parameterPath = "parameters/para.csv";
        final Project                  project                  = new MavenProject("src/test/resources/math_83_buggy");
        // final Project                  project                  = new MavenProject("src/test/resources/MavenFizzBuzz01");
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project, parameterPath, 1);
        final Coefficient              coefficient              = new Jaccard();
        final FaultLocalization        faultLocalization        = new SpectrumBasedFaultLocalization(config, coefficient);
        final PatchCandidateGenerator  patchCandidateGenerator  = new PatchCandidateGenerator();
        final PatchEvaluator           patchEvaluator           = new PatchEvaluator();
        final TestExecutor             testExecutor             = new UnitTestExecutor();
        final PatchedProjectGenerator  patchedProjectGenerator  = new PatchedProjectGenerator(config);
        final TestResultStore          testResultStore          = new TestResultStore();
        final List<TestResultExporter> testResultExporters = new ArrayList<TestResultExporter>(Arrays.asList(
            new CSVTestResultExporter(resultDir),
            new PatchDiffExporter(resultDir)
        ));
        final JProphetMain jprophet = new JProphetMain();
        long start = System.currentTimeMillis();
        System.out.println(start);
        final boolean isRepairSuccess = jprophet.run(config, faultLocalization, patchCandidateGenerator, operations, patchEvaluator, testExecutor, patchedProjectGenerator, testResultStore, testResultExporters);
        long end = System.currentTimeMillis();
        System.out.println(end);
        long timeElapsed = end - start;
        System.out.println(timeElapsed);
        
        try {
            FileUtils.write(new File("time"), String.valueOf(timeElapsed) + "\n", "utf-8");
            FileUtils.deleteDirectory(new File(buildDir));
            if(!isRepairSuccess){
                FileUtils.deleteDirectory(new File(config.getFixedProjectDirPath() + FilenameUtils.getBaseName(project.getRootPath())));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
}
