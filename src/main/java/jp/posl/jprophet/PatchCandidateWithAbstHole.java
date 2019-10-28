package jp.posl.jprophet;

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;

public class PatchCandidateWithAbstHole implements PatchCandidate {
    private PatchCandidateImpl patchCandidate;
    public PatchCandidateWithAbstHole(RepairUnit repairUnit, String fixedFilePath) {
        this.patchCandidate = new PatchCandidateImpl(repairUnit, fixedFilePath);
    }

    public String getFilePath(){
        return this.patchCandidate.getFilePath();
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
