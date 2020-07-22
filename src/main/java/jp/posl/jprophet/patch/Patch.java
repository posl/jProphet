package jp.posl.jprophet.patch;

import com.github.javaparser.ast.CompilationUnit;

public interface Patch {
    /**
     * 修正されたファイルのCompilationUnitを返す
     * @return 修正されたファイルのCompilationUnit
     */
    public CompilationUnit getCompilationUnit();

    /**
     * 修正前のファイルのCompilationUnitを返す
     * @return 修正前のファイルのCompilationUnit
     */
    public CompilationUnit getOriginalCompilationUnit();

}
