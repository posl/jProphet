package jp.posl.jprophet.patch;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;


/**
 * 実際にプログラムの生成が可能なパッチ候補の実装クラス
 */
public class PatchCandidate implements Patch {
    private final String fixedFilePath;
    private final String fixedFileFqn;
    private Class<? extends AstOperation> operation;
    private final int id;
    private final OperationDiff operationDiff;

    /**
     * 以下の引数の情報を元にパッチ候補を生成 
     * @param operationDiff 修正操作によるdiff
     * @param fixedFilePath 修正されたファイルのパス（jprophetルートからの相対パス）
     * @param fixedFileFQN 修正されたファイルのFQN
     * @param operation 適用されたオペレータのクラス
     * @param id パッチに割り当てるid
     */
    public PatchCandidate(OperationDiff operationDiff, String fixedFilePath, String fixedFileFQN, Class<? extends AstOperation> operation, int id) {
        this.operationDiff = operationDiff;
        this.fixedFilePath = fixedFilePath;
        this.fixedFileFqn = fixedFileFQN;
        this.operation = operation;
        this.id = id;
    }

    public OperationDiff getOperationDiff() {
        return operationDiff;
    }
 
    /**
     * 修正パッチ候補のIDを返す
     * @return 修正パッチ候補の整数型ID
     */
    public int getId() {
        return id;
    }

    /**
     * 修正されたファイルのパス(jProphetプロジェクトのルートから見た相対パス)を返す
     * @return 修正されたファイルのパス
     */
    public String getFilePath(){
        return this.fixedFilePath;
    }

    /**
     * 修正されたファイルのFQNを返す
     * @return FQN文字列
     */
    public String getFqn(){
        return this.fixedFileFqn;
    }

    /**
     * 修正されたファイルのCompilationUnitを返す
     * @return 修正されたファイルのCompilationUnit
     */
    @Override
    public CompilationUnit getFixedCompilationUnit(){
        if (this.operationDiff.getModifyType().equals(ModifyType.INSERT)) {
            return NodeUtility.insertNodeWithNewLine(this.operationDiff.getTargetNodeAfterFix(), this.operationDiff.getTargetNodeBeforeFix()).get().findCompilationUnit().get();
        } else if (this.operationDiff.getModifyType().equals(ModifyType.CHANGE)) {
            return NodeUtility.replaceNode(this.operationDiff.getTargetNodeAfterFix(), this.operationDiff.getTargetNodeBeforeFix()).get().findCompilationUnit().get();
        }
        return this.operationDiff.getTargetNodeBeforeFix().findCompilationUnit().get(); 
    }

    /**
     * 修正前のファイルのCompilationUnitを返す
     * @return 修正前のファイルのCompilationUnit
     */
    public CompilationUnit getOriginalCompilationUnit(){
        return this.operationDiff.getTargetNodeBeforeFix().findCompilationUnit().orElseThrow();
    }

    /**
     * 修正対象のステートメントのソースファイル全体における行番号を返す 
     * NodeからRangeが取れなかった場合Optional.emptyが返る
     * TODO: 一つの行番号ではなく何行目から何行目のような範囲を返した方がいいかもしれない
     * JavaParserのパッケージないのRangeクラスなど
     * @return 修正対象のステートメントのソースファイル全体における行番号
     */
    public Optional<Integer> getLineNumber() {
        try {
            Range range = this.operationDiff.getTargetNodeBeforeFix().getRange().orElseThrow();        
            return Optional.of(range.begin.line);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * 適用したオペレーションを返す
     * @return 適用したオペレーションを表す文字列
     */
     public String getAppliedOperation() {
         return operation.getName().replace("jp.posl.jprophet.operation.", "");
     }

    /**
     * 修正されたAST部分のソースコードを返す 
     * @return 修正されたAST部分のソースコード
     */
    @Override
    public String toString(){
        return new StringBuilder().append("")
            .append("ID : " + this.getId())
            .append("\n")
            .append("fixed file path : " + this.fixedFilePath)
            .append("\n")
            .append("used operation  : " + this.operation.getSimpleName())
            .append("\n\n")
            .append(new RepairDiff(this.operationDiff.getTargetNodeBeforeFix(), this.getFixedCompilationUnit()).toString())
            .toString();
    }
}
