package jp.posl.jprophet.patch;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;

public class DefaultPatch implements Patch {
    final private CompilationUnit compilationUnit;
    final private CompilationUnit originalCompilationUnit;

    public DefaultPatch(String originalSourceCode, String fixedSourceCode) throws ParseProblemException {
        this.originalCompilationUnit = JavaParser.parse(originalSourceCode);
        this.compilationUnit = JavaParser.parse(fixedSourceCode);
    }

    public DefaultPatch(CompilationUnit originalCompilationUnit, CompilationUnit fixedCompilationUnit) {
        this.originalCompilationUnit = originalCompilationUnit;
        this.compilationUnit = fixedCompilationUnit; 
    }

    public CompilationUnit getCompilationUnit() {
        return this.compilationUnit;        
    }

    public CompilationUnit getOriginalCompilationUnit() {
        return this.originalCompilationUnit;        
    }
}