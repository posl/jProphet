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
     * previousNodeとnextNodeの間にnodeToInsertを挿入する
     * nodeToInsertとnextNodeの間の空白の数や,インデント,改行の数は,previousNodeとnextNodeの間のそれらと等しくなる
     * @param nodeToInsert 挿入するノード
     * @param previousNode 挿入するノードの前のノード
     * @param nextNode 挿入するノードの後ろのノード
     * @return 挿入したノード
     */
    public static Node insertNodeBetweenNodes (Node nodeToInsert, Node previousNode, Node nextNode) throws NoSuchElementException{
        Node copiedAfterNode = NodeUtility.deepCopyByReparse(nextNode);

        JavaToken beginTokenOfAfter = copiedAfterNode.getTokenRange().orElseThrow().getBegin();
        JavaToken insertToken = nodeToInsert.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfInsert = nodeToInsert.getTokenRange().orElseThrow().getEnd();
        final JavaToken originalBeginTokenOfAfter = nextNode.getTokenRange().orElseThrow().getBegin();

        final Range beginRangeOfAfter = nextNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));
            if (insertToken.getRange().equals(endTokenOfInsert.getRange())) break;
            insertToken = insertToken.getNextToken().orElseThrow();
        }

        insertToken = previousNode.getTokenRange().orElseThrow().getEnd().getNextToken().orElseThrow();
        
        while (!insertToken.getRange().equals(originalBeginTokenOfAfter.getRange())){
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));
            insertToken = insertToken.getNextToken().orElseThrow();
        }
        
        CompilationUnit compilationUnit = copiedAfterNode.findCompilationUnit().orElseThrow();
        CompilationUnit parsedCompilationUnit = NodeUtility.reparseCompilationUnit(compilationUnit);
        Node copiedInsertNode = NodeUtility.findNodeInCompilationUnitByLine(parsedCompilationUnit, nodeToInsert, beginRangeOfAfter);
        return copiedInsertNode;
    }

    /**
     * targetNodeの直前の行にnodeToInsertを入れる
     * nodeToInsertのインデントはtargetNodeのインデントと同じ
     * Statementノードよりも小さい単位のノードを渡すと正しく挿入されない
     * @param nodeToInsert 挿入するノード
     * @param targetNode 挿入するノードの後ろのノード
     * @return 挿入したノード
     */
    public static Node insertNodeWithNewLine(Node nodeToInsert, Node targetNode) throws NoSuchElementException{
        Node copiedAfterNode = NodeUtility.deepCopyByReparse(targetNode);

        JavaToken beginTokenOfAfter = copiedAfterNode.getTokenRange().orElseThrow().getBegin();
        JavaToken insertToken = nodeToInsert.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfInsert = nodeToInsert.getTokenRange().orElseThrow().getEnd();
        final JavaToken originalBeginTokenOfAfter = targetNode.getTokenRange().orElseThrow().getBegin();

        final Range beginRangeOfAfter = targetNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));
            if (insertToken.getRange().equals(endTokenOfInsert.getRange())) break;
            insertToken = insertToken.getNextToken().orElseThrow();
        }

        insertToken = targetNode.getTokenRange().orElseThrow().getBegin();

        while (!insertToken.getText().equals("\n")){
            insertToken = insertToken.getPreviousToken().orElseThrow();
        }

        while (!insertToken.getRange().equals(originalBeginTokenOfAfter.getRange())){
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));   
            insertToken = insertToken.getNextToken().orElseThrow();
        }
        
        CompilationUnit compilationUnit = copiedAfterNode.findCompilationUnit().orElseThrow();
        CompilationUnit parsedCompilationUnit = NodeUtility.reparseCompilationUnit(compilationUnit);
        Node copiedInsertNode = NodeUtility.findNodeInCompilationUnitByLine(parsedCompilationUnit, nodeToInsert, beginRangeOfAfter);
        return copiedInsertNode;
    }

    /**
     * nodeToInsertをtargetNodeの直前に挿入する
     * Statementノードよりも小さい単位のノードを挿入する際に利用
     * x = 0; を int x = 0; にするなど
     * @param nodeToInsert 挿入するノード
     * @param targetNode 挿入するノードの後ろのノード
     * @return 挿入したノード
     */
    public static Node insertNodeInOneLine(Node nodeToInsert, Node targetNode) throws NoSuchElementException{
        Node copiedAfterNode = NodeUtility.deepCopyByReparse(targetNode);

        JavaToken beginTokenOfAfter = copiedAfterNode.getTokenRange().orElseThrow().getBegin();
        JavaToken insertToken = nodeToInsert.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfInsert = nodeToInsert.getTokenRange().orElseThrow().getEnd();

        final Range beginRangeOfAfter = targetNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));
            if (insertToken.getRange().equals(endTokenOfInsert.getRange())){
                beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, JavaToken.Kind.SPACE.getKind(), " ", null, null));
                break;
            }
            insertToken = insertToken.getNextToken().orElseThrow();
        }
        
        CompilationUnit compilationUnit = copiedAfterNode.findCompilationUnit().orElseThrow();
        CompilationUnit parsedCompilationUnit = NodeUtility.reparseCompilationUnit(compilationUnit);
        Node copiedInsertNode = NodeUtility.findNodeInCompilationUnitByLine(parsedCompilationUnit, nodeToInsert, beginRangeOfAfter);
        return copiedInsertNode;
    }

    /**
     * ノードを置換する
     * @param reolaceNode 置換された後のノード
     * @param originalNode 置換される前のノード
     * @return 置換後のASTノード
     */
    public static Node replaceNode(Node replaceNode, Node targetNode) throws NoSuchElementException{
        Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);

        JavaToken beginTokenOfTarget = copiedTargetNode.getTokenRange().orElseThrow().getBegin();
        JavaToken replaceToken = replaceNode.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfReplace = replaceNode.getTokenRange().orElseThrow().getEnd();
        final JavaToken endTokenOfTarget = targetNode.getTokenRange().orElseThrow().getEnd();

        final Range beginRangeOfTarget = targetNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, replaceToken.getKind(), replaceToken.getText(), null, null));
            if (replaceToken.getRange().equals(endTokenOfReplace.getRange())) break;
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
        Node copiedInsertNode = NodeUtility.findNodeInCompilationUnitByLine(parsedCompilationUnit, replaceNode, beginRangeOfTarget);
        return copiedInsertNode;
    }

    /**
     * tokenRangeを,指定したノードの直前の行に挿入
     * tokenRangeのbeginの一つ前と,endの一つ後はnullでないといけ
     * @param tokenRange beginの一つ前と,endの1つ後がnullであるtokenRange
     * @param nextNode
     * @return
     */
    public static CompilationUnit insertTokenWithNewLine(TokenRange tokenRange, Node nextNode) throws NoSuchElementException{
        Node copiedAfterNode = NodeUtility.deepCopyByReparse(nextNode);

        JavaToken beginTokenOfAfter = copiedAfterNode.getTokenRange().orElseThrow().getBegin();
        JavaToken insertToken = tokenRange.getBegin();
        final JavaToken originalBeginTokenOfAfter = nextNode.getTokenRange().orElseThrow().getBegin();

        final Range beginRangeOfAfter = nextNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));
            try{
                insertToken.getNextToken().orElseThrow();
            }catch (NoSuchElementException e){
                break;
            }
            insertToken = insertToken.getNextToken().orElseThrow();
        }

        insertToken = nextNode.getTokenRange().orElseThrow().getBegin();

        while (!insertToken.getText().equals("\n")){
            insertToken = insertToken.getPreviousToken().orElseThrow();
        }

        while (!insertToken.getRange().equals(originalBeginTokenOfAfter.getRange())){
            beginTokenOfAfter.insert(new JavaToken(beginRangeOfAfter, insertToken.getKind(), insertToken.getText(), null, null));   
            insertToken = insertToken.getNextToken().orElseThrow();
        }
        
        CompilationUnit compilationUnit = copiedAfterNode.findCompilationUnit().orElseThrow();
        CompilationUnit parsedCompilationUnit = NodeUtility.reparseCompilationUnit(compilationUnit);
        return parsedCompilationUnit;
    }

    /**
     * compilationUnitから行単位でノードを探す
     * @param compilationUnit パースし直した後のcompilationUnit
     * @param node パースし直す前の探したいノード
     * @param beginLine 探したいノードの最初の行番号
     * @return 見つけたノード
     */
    public static Node findNodeInCompilationUnitByLine(CompilationUnit compilationUnit, Node node, Range range){
        List<Node> nodes = NodeUtility.getAllDescendantNodes(compilationUnit);
        Node newNode = nodes.stream().filter(n -> {
            return n.equals(node) && n.getRange().orElseThrow().begin.line == range.begin.line;
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