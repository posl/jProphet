package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.fl.spectrumbased.SpectrumBasedFaultLocalization;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.manualspecification.ManualSpecification;
import jp.posl.jprophet.fl.manualspecification.strategy.*;
import jp.posl.jprophet.fl.spectrumbased.strategy.*;
import jp.posl.jprophet.operation.*;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.executor.TestExecutor;
import jp.posl.jprophet.test.executor.UnitTestExecutor;
import jp.posl.jprophet.test.result.TestExecutorResult;
import jp.posl.jprophet.test.result.TestResultStore;
import jp.posl.jprophet.test.exporter.TestResultExporter;
import jp.posl.jprophet.test.exporter.CSVTestResultExporter;
import jp.posl.jprophet.test.exporter.PatchDiffExporter;

public class JProphetMain {
    public static void main(String[] args) {
        final String buildDir = "./tmp/"; 
        final String resultDir = "./result/"; 
        String projectPath = "src/test/resources/FizzBuzz01";
        if(args.length > 0){
            projectPath = args[0];
        }
        final Project                  project                  = new GradleProject(projectPath);
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project);
        final Coefficient              coefficient              = new Jaccard();
        final List<SpecificationStrategy> specificationStrategyList = new ArrayList<SpecificationStrategy>();

        //specificationStrategyList.add(new SpecificOneLineBug("FizzBuzz01.FizzBuzz", 11, 1));
        //specificationStrategyList.add(new SpecificBugsByRange("FizzBuzz01.FizzBuzz", 4, 12, 1));
        //specificationStrategyList.add(new SpecificBugsWavy("FizzBuzz01.FizzBuzz", 9, 1, 2, 0.2));
        //specificationStrategyList.add(new SpecificBugsWavy("FizzBuzz01.FizzBuzz", 10, 1, 2, 0.2));
        //specificationStrategyList.add(new SpecificBugsWavy("FizzBuzz01.FizzBuzz", 11, 1, 2, 0.2));
        //specificationStrategyList.add(new SpecificBugsWavy("FizzBuzz01.FizzBuzz", 12, 1, 2, 0.2));
        //specificationStrategyList.add(new SpecificBugsWavy("FizzBuzz01.FizzBuzz", 13, 1, 2, 0.2));

        specificationStrategyList.add(new SpecificOneLineBug("QuickSort.QSort", 21, 1));

        //final FaultLocalization        faultLocalization        = new ManualSpecification(config, specificationStrategyList);
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
            new InsertInitOperation(), 
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
        final List<Suspiciousness> suspiciousnesses = faultLocalization.exec();

        //FL+manualするとき上を消して下にする
        /*
        final List<Suspiciousness> suspiciousnesses1 = faultLocalization.exec();
        final List<Suspiciousness> suspiciousnesses2 = new SpectrumBasedFaultLocalization(config, new Jaccard()).exec();
        List<Suspiciousness> suspiciousnesses = new ArrayList<Suspiciousness>();
        //suspiciousnesses1とsuspiciousnesses2の要素を比較して大きい方をとる処理
        for (int i = 0; i < suspiciousnesses1.size(); i++) {
            try {
                if (suspiciousnesses1.get(i).getValue() < suspiciousnesses2.get(i).getValue()){
                    suspiciousnesses.add(suspiciousnesses2.get(i));
                } else {
                    suspiciousnesses.add(suspiciousnesses1.get(i));
                }
            } catch (Exception e) {
                suspiciousnesses.add(suspiciousnesses1.get(i));
            }
        }
        */
        

        
        // 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
        final List<PatchCandidate> patchCandidates = patchCandidateGenerator.exec(config.getTargetProject(), operations);
        
        // 学習モデルやフォルトローカライゼーションのスコアによってソート
        patchEvaluator.descendingSortBySuspiciousness(patchCandidates, suspiciousnesses);

        //パッチを順位をcsvに書き出し
        
        final String resultDir = "./result/";
        final String resultFilePath = "rank.csv";
        final File resultDirFile = new File(resultDir);
        if(!resultDirFile.exists()) {
            resultDirFile.mkdir();
        }
        final File outputFile = new File(resultDir + resultFilePath);
        if(outputFile.exists()) {
            outputFile.delete();
        }
        final List<String> recodes = new ArrayList<String>();
        final String field = "ID,filePath,line,operation,isSuccess";
        recodes.add(field);
        for (PatchCandidate patchCandidate : patchCandidates) {
            final String patchLine = patchCandidate.getId() + "," + patchCandidate.getFilePath() + "," + patchCandidate.getLineNumber().get() + "," + patchCandidate.getAppliedOperation();
            Project patchedProject = patchedProjectGenerator.applyPatch(patchCandidate);
            //final TestExecutorResult result = testExecutor.exec(new RepairConfiguration(config, patchedProject));
            //final String resultLine = "," + result.canEndRepair();
            //final String recode = patchLine + resultLine;
            final String recode = patchLine;
            recodes.add(recode);
        }
        try {
            FileUtils.write(outputFile, String.join("\n", recodes), "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        

        //疑惑値のリストをcsvに
        final String resultDir2 = "./result/";
        final String resultFilePath2 = "suspiciousness.csv";
        final File resultDirFile2 = new File(resultDir2);
        if(!resultDirFile2.exists()) {
            resultDirFile2.mkdir();
        }
        final File outputFile2 = new File(resultDir2 + resultFilePath2);
        if(outputFile2.exists()) {
            outputFile2.delete();
        }
        final List<String> recodes2 = new ArrayList<String>();
        final String field2 = "FQN,Line,Value";
        recodes2.add(field2);
        for (Suspiciousness susp : suspiciousnesses) {
            final String recode = susp.getFQN() + "," + susp.getLineNumber() + "," + susp.getValue();
            recodes2.add(recode);
        }
        try {
            FileUtils.write(outputFile2, String.join("\n", recodes2), "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        
        // 修正パッチ候補ごとにテスト実行
        for(PatchCandidate patchCandidate: patchCandidates) {
            System.out.println(patchCandidate);
            Project patchedProject = patchedProjectGenerator.applyPatch(patchCandidate);
            final TestExecutorResult result = testExecutor.exec(new RepairConfiguration(config, patchedProject));
            testResultStore.addTestResults(result.getTestResults(), patchCandidate);
            if(result.canEndRepair()) {
                testResultExporters.stream().forEach(exporter -> exporter.export(testResultStore));
                return true;
            }
        }
        testResultExporters.stream().forEach(exporter -> exporter.export(testResultStore));
        return false;
    }
}
