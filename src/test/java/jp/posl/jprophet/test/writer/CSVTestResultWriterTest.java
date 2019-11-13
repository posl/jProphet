package jp.posl.jprophet.test.writer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.patch.DefaultPatchCandidate;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.spotbugs.SpotBugsWarning;
import jp.posl.jprophet.test.result.SpotBugsTestResult;
import jp.posl.jprophet.test.result.TestResult;


public class CSVTestResultWriterTest {

    private TestResultWriter writer;

    @Before
    public void setUpResult() {
        this.writer = new CSVTestResultWriter();
        final List<TestResult> testResults = List.of(
            new SpotBugsTestResult(true, new SpotBugsWarning("NM_METHOD_NAMING_CONVENTION", "hoge.java", 1, 10), 1),
            new SpotBugsTestResult(true, new SpotBugsWarning("NP_ALWAYS_NULL", "hoge.java", 5, 8), 0),
            new SpotBugsTestResult(true, new SpotBugsWarning("NM_METHOD_NAMING_CONVENTION", "huga.java", 4, 7), 0)
        );

        //final List<PatchCandidate> patchCandidates = List.of(
        //    new DefaultPatchCandidate(repairUnit, fixedFilePath, fixedFileFQN)
        //);
        


    }


    @Test
    public void testForWrite() {



    }

}