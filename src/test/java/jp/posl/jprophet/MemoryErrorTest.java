package jp.posl.jprophet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;


import org.junit.Test;

import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.operation.*;


public class MemoryErrorTest {

    /**
     * パッチの適用のみを多数繰り返すテスト 途中でOOMEが発生する
     */
    
    @Test public void generatingPatchTest() {
        final AstOperation operation = new CtrlFlowIntroductionOperation();
        String fileName = "src/test/resources/FizzBuzz01/src/main/java/FizzBuzz01/FizzBuzz.java";
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
            String sourceCode = String.join("\n", lines);
            List<Node> targetNodes = NodeUtility.getAllNodesFromCode(sourceCode);
            for(int i = 0; i < 1000; i++) {
                for(Node targetNode : targetNodes){
                    operation.exec(targetNode);    //戻り値を受け取らない
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test public void repeatingNodeUtilityTest() {
        String fileName = "src/test/resources/FizzBuzz01/src/main/java/FizzBuzz01/FizzBuzz.java";
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
            String sourceCode = String.join("\n", lines);
            List<Node> targetNodes = NodeUtility.getAllNodesFromCode(sourceCode);
            //final String abstractConditionName = "ABST_HOLE";
            for(int i = 0; i < 1000; i++) {
                for(Node targetNode : targetNodes){
                    if(!(targetNode instanceof Statement)) continue;
                    if(targetNode instanceof BlockStmt) continue;
                    
                    Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);
                    //deepCopyされたノードのCompilationUnitをreparseするとエラーが発生する
                    NodeUtility.reparseCompilationUnit(copiedTargetNode.findCompilationUnit().orElseThrow());
                    
                    //targetNodeのCompilationUnitでは発生しない
                    //NodeUtility.reparseCompilationUnit(targetNode.findCompilationUnit().orElseThrow());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        

    }
    

    @Test public void repeatingLexicalPreservingPrinterTest() {
        String fileName = "src/test/resources/FizzBuzz01/src/main/java/FizzBuzz01/FizzBuzz.java";
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
            String sourceCode = String.join("\n", lines);
            List<Node> targetNodes = NodeUtility.getAllNodesFromCode(sourceCode);
            for(int i = 0; i < 1000; i++) {
                for(Node targetNode : targetNodes){
                    if(!(targetNode instanceof Statement)) continue;
                    if(targetNode instanceof BlockStmt) continue;
                    Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);
                    LexicalPreservingPrinter.setup(copiedTargetNode);
                    LexicalPreservingPrinter.print(copiedTargetNode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        

    }


}