package jp.posl.jprophet.learning;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import jp.posl.jprophet.learning.TrainingCaseGenerator.TrainingCase;
import jp.posl.jprophet.operation.*;

public class TrainingCaseExporterTest {
    /**
     * 特徴ベクトルファイルが生成されるかどうかテスト
     */
    @Test public void testExport() {
        final String pathesDirPath = "src/test/resources/testPatches";
        final String originalDirName = "original";
        final String fixedDirName = "fixed";
        final String outputPathName = "result/feature-vector.json";
        final List<AstOperation> operations = List.of(new CondRefinementOperation(), new CondIntroductionOperation(),
                new CtrlFlowIntroductionOperation(), new InsertInitOperation(), new VariableReplacementOperation(),
                new CopyReplaceOperation());
        final TrainingCaseConfig config = new TrainingCaseConfig(pathesDirPath, originalDirName, fixedDirName,
                outputPathName, operations);
        final TrainingCaseExporter exporter = new TrainingCaseExporter();
        final TrainingCaseGenerator learner = new TrainingCaseGenerator();
        final List<TrainingCase> cases = learner.generateTrainingCase(config);
        final Path outputPath = Paths.get(outputPathName);
        try {
            if(Files.exists(outputPath)) {
                FileUtils.forceDelete(new File(outputPathName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        exporter.export(config, cases);

        assertThat(Files.exists(outputPath)).isTrue();
    }
    
}