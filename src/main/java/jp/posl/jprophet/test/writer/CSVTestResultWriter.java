package jp.posl.jprophet.test.writer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.result.TestResult;

public class CSVTestResultWriter implements TestResultWriter {

    private final HashMap<TestResult, PatchCandidate> patchResults;
    private final String resultFilePath = "./result.csv";

    public CSVTestResultWriter() {
        this.patchResults = new HashMap<TestResult, PatchCandidate>();
    }

    @Override
    public void addTestResult(List<TestResult> testResults, PatchCandidate patch) {
        for(TestResult testResult : testResults) {
            patchResults.put(testResult, patch);
        } 
    }

    @Override
    public void write() {

        final File outputFile = new File(resultFilePath);
        if(outputFile.exists()) {
            System.out.println(outputFile.delete());
            
        }
        final List<String> recodes = new ArrayList<String>();

        
        final List<Map.Entry<TestResult, PatchCandidate>> entryList = new ArrayList<>(patchResults.entrySet());
        
        final String field = "filePath,Line," + String.join(",", entryList.get(0).getKey().toStringMap().keySet());
        recodes.add(field);

        for (Map.Entry<TestResult, PatchCandidate> entry : entryList) {
            final TestResult result = entry.getKey();
            final PatchCandidate patch = entry.getValue();
            final String patchLine = patch.getFilePath() + "," + patch.getLineNumber().get();
            final String resultLine = String.join(",", result.toStringMap().values());
            final String recode = patchLine + "," + resultLine;
            recodes.add(recode);
        }

        
        try {
            FileUtils.write(outputFile, String.join("\n", recodes), "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        
    }

}