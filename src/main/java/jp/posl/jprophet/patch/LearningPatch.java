package jp.posl.jprophet.patch;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;

import jp.posl.jprophet.NodeUtility;

/**
 * 学習用のパッチを表現するクラス
 */
public class LearningPatch implements Patch {
    final private CompilationUnit fixedCompilationUnit;
    final private CompilationUnit originalCompilationUnit;

    /**
     * 修正前後のソースコードを元にパッチクラスを作成
     * @param originalSourceCode 修正前コード
     * @param fixedSourceCode 修正後コード
     * @throws ParseProblemException
     */
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

    /**
     * 修正前後のCompilationUnitを元にパッチクラスを作成
     * @param originalCompilationUnit 修正前のCompilationUnit
     * @param fixedCompilationUnit 修正後のCompilationUnit
     */
    public LearningPatch(CompilationUnit originalCompilationUnit, CompilationUnit fixedCompilationUnit) {
        this.originalCompilationUnit = JavaParser.parse(originalCompilationUnit.toString());
        this.fixedCompilationUnit = JavaParser.parse(fixedCompilationUnit.toString());
    }

    /**
     * {@inheritDoc}
     */
    public CompilationUnit getFixedCompilationUnit() {
        return this.fixedCompilationUnit;        
    }

    /**
     * {@inheritDoc}
     */
    public CompilationUnit getOriginalCompilationUnit() {
        return this.originalCompilationUnit;        
    }
}
