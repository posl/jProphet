package jp.posl.jprophet.fl.spectrumbased;

import java.util.List;

public class TestCase {
    private String sourceName;
    private List<String> testNames;

    public TestCase(String sourceName, List<String> testNames) {
        this.sourceName = sourceName;
        this.testNames = testNames;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public List<String> getTestNames() {
        return this.testNames;
    }
}