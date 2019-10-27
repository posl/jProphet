package jp.posl.jprophet;

import com.github.javaparser.ast.CompilationUnit;

public class PatchCandidateWithAbstHole implements PatchCandidate {
    private PatchCandidateImpl patchCandidate;
    public PatchCandidateWithAbstHole(RepairUnit repairUnit, String fixedFilePath) {
        this.patchCandidate = new PatchCandidateImpl(repairUnit, fixedFilePath);
    }

    public String getFixedFilePath(){
        return this.patchCandidate.getFixedFilePath();
    }

    public CompilationUnit getCompilationUnit(){
        return this.patchCandidate.getCompilationUnit();
    }

    public String toString(){
        return this.patchCandidate.toString();
    }
}
