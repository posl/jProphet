package jp.posl.jprophet.learning;

import java.util.List;

import org.junit.Test;

import jp.posl.jprophet.learning.Learner.TrainingCase;
import jp.posl.jprophet.operation.*;

public class LearnerTest {
    @Test public void test() {
        final String dirPath = "src/test/resources/testTrainingCases";
        final String originalDirName = "original";
        final String fixedDirName = "fixed";
        final List<AstOperation> operations = List.of(
            new CondRefinementOperation(),
            new CondIntroductionOperation(), 
            new CtrlFlowIntroductionOperation(), 
            new InsertInitOperation(), 
            new VariableReplacementOperation(),
            new CopyReplaceOperation()
        );
        final TrainingConfig config = new TrainingConfig(dirPath, originalDirName, fixedDirName, operations);
        final Learner learner = new Learner();
        final List<TrainingCase> cases = learner.generateTrainingCase(config);
        return;
    }
    
}