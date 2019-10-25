package jp.posl.jprophet;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

public interface RepairCandidate {
    public List<String> getFixedFilePaths();
    public CompilationUnit getCompilationUnit();
    public String toString();

}
