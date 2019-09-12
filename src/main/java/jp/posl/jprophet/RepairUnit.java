package jp.posl.jprophet;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.printer.*;

public class RepairUnit {
    private Node targetNode;
    private int targetNodeIndex;
    private CompilationUnit compilationUnit;

    public RepairUnit(Node targetNode, int targetNodeIndex, CompilationUnit compilationUnit){
        this.targetNode = targetNode;
        this.targetNodeIndex = targetNodeIndex;
        this.compilationUnit = compilationUnit;
    }

    public Node getNode(){
        return this.targetNode;
    };

    public CompilationUnit getCompilationUnit(){
        return this.compilationUnit;
    }

    public String getSourceCode() {
        //return LexicalPreservingPrinter.print(this.compilationUnit);
        return new PrettyPrinter(new PrettyPrinterConfiguration()).print(this.compilationUnit);
    }

    public String toString(){
        return this.targetNode.toString();
    }

    public int getLineNumber(){
        // TODO
        TokenRange range = this.targetNode.getTokenRange().get();
        return 0;
    }

    public int getTargetNodeIndex(){
        return this.targetNodeIndex; 
    }

}
