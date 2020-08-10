package jp.posl.jprophet.patch;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;

import jp.posl.jprophet.NodeUtility;

public class LearningPatch implements Patch {
    final private CompilationUnit fixedCompilationUnit;
    final private CompilationUnit originalCompilationUnit;

    public LearningPatch(String originalSourceCode, String fixedSourceCode) throws ParseProblemException {
        final CompilationUnit originalCu = JavaParser.parse(originalSourceCode);
        final CompilationUnit fixedCu = JavaParser.parse(fixedSourceCode);
        // コメントの削除
        // コード変形後にコメントとノードが合体するとequal判定の結果が変わり，
        // 挿入したノードの検索が不可能になる
        NodeUtility.getAllNodesInDepthFirstOrder(originalCu.findRootNode()).stream()
            .forEach(n-> n.removeComment());
        NodeUtility.getAllNodesInDepthFirstOrder(fixedCu.findRootNode()).stream()
            .forEach(n-> n.removeComment());
        this.originalCompilationUnit = JavaParser.parse(originalCu.toString());
        this.fixedCompilationUnit = JavaParser.parse(fixedCu.toString());
    }

    public LearningPatch(CompilationUnit originalCompilationUnit, CompilationUnit fixedCompilationUnit) {
        this.originalCompilationUnit = JavaParser.parse(originalCompilationUnit.toString());
        this.fixedCompilationUnit = JavaParser.parse(fixedCompilationUnit.toString());
    }

    public CompilationUnit getFixedCompilationUnit() {
        return this.fixedCompilationUnit;        
    }

    public CompilationUnit getOriginalCompilationUnit() {
        return this.originalCompilationUnit;        
    }
}
