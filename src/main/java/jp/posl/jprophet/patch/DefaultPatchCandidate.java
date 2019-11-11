package jp.posl.jprophet.patch;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;


/**
 * 実際にプログラムの生成が可能なパッチ候補の実装クラス
 */
public class DefaultPatchCandidate implements PatchCandidate {
    private final Node targetNodeBeforeFix;
    private final CompilationUnit fixedCompilationUnit;
    private final String fixedFilePath;
    private final String fixedFileFqn;

    /**
     * 以下の引数の情報を元にパッチ候補を生成 
     * @param targetNodeBeforeFix 修正されたASTノードの情報を持つRepairUnit
     * @param fixedCompilationUnit 修正されたASTノードの情報を持つRepairUnit
     * @param fixedFilePath 修正されたファイルのパス（jprophetルートからの相対パス）
     * @param fixedFileFQN 修正されたファイルのFQN
     */
    public DefaultPatchCandidate(Node targetNodeBeforeFix, CompilationUnit fixedCompilationUnit, String fixedFilePath, String fixedFileFQN) {
        this.targetNodeBeforeFix = targetNodeBeforeFix;
        this.fixedCompilationUnit = fixedCompilationUnit;
        this.fixedFilePath = fixedFilePath;
        this.fixedFileFqn = fixedFileFQN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilePath(){
        return this.fixedFilePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFqn(){
        return this.fixedFileFqn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompilationUnit getCompilationUnit(){
        return this.fixedCompilationUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Integer> getLineNumber() {
        try {
            Range range = this.targetNodeBeforeFix.getRange().orElseThrow();        
            return Optional.of(range.begin.line);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return this.fixedCompilationUnit.toString();
    }
}
