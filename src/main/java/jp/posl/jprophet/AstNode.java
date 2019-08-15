package jp.posl.jprophet;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.printer.*;

public class AstNode {
    private Node node;
    private CompilationUnit compilationUnit;

    public AstNode(Node node, CompilationUnit compilationUnit){
        this.node = node;
        this.compilationUnit = compilationUnit;
    }

    public Node get(){
        return this.node;
    };

    // public void accept(RepairCandidateGenerator rcGenerator){
    //     rcGenerator.applyTemplate(this.get());
    //     return;
    // }

    // protected AstNode(TokenRange tokenRange) {
    //     super(tokenRange);
    //     // TODO Auto-generated constructor stub
    // }

    public String getSourceCode() {
        //return LexicalPreservingPrinter.print(this.compilationUnit);

        return new PrettyPrinter(new PrettyPrinterConfiguration()).print(this.compilationUnit);
    }

    public int getLineNumber(int i){
        // 未実装
        TokenRange range = this.node.getTokenRange().get();
        return i;
    }

}
