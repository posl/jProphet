package jp.posl.jprophet;

import com.github.javaparser.ast.CompilationUnit;
import java.util.List;

public class AbstractRepairCandidate implements RepairCandidate {
    private CompilationUnit compilationUnit;
    private List<String> fixedFilePaths;
    public AbstractRepairCandidate(CompilationUnit compilationUnit, List<String> fixedFilePaths) {
        this.compilationUnit = compilationUnit;
        this.fixedFilePaths = fixedFilePaths;
    }

    public List<String> getFixedFilePaths(){
        return this.fixedFilePaths;
    }

    public CompilationUnit getCompilationUnit(){
        return this.compilationUnit;
    }

    public String toString(){
        return this.compilationUnit.toString();
    }
}
