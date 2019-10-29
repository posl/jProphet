package jp.posl.jprophet;

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;

public class PatchCandidateWithAbstHole implements PatchCandidate {
    private PatchCandidateImpl patchCandidate;
    public PatchCandidateWithAbstHole(RepairUnit repairUnit, String fixedFilePath, String fixedFileFQN) {
        this.patchCandidate = new PatchCandidateImpl(repairUnit, fixedFilePath, fixedFileFQN);
    }

    public String getFilePath(){
        return this.patchCandidate.getFilePath();
    }

    public String getFQN(){
        return this.patchCandidate.getFQN();
    }

    public CompilationUnit getCompilationUnit(){
        return this.patchCandidate.getCompilationUnit();
    }

    public Optional<Integer> getLineNumber() {
        return this.patchCandidate.getLineNumber();
    }

    public String toString(){
        return this.patchCandidate.toString();
    }
}
