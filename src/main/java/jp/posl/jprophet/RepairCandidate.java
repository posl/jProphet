package jp.posl.jprophet;


import com.github.javaparser.ast.CompilationUnit;

public interface RepairCandidate {
    public String getFixedFilePath();
    public CompilationUnit getCompilationUnit();
    public String toString();

}
