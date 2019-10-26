package jp.posl.jprophet;

import com.github.javaparser.ast.CompilationUnit;

public class AbstractPatchCandidate implements PatchCandidate {
    private CompilationUnit compilationUnit;
    private String fixedFilePath;
    public AbstractPatchCandidate(CompilationUnit compilationUnit, String fixedFilePath) {
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
