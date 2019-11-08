package jp.posl.jprophet;

import com.github.javaparser.ast.Node;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


public class AstGenerator {

    /**
     * ノードの子ノードを幅優先で探索し，与えられたインデックスのノードを返す
     * 
     * @param node 検索対象の親ノード
     * @param targetIndex レベル順（幅優先）のインデックス
     * @return レベル順でtargetIndex番目のノード
     */
    public static Optional<Node> findByLevelOrderIndex(Node node, int targetIndex){
        List<Node> childNodes = new LinkedList<Node>(node.getChildNodes());
        for(int i = 0;;i++){
            if(childNodes.isEmpty()){ 
                return Optional.empty();
            }
            Node head = childNodes.remove(0);
            if(i == targetIndex){
                return Optional.of(head); 
            }
            childNodes.addAll(head.getChildNodes());
        }
    }

    /**
     * Nodeインスタンスのディープコピーを作成する
     * （JavaParserのNodeクラスの提供するcloneメソッドが親ノードの参照をコピーしないためこのメソッドを作成した）
     *   
     * @param node コピー元のインスタンス
     * @return ディープコピーによって生成されたインスタンス
     */
    public static Node deepCopy(Node node) {
        Node rootNode = node.findRootNode();
        CompilationUnit cu = (CompilationUnit)(rootNode);

        CompilationUnit newCu = cu.clone();
        List<Node> nodes = AstGenerator.getAllDescendantNodes(newCu);
        Node newNode = nodes.stream().filter(n -> {
            return n.equals(node) && n.getRange().equals(node.getRange());
        }).findFirst().orElseThrow();
        return newNode;
    }


    /**
     * Nodeの全ての子孫ノード（ASTツリー上の全ての子要素）を取得する
     * @param parentNode 子孫ノードを取得したいノード
     * @return 子孫ノードのリスト
     */
    public static List<Node> getAllDescendantNodes(Node parentNode) {
        List<Node> descendantNodes = new ArrayList<Node>();
        descendantNodes.addAll(parentNode.getChildNodes());
        parentNode.getChildNodes().stream().map(n -> {
            return getAllDescendantNodes(n);
        }).forEach(descendantNodes::addAll);
        return descendantNodes;
    }

    /**
     * Nodeの全ての子孫ノード（ASTツリー上の全ての子要素）のディープコピーを取得する 
     * @param parentNode 子孫ノードを取得したいノード
     * @return ディープコピーされた子孫ノードのリスト
     */
    public static List<Node> getAllCopiedDescendantNodes(Node parentNode) {
        List<Node> descendantNodes = AstGenerator.getAllDescendantNodes(parentNode); 
        List<Node> copiedDescendantNodes = descendantNodes.stream()
            .map(AstGenerator::deepCopy)
            .collect(Collectors.toList());

        return copiedDescendantNodes;
    }

    /**
     * ソースコードからASTノード全てを取得する
     * 各ノードはそれぞれ異なるインスタンスのASTツリーに属する
     * @param sourceCode AST抽出対象のソースコード
     * @return ASTノードのリスト
     */
    public static List<Node> getAllNodesFromCode(String sourceCode) {
        return AstGenerator.getAllCopiedDescendantNodes(JavaParser.parse(sourceCode));
    }

    /**
     * ソースコードから全てのASTノードを抽出し，修正単位であるRepairUnitを取得する.
     * 
     * @param sourceCode AST抽出対象のソースコード
     * @return 修正対象のASTノードとコンパイルユニットを持った修正単位であるRepairUnitのリスト
     */
    public List<RepairUnit> getAllRepairUnit(String sourceCode){
        List<RepairUnit> repairUnits = new ArrayList<RepairUnit>();
        for(int i = 0;/*終了条件なし*/; i++){
            CompilationUnit compilationUnit;   //RepairUnitごとに新しいインスタンスの生成
            compilationUnit = JavaParser.parse(sourceCode);
            // なくなるまで順にASTノードを取り出す
            try {
                Node node = AstGenerator.findByLevelOrderIndex(compilationUnit.findRootNode(), i).orElseThrow(); 
                repairUnits.add(new RepairUnit(node, i, compilationUnit)); 
            } catch (NoSuchElementException e) {
                return repairUnits;
            }
        }
    }
}

