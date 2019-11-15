package jp.posl.jprophet.patch;

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;

public interface PatchCandidate {
    /**
     * 修正されたファイルのパス(jProphetプロジェクトのルートから見た相対パス)を返す
     * @return 修正されたファイルのパス
     */
    public String getFilePath();

    /**
     * 修正されたファイルのFQNを返す
     * @return FQN文字列
     */
    public String getFqn();

    /**
     * 修正されたファイルのCompilationUnitを返す
     * @return 修正されたファイルのCompilationUnit
     */
    public CompilationUnit getCompilationUnit();

    /**
     * 修正対象のステートメントのソースファイル全体における行番号を返す 
     * NodeからRangeが取れなかった場合Optional.emptyが返る
     * TODO: 一つの行番号ではなく何行目から何行目のような範囲を返した方がいいかもしれない
     * JavaParserのパッケージないのRangeクラスなど
     * @return 修正対象のステートメントのソースファイル全体における行番号
     */
    public Optional<Integer> getLineNumber();


    /**
     * 適用したオペレーションを返す
     * @return　適用したオペレーションを表す文字列
     */
    public String getAppliedOperation();

    /**
     * 修正されたAST部分のソースコードを返す 
     * @return 修正されたAST部分のソースコード
     */
    public String toString();
}
