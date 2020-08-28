package jp.posl.jprophet.fl;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.fl.spectrumbased.ExecutionTest;

public interface FaultLocalization {
    public List<Suspiciousness> exec();

    default public List<ExecutionTest> getExecutionTests() {
        return new ArrayList<ExecutionTest>();
    }
}