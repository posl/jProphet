package jp.posl.jprophet.test.exporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import jp.posl.jprophet.SpotBugsIntegrationTest;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.result.TestResult;
import jp.posl.jprophet.test.result.TestResultStore;

public class PatchDiffExporter implements TestResultExporter {

    private final String resultDir;
    private final String resultFileName = "diff";

    public PatchDiffExporter(String resultDir) {
        this.resultDir = resultDir;
    }

    @Override
    public void export(TestResultStore resultStore) {
        final File resultDirFile = new File(resultDir);
        if(!resultDirFile.exists()) {
            resultDirFile.mkdir();
        }

        final File outputFile = new File(resultDir + resultFileName + SpotBugsIntegrationTest.id + ".txt");
        if(outputFile.exists()) {
            outputFile.delete();
        }

        final List<Map.Entry<TestResult, PatchCandidate>> entryList = new ArrayList<>(resultStore.getPatchResults().entrySet());

        Collections.sort(   //ID順に並び替え
            entryList,
            new Comparator<Map.Entry<TestResult, PatchCandidate>>() {
                @Override
                public int compare(Map.Entry<TestResult, PatchCandidate> obj1, Map.Entry<TestResult, PatchCandidate> obj2) {
                    return obj1.getValue().getId() - obj2.getValue().getId();
                }
            }
        );

        final List<String> diffList = entryList.stream().map(e -> e.getValue().toString()).collect(Collectors.toList());

        try {
            FileUtils.write(outputFile, String.join("\n\n", diffList), "utf-8", true);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }



    
}