package jp.posl.jprophet;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.*;

public class RepairUnit {
    private Node targetNode;
    final private int targetNodeIndex;
    private CompilationUnit compilationUnit;

    /**
     * 修正する対象のソースコードのASTをまとめたRepairUnitクラスを作成する
     *  
     * @param targetNode 修正対象のステートメントを指すAST
     * @param targetNodeIndex ソースファイル全体のASTノードをレベル順にソートした場合の修正対象ステートメントのASTのインデックス 
     * @param compilationUnit 修正対象のステートメントを含むソースファイル全体のASTを持ったCompilationUnit
     */
    public RepairUnit(Node targetNode, int targetNodeIndex, CompilationUnit compilationUnit){
        this.targetNode = targetNode;
        this.targetNodeIndex = targetNodeIndex;
        this.compilationUnit = compilationUnit;
    }

    /**
     * RepairUnitインスタンスのディープコピーを作成する
     * （JavaParserのNodeクラスの提供するcloneメソッドが親ノードの参照をコピーしないためこのメソッドを作成した）
     *   
     * @param repairUnit コピー元のRepairUnitインスタンス
     * @return コピーされたRepairUnitインスタンス
     */
    public static RepairUnit deepCopy(RepairUnit repairUnit){
        int targetNodeIndex = repairUnit.getTargetNodeIndex();
        CompilationUnit cu = repairUnit.getCompilationUnit();
        CompilationUnit newCu = cu.clone();
        Node newTargetNode = NodeUtility.findByLevelOrderIndex(newCu, targetNodeIndex).orElseThrow();
                
        return new RepairUnit(newTargetNode, targetNodeIndex, newCu);
    }

    /**
     * 修正対象のステートメントのASTを返す  
     * @return 修正対象のステートメント
     */
    public Node getTargetNode(){
        return this.targetNode;
    };

    /**
     * 修正対象のステートメントを含んだソースファイル全体のASTを持つCompilationUnitを返す 
     * @return 修正対象のステートメントを含んだソースファイル全体のASTを持つCompilationUnit
     */
    public CompilationUnit getCompilationUnit(){
        return this.compilationUnit;
    }

    /**
     * CompilationUnitから生成されるソースコードを返す
     * TODO: LexicalPreservingPrinterを使おうとするとエラーが出るのでPrettyPrinterを使っている
     * 本当はLPPの方が元のソースコードの再現度が高い
     * @return CompilationUnitから生成されるソースコード
     */
    public String getSourceCode() {
        return new PrettyPrinter(new PrettyPrinterConfiguration()).print(this.compilationUnit);
    }

    /**
     * 修正対象のステートメントを文字列表現で返す 
     * @return 修正対象のステートメントの文字列表現
     */
    @Override public String toString(){
        return this.targetNode.toString();
    }

    /**
     * ソースファイル全体のASTノードをレベル順にソートした場合の修正対象ステートメントのASTのインデックスを返す
     * @return ソースファイル全体のASTノードをレベル順にソートした場合の修正対象ステートメントのASTのインデックス 
     */
    public int getTargetNodeIndex(){
        return this.targetNodeIndex; 
    }
}
