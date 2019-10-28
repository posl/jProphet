package jp.posl.jprophet;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public class PatchCandidateImpl implements PatchCandidate {
    private final Node fixedNode; 
    private final CompilationUnit compilationUnit;
    private final String fixedFilePath;
    public PatchCandidateImpl(RepairUnit repairUnit, String fixedFilePath) {
        this.fixedNode = repairUnit.getTargetNode();
        this.compilationUnit = repairUnit.getCompilationUnit();
        this.fixedFilePath = fixedFilePath;
    }

    public String getFilePath(){
        return this.fixedFilePath;
    }

    public CompilationUnit getCompilationUnit(){
        return this.compilationUnit;
    }

    public Optional<Integer> getLineNumber() {
        try {
            Range range = this.fixedNode.getRange().orElseThrow();        
            if(range.begin.line == range.end.line){
                return Optional.of(range.begin.line);
            }
            else {
                return Optional.empty();
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public String toString(){
        return this.compilationUnit.toString();
    }
}
