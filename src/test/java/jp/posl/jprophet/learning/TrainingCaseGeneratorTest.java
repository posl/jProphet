package jp.posl.jprophet.learning;

import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import jp.posl.jprophet.learning.TrainingCaseGenerator.TrainingCase;
import jp.posl.jprophet.operation.*;

public class TrainingCaseGeneratorTest {
    /**
     * トレーニングケースオブジェクトが生成されるかテスト
     */
    @Test public void testGeneration() {
        final String pathesDirPath = "src/test/resources/testPatches";
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
        final TrainingCaseConfig config = new TrainingCaseConfig(pathesDirPath, originalDirName, fixedDirName, null, operations);
        final TrainingCaseGenerator generator = new TrainingCaseGenerator();
        final List<TrainingCase> cases = generator.generateTrainingCase(config);
        assertThat(cases.size()).isEqualTo(4);
        return;
    }
    
}