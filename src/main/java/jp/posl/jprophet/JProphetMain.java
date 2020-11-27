package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import jp.posl.jprophet.project.FileLocator;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.MavenProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.fl.spectrumbased.SpectrumBasedFaultLocalization;
import jp.posl.jprophet.fl.spectrumbased.TestCase;
import jp.posl.jprophet.fl.spectrumbased.coverage.CoverageCollector;
import jp.posl.jprophet.evaluator.PatchEvaluator;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.spectrumbased.strategy.*;
import jp.posl.jprophet.operation.*;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.executor.TestExecutor;
import jp.posl.jprophet.test.executor.UnitTestExecutor;
import jp.posl.jprophet.test.result.TestExecutorResult;
import jp.posl.jprophet.test.result.TestResultStore;
import jp.posl.jprophet.trainingcase.TrainingCaseConfig;
import jp.posl.jprophet.trainingcase.TrainingCaseExporter;
import jp.posl.jprophet.trainingcase.TrainingCaseGenerator;
import jp.posl.jprophet.trainingcase.TrainingCaseGenerator.TrainingCase;
import jp.posl.jprophet.test.exporter.TestResultExporter;
import jp.posl.jprophet.test.exporter.CSVTestResultExporter;
import jp.posl.jprophet.test.exporter.PatchDiffExporter;

public class JProphetMain {
    public static void main(String[] args) {
        final String buildDir = "./tmp/"; 
        final String resultDir = "./result/"; 
        final String parameterPath = "parameters/para.csv";
        String projectPath = "src/test/resources/FizzBuzz01";
        if(args.length > 0){
            if (args[0].contains("-t")) {
                final JProphetMain jprophet = new JProphetMain();
                jprophet.genTrainingCase();
                return;
            }
            else {
                projectPath = args[0];
            }
        }
        final Project                  project                  = new GradleProject(projectPath);
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project, parameterPath);
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

        final List<AstOperation> operations = new ArrayList<AstOperation>(Arrays.asList(
            new CondRefinementOperation(),
            new CondIntroductionOperation(),
            new CtrlFlowIntroductionOperation(),
            new MethodReplacementOperation(),
            new VariableReplacementOperation(),
            new CopyReplaceOperation(),
            new MethodReplacementOperation()
        ));


        final JProphetMain jprophet = new JProphetMain();
        final boolean isRepairSuccess = jprophet.run(config, faultLocalization, patchCandidateGenerator, operations, patchEvaluator, testExecutor, patchedProjectGenerator, testResultStore, testResultExporters);
        try {
            FileUtils.deleteDirectory(new File(buildDir));
            if(!isRepairSuccess){
                FileUtils.deleteDirectory(new File(config.getFixedProjectDirPath() + FilenameUtils.getBaseName(project.getRootPath()))); //失敗した場合でもログは残す
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean run(RepairConfiguration config, FaultLocalization faultLocalization, PatchCandidateGenerator patchCandidateGenerator,
            List<AstOperation> operations, PatchEvaluator patchEvaluator, TestExecutor testExecutor,
            PatchedProjectGenerator patchedProjectGenerator, TestResultStore testResultStore, List<TestResultExporter> testResultExporters
            ) {
        // フォルトローカライゼーション
        final List<Suspiciousness> suspiciousenesses = faultLocalization.exec();

        final Map<FileLocator, CompilationUnit> targetCuMap = new AstGenerator().exec(suspiciousenesses, config.getTargetProject().getSrcFileLocators());
        
        
        // 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
        final List<PatchCandidate> patchCandidates = patchCandidateGenerator.exec(operations, suspiciousenesses, targetCuMap);
        
        // 学習モデルやフォルトローカライゼーションのスコアによってソート
        final List<PatchCandidate> sortedCandidates = patchEvaluator.sort(patchCandidates, suspiciousenesses, config);
        
        final List<TestCase> testCases = new CoverageCollector(config.getBuildPath()).getTestCases(config);
        
        // 修正パッチ候補ごとにテスト実行
        for(PatchCandidate patchCandidate: sortedCandidates) {
            Project patchedProject = patchedProjectGenerator.applyPatch(patchCandidate);
            //final TestExecutorResult result = testExecutor.exec(new RepairConfiguration(config, patchedProject));
            final TestExecutorResult result = testExecutor.exec(new RepairConfiguration(config, patchedProject), testCases);
            testResultStore.addTestResults(result.getTestResults(), patchCandidate);
            if(result.canEndRepair()) {
                testResultExporters.stream().forEach(exporter -> exporter.export(testResultStore));
                return true;
            }
        }
        testResultExporters.stream().forEach(exporter -> exporter.export(testResultStore));
        return false;
    }

    /**
     * 修正パッチから特徴抽出を行い，特徴ベクトルを出力する
     * 学習時に利用されるモード
     */
    public void genTrainingCase() {
        final String pathesDirPath = "result/patches";
        final String originalDirName = "original";
        final String fixedDirName = "fixed";
        final String exportPathName = "result/feature-vector.json";
        final List<AstOperation> operations = List.of(
            new CondRefinementOperation(),
            new CondIntroductionOperation(),
            new CtrlFlowIntroductionOperation(),
            new InsertInitOperation(),
            new VariableReplacementOperation(),
            new CopyReplaceOperation()
        );
        final TrainingCaseConfig config = new TrainingCaseConfig(pathesDirPath, originalDirName, fixedDirName,
                exportPathName, operations);
        final TrainingCaseExporter exporter = new TrainingCaseExporter();
        final TrainingCaseGenerator learner = new TrainingCaseGenerator();
        final List<TrainingCase> cases = learner.generate(config);
        final Path outputPath = Paths.get(exportPathName);
        try {
            if(Files.exists(outputPath)) {
                FileUtils.forceDelete(new File(exportPathName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        exporter.export(exportPathName, cases);
    }
}
