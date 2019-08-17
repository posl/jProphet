package jp.posl.jprophet;

import com.github.javaparser.ast.CompilationUnit;

public class AbstractRepairCandidate extends RepairCandidate {
    private CompilationUnit compilationUnit;
    private String filePath;
    public AbstractRepairCandidate(CompilationUnit compilationUnit, String filePath){
        this.compilationUnit = compilationUnit;
        this.filePath = filePath;
    }

    public String getFilePath(){
        return this.filePath;
    }

    public CompilationUnit getCompilationUnit(){
        return this.compilationUnit;
    }

    public String toString(){
        return this.compilationUnit.toString();
    }
}
