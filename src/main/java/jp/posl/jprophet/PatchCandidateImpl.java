package jp.posl.jprophet;

import com.github.javaparser.ast.CompilationUnit;

public class PatchCandidateImpl implements PatchCandidate {
    private CompilationUnit compilationUnit;
    private String fixedFilePath;
    public PatchCandidateImpl(RepairUnit repairUnit, String fixedFilePath) {
        this.compilationUnit = repairUnit.getCompilationUnit();
        this.fixedFilePath = fixedFilePath;
    }

    public String getFixedFilePath(){
        return this.fixedFilePath;
    }

    public CompilationUnit getCompilationUnit(){
        return this.compilationUnit;
    }

    public String toString(){
        return this.compilationUnit.toString();
    }
}
