package jp.posl.jprophet.test.exporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.result.TestResult;
import jp.posl.jprophet.test.result.TestResultStore;


/**
 * テスト結果と修正パッチを受け取り、CSVファイルとして書き込みを行うクラス
 */
public class CSVTestResultExporter implements TestResultExporter {

    private final String resultDir;
    private final String resultFilePath = "result.csv";


    /**
     * CSVTestResultExporterのコンストラクタ
     */
    public CSVTestResultExporter(String resultDir) {
        this.resultDir = resultDir;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void export(TestResultStore resultStore) {


        final File resultDirFile = new File(resultDir);
        if(!resultDirFile.exists()) {
            resultDirFile.mkdir();
        }

        final File outputFile = new File(resultDir + resultFilePath);
        if(outputFile.exists()) {
            outputFile.delete();
        }
        final List<String> recodes = new ArrayList<String>();

        
        final List<Map.Entry<TestResult, PatchCandidate>> entryList = new ArrayList<>(resultStore.getPatchResults().entrySet());

        entryList.stream().forEach(e -> System.out.println(e.getValue()));
        final String field = "filePath,line,operation," + String.join(",", entryList.get(0).getKey().toStringMap().keySet());
        recodes.add(field);

        for (Map.Entry<TestResult, PatchCandidate> entry : entryList) {
            final TestResult result = entry.getKey();
            final PatchCandidate patch = entry.getValue();
            final String patchLine = patch.getFilePath() + "," + patch.getLineNumber().get() + "," + patch.getAppliedOperation();
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