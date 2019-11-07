package jp.posl.jprophet.spotbugs;

import java.util.List;

import jp.posl.jprophet.patch.PatchCandidate;

public class SpotBugsFixedResult {

    private final SpotBugsWarning fixedWarning;
    private final PatchCandidate patchCandidate;
    private final List<SpotBugsWarning> occurredNewWarnings;


    public SpotBugsFixedResult(SpotBugsWarning fixedWarning, PatchCandidate patchCandidate, List<SpotBugsWarning> occurredNewWarnings) {
        this.fixedWarning = fixedWarning;
        this.patchCandidate = patchCandidate;
        this.occurredNewWarnings = occurredNewWarnings;
    }


    public SpotBugsWarning getFixedWarning() {
        return this.fixedWarning;
    }


    public PatchCandidate getPatchCandidate() {
        return this.patchCandidate;
    }

    public List<SpotBugsWarning> getOccurredNewWarnings() {
        return this.occurredNewWarnings;
    }
    


}