package jp.posl.jprophet.operation;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;


public interface AstOperation{
    /**
     * 対象ステートメントに対し修正操作を適用し，
     * 適用後のソースコードを含むCompilationUnitを返す 
     * @param targetNode 対象ステートメントを表すノード
     * @return 修正後のソースコードを含んだCompilationUnit
     */
    public List<CompilationUnit> exec(Node targetNode);
}