package jp.posl.jprophet.test.exporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.patch.PatchCandidate;

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

    public void exportSuspiciousness(List<Suspiciousness> suspiciousnesses) {
        final String field = "fqn,line,suspiciousness";
        this.recodes.add(field);
        for (Suspiciousness suspiciousness : suspiciousnesses) {
            final String recode = suspiciousness.getFQN() + "," + suspiciousness.getLineNumber() + "," + suspiciousness.getValue();
            this.recodes.add(recode);
        }
        this.export();
    }

}
