package jp.posl.jprophet.fl;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.fl.spectrumbased.TestCase;

public interface FaultLocalization {
    public List<Suspiciousness> exec();

    default public List<TestCase> getExecutedTests() {
        return new ArrayList<TestCase>();
    }
}