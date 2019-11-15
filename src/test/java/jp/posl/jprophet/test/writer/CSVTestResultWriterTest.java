package jp.posl.jprophet.test.writer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.patch.DefaultPatchCandidate;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.spotbugs.SpotBugsWarning;
import jp.posl.jprophet.test.result.SpotBugsTestResult;
import jp.posl.jprophet.test.result.TestResult;


public class CSVTestResultWriterTest {

    private TestResultWriter writer;

    /**
     * テスト結果と修正パッチを準備する
     */
    @Before
    public void setUpResult() {
        this.writer = new CSVTestResultWriter();
        final List<TestResult> testResults01 = List.of(
            new SpotBugsTestResult(false, new SpotBugsWarning("NM_METHOD_NAMING_CONVENTION", "hoge", 1, 10), 1),
            new SpotBugsTestResult(false, new SpotBugsWarning("NP_ALWAYS_NULL", "hoge", 5, 8), 1)
        );

        final List<TestResult> testResults02 = List.of(
            new SpotBugsTestResult(true, new SpotBugsWarning("NM_METHOD_NAMING_CONVENTION", "huga", 4, 7), 0)
        );

        PatchCandidate patchCandidate01 = mock(DefaultPatchCandidate.class);
        when(patchCandidate01.getLineNumber()).thenReturn(Optional.of(6));
        when(patchCandidate01.getFilePath()).thenReturn("hoge");
        when(patchCandidate01.getAppliedOperation()).thenReturn("VariableReplacementOperation");
        
        PatchCandidate patchCandidate02 = mock(DefaultPatchCandidate.class);
        when(patchCandidate02.getLineNumber()).thenReturn(Optional.of(4));
        when(patchCandidate02.getFilePath()).thenReturn("huga");
        when(patchCandidate02.getAppliedOperation()).thenReturn("CopyReplaceOperation");



        writer.addTestResult(testResults01, patchCandidate01);
        writer.addTestResult(testResults02, patchCandidate02);


    }


    /**
     * 正しくファイルを書けているかテスト
     */
    @Test
    public void testForWrite() {
        writer.write();
        //ファイルを生成するだけなので、assertionは使わず実際に生成されたファイルを確認する

    }
    

}