package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.expr.Expression;

import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.RepairUnit;

/**
 * 対象ステートメントの以前に現れているステートメントを，
 * 対象ステートメントの直前に挿入し，さらに置換操作(ValueReplacementOperation)を適用する．
 */
public class CopyReplaceOperation implements AstOperation{

    private final RepairUnit repairUnit;
    private final Node targetNode;

    public CopyReplaceOperation(RepairUnit repairUnit){
        this.repairUnit = repairUnit;
        this.targetNode = this.repairUnit.getTargetNode();
    }

    @Override
    public List<RepairUnit> exec(){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        //修正対象のステートメントの属するメソッドノードを取得
        //メソッド内のステートメント(修正対象のステートメントより前のもの)を収集
        List<Statement> statements = collectLocalStatements(this.targetNode);
        if (statements.size() != 0){
            candidates = copyStatementBeforeTarget(statements, this.repairUnit);
        }
        //修正対象のステートメントの直前に,収集したステートメントのNodeを追加
        return candidates;
    }

    /**
     * targetNodeを含むメソッド内のステートメントで,targetNodeよりも上にあるものを取得
     * @param targetNode ターゲットノード
     * @return ステートメントのリスト
     */
    private List<Statement> collectLocalStatements(Node targetNode){
        MethodDeclaration methodNode;
        try {
            methodNode =  targetNode.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<Statement>();
        }

        List<Statement> localStatements = methodNode.findAll(Statement.class);

        //targetStatementを含むそれより後ろの行の要素を全て消す
        //BlockStmtを全て除外する
        return localStatements.stream()
            .filter(s -> getEndLineNumber(s).orElseThrow() < getBeginLineNumber(targetNode).orElseThrow())
            .filter(s -> (s instanceof BlockStmt) == false)
            .collect(Collectors.toList());
    }

    /**
     * targetNodeの直前にstatementをコピペしてrepairUnitを生成してそのリストを返す
     * @param statements コピペするステートメントのリスト
     * @param repairUnit targetNodeを含むrepairUnit
     * @return repairUnitのリスト
     */
    private List<RepairUnit> copyStatementBeforeTarget(List<Statement> statements, RepairUnit repairUnit){
        Node targetNode = repairUnit.getTargetNode();
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        BlockStmt blockStatement;
        try {
            blockStatement =  targetNode.findParent(BlockStmt.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return candidates;
        }
        
        for (Statement statement : statements){            
            NodeList<Statement> nodeList = blockStatement.clone().getStatements();
            if(targetNode instanceof Statement && nodeList.indexOf(targetNode) != -1){
                NodeList<Statement> newNodeList = nodeList.addBefore(statement, (Statement)targetNode);
                RepairUnit newCandidate = RepairUnit.copy(repairUnit);
                //taergetNodeの親ノード(多分BlockStmt)を丸ごと置き換えることでコピペしている
                newCandidate.getTargetNode().getParentNode().orElseThrow().replace(newNodeList.get(nodeList.indexOf(targetNode)).getParentNode().orElseThrow());
                //candidates.add(newCandidate);
                //List<RepairUnit> units = getAllRepairUnit(newCandidate.getCompilationUnit());

                //Nodeをコピペして任意の場所に入れると,コピペ前のNodeの情報がそのまま入るので行番号がおかしくなる
                //compilationUnitのtoString()ではちゃんとステートメントが意図した場所にコピペされている
                //compilationUnitをtoString()してAstGeneratorにかけてcompilationUnitを作り直す
                //問題点 : compilationUnitをtoString()すると改行や空白が入ってしまうので元のコードと形が変わる
                //        行数が変わるからVariableReplacementにかけたい行がわからない 
                AstGenerator astGenerator = new AstGenerator();
                List<RepairUnit> units = astGenerator.getAllRepairUnit(newCandidate.getCompilationUnit().toString());
                List<RepairUnit> le = units.stream()
                    .filter(unit -> unit.getTargetNode() instanceof Expression)
                    .filter(unit -> (unit.toString() + ";").equals(statement.toString()))
                    //.filter(unit -> getBeginLineNumber(unit.getTargetNode()).orElseThrow() == (getBeginLineNumber(targetNode).orElseThrow()))
                    .collect(Collectors.toList());
                
                if (le.size() != 0){
                    RepairUnit leunit = le.get(1);  //行番号でleが取得できればget(0)
                    VariableReplacementOperation vr = new VariableReplacementOperation(leunit);
                    List<RepairUnit> copiedNodeList = vr.exec();
                    candidates.addAll(copiedNodeList);
                }
                
            }
        }
        return candidates;
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
     * ソースコードから全てのASTノードを抽出し，修正単位であるRepairUnitを取得する.
     * 
     * @param compilationUnit AST抽出対象のソースコード
     * @return 修正対象のASTノードとコンパイルユニットを持った修正単位であるRepairUnitのリスト
     */
    private List<RepairUnit> getAllRepairUnit(CompilationUnit compilationUnit){
        List<RepairUnit> repairUnits = new ArrayList<RepairUnit>();
        for(int i = 0;/*終了条件なし*/; i++){
            CompilationUnit newCompilationUnit;   //RepairUnitごとに新しいインスタンスの生成
            newCompilationUnit = compilationUnit;
            // なくなるまで順にASTノードを取り出す
            try {
                Node node = AstGenerator.findByLevelOrderIndex(newCompilationUnit.findRootNode(), i).orElseThrow(); 
                repairUnits.add(new RepairUnit(node, i, compilationUnit)); 
            } catch (NoSuchElementException e) {
                return repairUnits;
            }
        }
    }
}