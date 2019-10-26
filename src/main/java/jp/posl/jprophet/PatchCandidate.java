package jp.posl.jprophet;


import com.github.javaparser.ast.CompilationUnit;

public interface PatchCandidate {
    public String getFixedFilePath();
    public CompilationUnit getCompilationUnit();
    public String toString();

}
