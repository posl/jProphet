package jp.posl.jprophet;

import com.github.javaparser.ast.Node;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class NodeUtility {

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
        CompilationUnit cu = node.findCompilationUnit().get();

        CompilationUnit newCu = cu.clone();
        List<Node> nodes = NodeUtility.getAllDescendantNodes(newCu);
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
        List<Node> descendantNodes = NodeUtility.getAllDescendantNodes(parentNode); 
        List<Node> copiedDescendantNodes = descendantNodes.stream()
            .map(NodeUtility::deepCopy)
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
        return NodeUtility.getAllCopiedDescendantNodes(JavaParser.parse(sourceCode));
    }


    /**
     * 1つのノードを2つのノードの間に入れる
     * @param insertNode
     * @param beforeNode
     * @param afterNode
     * @return ノード挿入後のASTノード
     */
    public static Optional<Node> insertToken(Node insertNode, Node beforeNode, Node afterNode){
        return Optional.empty();
    }

    /**
     * ノードを置換する
     * @param reolaceNode
     * @param originalNode
     * @return 置換後のASTノード
     */
    public static Optional<Node> replaceNode(Node reolaceNode, Node originalNode){
        return Optional.empty();
    }
}

