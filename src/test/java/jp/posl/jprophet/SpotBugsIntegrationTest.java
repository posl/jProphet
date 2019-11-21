package jp.posl.jprophet;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.executor.SpotBugsTestExecutor;
import jp.posl.jprophet.test.executor.TestExecutor;
import jp.posl.jprophet.test.exporter.CSVTestResultExporter;
import jp.posl.jprophet.test.exporter.TestResultExporter;
import jp.posl.jprophet.test.result.TestResultStore;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.spotbugsbased.SpotBugsBasedFaultLocalization;
import jp.posl.jprophet.operation.*;
import jp.posl.jprophet.project.GradleProject;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * SpotBugsのワーニング解消機能の結合テスト
 */
public class SpotBugsIntegrationTest {

    private final String buildDir = "./tmp/"; 
    private final String resultDir = "./result/"; 
    private List<AstOperation> operations;

    @Before
    public void SetUpClasses() {



        this.operations = new ArrayList<AstOperation>(Arrays.asList(
            new CondRefinementOperation(),
            new CondIntroductionOperation(), 
            new CtrlFlowIntroductionOperation(), 
            new InsertInitOperation(), 
            new VariableReplacementOperation(),
            new CopyReplaceOperation()
        ));

    }


    @Test
    public void testForRoughConstantValue() {

    }


    public void runjProphet(String projectPath) {
        final Project                  project                  = new GradleProject(projectPath);
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project);
        final FaultLocalization        faultLocalization        = new SpotBugsBasedFaultLocalization(config);
        final PatchCandidateGenerator  patchCandidateGenerator  = new PatchCandidateGenerator();
        final PlausibilityAnalyzer     plausibilityAnalyzer     = new PlausibilityAnalyzer();  
        final PatchEvaluator           patchEvaluator           = new PatchEvaluator();
        final StagedCondGenerator      stagedCondGenerator      = new StagedCondGenerator();
        final TestExecutor             testExecutor             = new SpotBugsTestExecutor("");
        final FixedProjectGenerator    fixedProjectGenerator    = new FixedProjectGenerator();
        final TestResultStore          testResultStore          = new TestResultStore();
        final TestResultExporter       testResultExporter       = new CSVTestResultExporter(resultDir);
    }



}