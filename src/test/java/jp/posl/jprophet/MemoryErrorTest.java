package jp.posl.jprophet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

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
            final String abstractConditionName = "ABST_HOLE";
            for(int i = 0; i < 1000; i++) {
                for(Node targetNode : targetNodes){
                    if(!(targetNode instanceof Statement)) continue;
                    if(targetNode instanceof BlockStmt) continue;
                    final Statement thenStmt = (Statement)NodeUtility.deepCopyByReparse(targetNode); 
                    final IfStmt newIfStmt =  (IfStmt)JavaParser.parseStatement((new IfStmt(new MethodCallExpr(abstractConditionName), thenStmt, null)).toString());
                    
                    NodeUtility.replaceNode(newIfStmt, targetNode);
                    //この行をコメントアウトするとエラーは起きない
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        

    }
    


}