package jp.posl.jprophet;

import com.github.javaparser.ast.CompilationUnit;

public class AbstractRepairCandidate implements RepairCandidate {
    private CompilationUnit compilationUnit;
    private String fixedFilePath;
    public AbstractRepairCandidate(CompilationUnit compilationUnit, String fixedFilePath) {
        this.compilationUnit = compilationUnit;
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
