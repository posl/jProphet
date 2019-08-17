package jp.posl.jprophet;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.printer.*;

public class RepairUnit {
    private Node targetNode;
    private CompilationUnit compilationUnit;
    private String filePath;

    public RepairUnit(Node node, CompilationUnit compilationUnit, String filePath){
        this.targetNode = node;
        this.compilationUnit = compilationUnit;
        this.filePath = filePath;
    }

    public Node getNode(){
        return this.targetNode;
    };

    public String getFilePath(){
        return this.filePath;
    }

    public CompilationUnit getCompilationUnit(){
        return this.compilationUnit;
    }

    public String getSourceCode() {
        //return LexicalPreservingPrinter.print(this.compilationUnit);

        return new PrettyPrinter(new PrettyPrinterConfiguration()).print(this.compilationUnit);
    }

    public String toString(){
        return this.compilationUnit.toString();
    }

    public int getLineNumber(){
        // 未実装
        TokenRange range = this.targetNode.getTokenRange().get();
        return 0;
    }

}
