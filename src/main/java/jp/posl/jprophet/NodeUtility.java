package jp.posl.jprophet;

import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
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
     * パースし直すことでdeepCopyする
     * @param node コピー元のインスタンス
     * @return パースし直したnode
     */
    public static Node deepCopyByReparse(Node node){
        CompilationUnit compilationUnit = node.findCompilationUnit().orElseThrow();
        LexicalPreservingPrinter.setup(compilationUnit);
        CompilationUnit newCu = JavaParser.parse(LexicalPreservingPrinter.print(compilationUnit));
        List<Node> nodes = NodeUtility.getAllDescendantNodes(newCu);
        Node newNode = nodes.stream().filter(n -> {
            return n.equals(node) && n.getRange().equals(node.getRange());
        }).findFirst().orElseThrow();
        return newNode;
    }


    /**
     * 1つのノードを2つのノードの間に入れる(行の最初や最後に入れることはできない)
     * beforeNodeの最後からafterNodeの初めまでのトークンをそのままinsertNodeの後にコピーする
     * @param insertNode 挿入するノード
     * @param beforeNode 挿入するノードの前のノード
     * @param afterNode 挿入するノードの後ろのノード
     * @return 挿入したノード
     */
    public static Node insertNode(Node insertNode, Node beforeNode, Node afterNode){
        Node copiedAfterNode = NodeUtility.deepCopyByReparse(afterNode);

        JavaToken beginTokenOfAfter = copiedAfterNode.getTokenRange().orElseThrow().getBegin();
        JavaToken insertToken = insertNode.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfInsert = insertNode.getTokenRange().orElseThrow().getEnd();
        final JavaToken originalBeginTokenOfAfter = afterNode.getTokenRange().orElseThrow().getBegin();

        final Range beginRangeOfAfter = afterNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        final int beginLineOfAfter = beginRangeOfAfter.begin.line;


        while (true){
            //deepCopyしているはずがオリジナルのノードも変更されてしまう
            //cloneはTokenRangeまでコピーしない?
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));
            if (insertToken.getRange().equals(endTokenOfInsert.getRange())){
                break;
            }
            insertToken = insertToken.getNextToken().orElseThrow();
        }

        insertToken = beforeNode.getTokenRange().orElseThrow().getEnd().getNextToken().orElseThrow();
        
        while (true){
            if (insertToken.getRange().equals(originalBeginTokenOfAfter.getRange())){
                break;
            }
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));
            
            insertToken = insertToken.getNextToken().orElseThrow();
        }
        
        CompilationUnit compilationUnit = copiedAfterNode.findCompilationUnit().orElseThrow();
        CompilationUnit parsedCompilationUnit = NodeUtility.reparseCompilationUnit(compilationUnit);
        Node copiedInsertNode = NodeUtility.findNodeInCompilationUnit(parsedCompilationUnit, insertNode, beginLineOfAfter);
        return copiedInsertNode;
    }

    /**
     * 1つのノードをあるノードの前に入れる(行の最初や最後に入れることはできない)
     * afterNodeの最初から後ろを見て改行コード\nまでをinsertNodeの後にコピーする
     * 1行の中にノードを挿入することはできない(final int a = 0; をfinal public int a = 0; にする等)
     * @param insertNode 挿入するノード
     * @param afterNode 挿入するノードの後ろのノード
     * @return 挿入したノード
     */
    public static Node insertNodeWithNewLine(Node insertNode, Node afterNode){
        Node copiedAfterNode = NodeUtility.deepCopyByReparse(afterNode);

        JavaToken beginTokenOfAfter = copiedAfterNode.getTokenRange().orElseThrow().getBegin();
        JavaToken insertToken = insertNode.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfInsert = insertNode.getTokenRange().orElseThrow().getEnd();
        final JavaToken originalBeginTokenOfAfter = afterNode.getTokenRange().orElseThrow().getBegin();

        final Range beginRangeOfAfter = afterNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        final int beginLineOfAfter = beginRangeOfAfter.begin.line;


        while (true){
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));
            if (insertToken.getRange().equals(endTokenOfInsert.getRange())){
                break;
            }
            insertToken = insertToken.getNextToken().orElseThrow();
        }

        insertToken = afterNode.getTokenRange().orElseThrow().getBegin();

        while (!insertToken.getText().equals("\n")){
            insertToken = insertToken.getPreviousToken().orElseThrow();
        }

        while (true){
            if (insertToken.getRange().equals(originalBeginTokenOfAfter.getRange())){
                break;
            }
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));
            
            insertToken = insertToken.getNextToken().orElseThrow();
        }
        
        CompilationUnit compilationUnit = copiedAfterNode.findCompilationUnit().orElseThrow();
        CompilationUnit parsedCompilationUnit = NodeUtility.reparseCompilationUnit(compilationUnit);
        Node copiedInsertNode = NodeUtility.findNodeInCompilationUnit(parsedCompilationUnit, insertNode, beginLineOfAfter);
        return copiedInsertNode;
    }

    /**
     * ノードを置換する
     * @param reolaceNode 置換された後のノード
     * @param originalNode 置換される前のノード
     * @return 置換後のASTノード
     */
    public static Node replaceNode(Node replaceNode, Node targetNode){
        Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);
        JavaToken beginTokenOfTarget = copiedTargetNode.getTokenRange().orElseThrow().getBegin();
        JavaToken replaceToken = replaceNode.getTokenRange().orElseThrow().getBegin();

        final JavaToken endTokenOfReplace = replaceNode.getTokenRange().orElseThrow().getEnd();
        final JavaToken endTokenOfTarget = targetNode.getTokenRange().orElseThrow().getEnd();
        final Range beginRangeOfTarget = targetNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();
        final int beginLineOfTarget = beginRangeOfTarget.begin.line;

        while (true){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, replaceToken.getKind(), replaceToken.getText(), null, null));
            if (replaceToken.getRange().equals(endTokenOfReplace.getRange())){
                break;
            }
            replaceToken = replaceToken.getNextToken().orElseThrow();
        }

        JavaToken deleteToken = beginTokenOfTarget.getNextToken().orElseThrow();

        while (true){
            if (beginTokenOfTarget.getRange().equals(endTokenOfTarget.getRange())){
                beginTokenOfTarget.deleteToken();
                break;
            }
            if (deleteToken.getRange().equals(endTokenOfTarget.getRange())){
                deleteToken.deleteToken();
                beginTokenOfTarget.deleteToken();
                break;
            }
            deleteToken.deleteToken();
            deleteToken = beginTokenOfTarget.getNextToken().orElseThrow();
        }

        CompilationUnit compilationUnit = copiedTargetNode.findCompilationUnit().orElseThrow();
        CompilationUnit parsedCompilationUnit = NodeUtility.reparseCompilationUnit(compilationUnit);
        Node copiedInsertNode = NodeUtility.findNodeInCompilationUnit(parsedCompilationUnit, replaceNode, beginLineOfTarget);
        return copiedInsertNode;
    }

    /**
     * compilationUnitからノードを探す
     * @param compilationUnit パースし直した後のcompilationUnit
     * @param node パースし直す前の探したいノード
     * @param beginLine 探したいノードの最初の行番号
     * @return 見つけたノード
     */
    public static Node findNodeInCompilationUnit(CompilationUnit compilationUnit, Node node, int beginLine){
        List<Node> nodes = NodeUtility.getAllDescendantNodes(compilationUnit);
        Node newNode = nodes.stream().filter(n -> {
            return n.equals(node) && n.getRange().orElseThrow().begin.line == beginLine;
        }).findFirst().orElseThrow();
        return newNode;
    }

    /**
     * LexicalPreserverPrinterを用いてcompilationUnitをパースし直す
     * @param compilationUnit パースし直すcompilationUnit
     * @return パースし直したcompilationUnit
     */
    public static CompilationUnit reparseCompilationUnit(CompilationUnit compilationUnit){
        LexicalPreservingPrinter.setup(compilationUnit);
        String source = LexicalPreservingPrinter.print(compilationUnit);
        return JavaParser.parse(source);
    }
}

