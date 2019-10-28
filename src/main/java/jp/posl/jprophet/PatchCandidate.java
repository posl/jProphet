package jp.posl.jprophet;

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;

public interface PatchCandidate {
    /**
     * 修正されたファイルのパスを返す
     * @return 修正されたファイルのパス
     */
    public String getFilePath();

    /**
     * 修正されたファイルのCompilationUnitを返す
     * @return 修正されたファイルのCompilationUnit
     */
    public CompilationUnit getCompilationUnit();

    /**
     * 修正対象のステートメントのソースファイル全体における行番号を返す 
     * @return 修正対象のステートメントのソースファイル全体における行番号
     */
    public Optional<Integer> getLineNumber();

    /**
     * 修正されたAST部分のソースコードを返す 
     * @return 修正されたAST部分のソースコード
     */
    public String toString();
}
