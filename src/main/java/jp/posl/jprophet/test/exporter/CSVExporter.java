package jp.posl.jprophet.test.exporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.spectrumbased.coverage.TestResults;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.evaluator.PatchEvaluator;

public class CSVExporter {
    
    private final String resultDir;
    private final String resultFilePath;
    private List<String> recodes = new ArrayList<String>();

    public CSVExporter(String resultDir, String resultFilePath) {
        this.resultDir = resultDir;
        this.resultFilePath = resultFilePath;
    }

    public void initRecodes(List<String> recodes) {
        this.recodes = recodes;
    }

    public void addRecode(String recode) {
        this.recodes.add(recode);
    }

    public void export() {
        final File resultDirFile = new File(resultDir);
        if(!resultDirFile.exists()) {
            resultDirFile.mkdir();
        }

        final File outputFile = new File(resultDir + resultFilePath);
        if(outputFile.exists()) {
            outputFile.delete();
        }
        
        try {
            FileUtils.write(outputFile, String.join("\n", this.recodes), "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void exportAllPatch(List<PatchCandidate> candidates) {
        final String field = "ID,filePath,line,operation";
        this.recodes.add(field);
        for (PatchCandidate candidate : candidates) {
            final String recode = candidate.getId() + "," + candidate.getFilePath() + "," + candidate.getLineNumber().get() + "," + candidate.getAppliedOperation();
            this.recodes.add(recode);
        }
        this.export();
    }

    public void exportAllPatchScore(List<PatchEvaluator.PatchForEval> candidates) {
        final String field = "ID,filePath,line,operation,score";
        this.recodes.add(field);
        for (PatchEvaluator.PatchForEval candidate : candidates) {
            final String recode = candidate.patch.getId() + "," + candidate.patch.getFilePath() + "," + candidate.patch.getLineNumber().get() + "," + candidate.patch.getAppliedOperation() + "," + candidate.getScore().get();
            this.recodes.add(recode);
        }
        this.export();
    }

    public void exportSuspiciousness(List<Suspiciousness> suspiciousnesses) {
        final String field = "fqn,line,suspiciousness";
        this.recodes.add(field);
        for (Suspiciousness suspiciousness : suspiciousnesses) {
            final String recode = suspiciousness.getFQN() + "," + suspiciousness.getLineNumber() + "," + suspiciousness.getValue();
            this.recodes.add(recode);
        }
        this.export();
    }

    public void exportFailedTest(TestResults testResults) {
        List<String> ft = testResults.getTestResults().stream()
            .filter(s -> s.wasFailed() == true)
            .map(s -> s.getMethodName())
            .collect(Collectors.toList());

        this.recodes.addAll(ft);
        this.export();
        

        /*
        List<TestResult> coft = testResults.getTestResults().stream()
            .filter(s -> s.wasFailed() == true)
            .filter(s -> s.getCoverages().size() != 0)
            .collect(Collectors.toList());
        */
    }

}
