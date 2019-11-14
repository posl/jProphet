package jp.posl.jprophet;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;

import org.junit.Before;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.JavaParser;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import jp.posl.jprophet.NodeUtility;

public class LexicalTest{

    /**
     * ステートメントコピペ後の修正候補の数のテスト
     */
    @Test public void testForFlexible(){
        String source = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   private String fa = \"a\";\n")
            .append("   private void ma(String pa, String pb) {\n")
            .append("       String la = \"b\";\n")
            .append("       la = \"hoge\";\n")
            .append("       ld = \"huga\";\n")
            .append("   }\n")
            .append("}\n")
            .toString();
        
        CompilationUnit compilationUnit = JavaParser.parse(source);
        //LexicalPreservingPrinter.setup(compilationUnit);
        System.out.println(source);

        BlockStmt block = (BlockStmt)compilationUnit.getChildNodes().get(0).getChildNodes().get(2).getChildNodes().get(4);
        NodeList<Statement> nodeList = block.getStatements();

        Statement statement = nodeList.get(0).clone();

        /*
        Range statementRange = new Range(new Position(2, 0), new Position(2, 0));
        Range otherRange = new Range(new Position(1, 0), new Position(1, 0));
        Range kakkoRange = new Range(new Position(0, 0), new Position(1, 0));
        this.addNodeRange(statement, statementRange);
        NodeUtility.getAllDescendantNodes(statement).forEach(n -> addNodeRange(n, statementRange));
        this.addNodeRange(nodeList.get(2), otherRange);
        NodeUtility.getAllDescendantNodes(nodeList.get(2)).forEach(n -> addNodeRange(n, otherRange));
        addNodeRange(block, kakkoRange);
        addNodeRange(block.getParentNode().orElseThrow(), kakkoRange);
        addNodeRange(block.getParentNode().orElseThrow().getParentNode().orElseThrow(), kakkoRange);
        addNodeRange(block.findCompilationUnit().orElseThrow(), kakkoRange);
        */

        //nodeList.add(2, statement);
        Range statementRange = new Range(new Position(2, 0), new Position(2, 0));
        JavaToken previous = nodeList.get(2).getTokenRange().orElseThrow().getBegin().getPreviousToken().orElseThrow();
        JavaToken next = nodeList.get(2).getTokenRange().orElseThrow().getBegin();
        Range endRange = next.getRange().orElseThrow();
        //Range previousRange = 

        //nodeList.get(2).getTokenRange().orElseThrow().getBegin().insert(new JavaToken(endRange, 89, "String", null, null));
        //nodeList.get(2).getTokenRange().orElseThrow().getBegin().insert(new JavaToken(endRange, 3, "\n", null, null));
        insertTokenBefore(nodeList.get(2).getTokenRange().orElseThrow(), nodeList.get(1).getTokenRange().orElseThrow(),nodeList.get(0).getTokenRange().orElseThrow());
        CompilationUnit compilationUnit2 = nodeList.getParentNode().orElseThrow().findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(compilationUnit2);
        String source2 = LexicalPreservingPrinter.print(compilationUnit2);
        String source3 = LexicalPreservingPrinter.print(nodeList.get(2));
        System.out.println(source2);
        System.out.println(source3);

        CompilationUnit parsedCompilationUnit = JavaParser.parse(source2);

        return;
    }

    private void insertTokenBefore(TokenRange targetTokenRange, TokenRange beforeTargetTokenRange, TokenRange insertTokenRange){
        final JavaToken beginTokenOfTarget = targetTokenRange.getBegin();
        Range beginRangeOfTarget = beginTokenOfTarget.getRange().orElseThrow();
        JavaToken beginTokenOfInsert = insertTokenRange.getBegin();
        JavaToken endTokenOfInsert = insertTokenRange.getEnd();
        JavaToken insertJavaToken = beginTokenOfInsert;
        JavaToken endTokenOfBeforeTarget = beforeTargetTokenRange.getEnd();
        while (true){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, insertJavaToken.getKind(), insertJavaToken.getText(), null, null));
            if (insertJavaToken.equals(endTokenOfInsert)){
                break;
            }
            insertJavaToken = insertJavaToken.getNextToken().orElseThrow();
        }
        insertJavaToken = endTokenOfBeforeTarget.getNextToken().orElseThrow();
        while (true){
            if (insertJavaToken.equals(beginTokenOfInsert)){
                break;
            }
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, insertJavaToken.getKind(), insertJavaToken.getText(), null, null));
            
            insertJavaToken = insertJavaToken.getNextToken().orElseThrow();
        }

    }

    private void addNodeRange(Node node, Range addRange){
        int beginLine = getBeginLineNumber(node).orElseThrow();
        int beginColumn = getBeginColumnNumber(node).orElseThrow();
        int endLine = getEndLineNumber(node).orElseThrow();
        int endColumn = getEndColumnNumber(node).orElseThrow();
        node.setRange(new Range(new Position(beginLine + addRange.begin.line, beginColumn + addRange.begin.column), new Position(endLine + addRange.end.line, endColumn + addRange.end.column)));
    }

    /**
     * ノードの始まりの行番号を取得する
     * @param node ノード
     * @return ノードの始まりの行番号
     */
    private Optional<Integer> getBeginLineNumber(Node node) {
        try {
            Range range = node.getRange().orElseThrow();        
            return Optional.of(range.begin.line);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * ノードの終わりの行番号を取得
     * @param node ノード
     * @return ノードの終わりの行番号
     */
    private Optional<Integer> getEndLineNumber(Node node) {
        try {
            Range range = node.getRange().orElseThrow();        
            return Optional.of(range.end.line);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * ノードの始まりのコラム番号を取得する
     * @param node ノード
     * @return ノードの始まりのコラム番号
     */
    private Optional<Integer> getBeginColumnNumber(Node node) {
        try {
            Range range = node.getRange().orElseThrow();        
            return Optional.of(range.begin.column);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * ノードの終わりのコラム番号を取得する
     * @param node ノード
     * @return ノードの終わりのコラム番号
     */
    private Optional<Integer> getEndColumnNumber(Node node) {
        try {
            Range range = node.getRange().orElseThrow();        
            return Optional.of(range.end.column);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
}