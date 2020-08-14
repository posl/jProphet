package jp.posl.jprophet;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


public final class NodeUtility {

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
     * Nodeの木構造に含まれるの全てのノードを深さ優先順に取得する
     * @param root ノードを取得したい木構造の根ノード
     * @return 子孫ノードのリスト
     */
    public static List<Node> getAllNodesInDepthFirstOrder(Node root) {
        List<Node> descendantNodes = new ArrayList<Node>();
        descendantNodes.add(root);
        root.getChildNodes().stream()
            .map(childNode -> getAllNodesInDepthFirstOrder(childNode))
            .forEach(descendantNodes::addAll);
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
    public static Node deepCopyByReparse(Node node) {
        CompilationUnit compilationUnit = node.findCompilationUnit().orElseThrow();

        String source = lexicalPreservingPrint(compilationUnit);
        CompilationUnit newCu = JavaParser.parse(source);
        List<Node> nodes = NodeUtility.getAllDescendantNodes(newCu);
        Node newNode = nodes.stream().filter(n -> {
            return n.equals(node) && n.getRange().equals(node.getRange());
        }).findFirst().orElseThrow();

        return newNode;
    }


    /**
     * previousNodeとnextNodeの間にnodeToInsertを挿入する
     * nodeToInsertとnextNodeの間の空白の数,インデント,改行の数は,previousNodeとnextNodeの間のそれらと等しくなる
     * 行の先頭(末尾)にノードを挿入しようとすると,一つ上の行(下の行)に挿入される
     * 複数行のノードを挿入しようとするとインデントがおかしくなる場合がある
     * 挿入後のコードがパースできない場合nullを返す
     * @param nodeToInsert 挿入するノード
     * @param previousNode 挿入するノードの前のノード
     * @param nextNode 挿入するノードの後ろのノード
     * @return 挿入したノード
     */
    public static Optional<Node> insertNodeBetweenNodes (Node nodeToInsert, Node previousNode, Node nextNode){
        Node copiedNextNode = NodeUtility.deepCopyByReparse(nextNode);
        Node nodeWithTokenToInsert;
        if (!nodeToInsert.getTokenRange().isPresent()){
            try {
                nodeWithTokenToInsert = NodeUtility.initTokenRange(nodeToInsert).orElseThrow();
            } catch (NoSuchElementException e){
                return Optional.empty();
            }
        }else{
            nodeWithTokenToInsert = nodeToInsert;
        }

        JavaToken beginTokenOfNext = copiedNextNode.getTokenRange().orElseThrow().getBegin();
        JavaToken tokenToInsert= nodeWithTokenToInsert.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfInsert = nodeWithTokenToInsert.getTokenRange().orElseThrow().getEnd();
        final JavaToken originalBeginTokenOfNext = nextNode.getTokenRange().orElseThrow().getBegin();

        final Range beginRangeOfNext = nextNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfNext.insert(new JavaToken(beginRangeOfNext, tokenToInsert.getKind(), tokenToInsert.getText(), null, null));
            if (tokenToInsert.getRange().equals(endTokenOfInsert.getRange())) break;
            tokenToInsert = tokenToInsert.getNextToken().orElseThrow();
        }

        tokenToInsert = previousNode.getTokenRange().orElseThrow().getEnd().getNextToken().orElseThrow();
        
        while (!tokenToInsert.getRange().equals(originalBeginTokenOfNext.getRange())){
            beginTokenOfNext.insert(new JavaToken(beginRangeOfNext, tokenToInsert.getKind(), tokenToInsert.getText(), null, null));
            tokenToInsert = tokenToInsert.getNextToken().orElseThrow();
        }
        
        final CompilationUnit compilationUnit = copiedNextNode.findCompilationUnit().orElseThrow();
        return NodeUtility.reparseCompilationUnit(compilationUnit)
            .map(cu -> NodeUtility.findNodeInCompilationUnitByBeginRange(cu, nodeWithTokenToInsert, beginRangeOfNext));
    }

    /**
     * targetNodeの直前の行にnodeToInsertを入れる
     * nodeToInsertのインデントはtargetNodeのインデントと同じ
     * Statementノードよりも小さい単位のノードを渡すとnullが返される
     * 挿入後のコードがパースできない場合nullを返す
     * @param nodeToInsert 挿入するノード
     * @param targetNode 挿入するノードの後ろのノード
     * @return 挿入したノード
     */
    public static Optional<Node> insertNodeWithNewLine(Node nodeToInsert, Node targetNode){
        Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);
        Node nodeWithTokenToInsert;
        try {
            nodeWithTokenToInsert = NodeUtility.initTokenRange(nodeToInsert).orElseThrow();
        } catch (NoSuchElementException e){
            return Optional.empty();
        }

        JavaToken beginTokenOfTarget = copiedTargetNode.getTokenRange().orElseThrow().getBegin();
        JavaToken tokenToInsert = nodeWithTokenToInsert.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfInsert = nodeWithTokenToInsert.getTokenRange().orElseThrow().getEnd();
        final JavaToken originalBeginTokenOfTarget = targetNode.getTokenRange().orElseThrow().getBegin();

        final Range beginRangeOfTarget = targetNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, tokenToInsert.getKind(), tokenToInsert.getText(), null, null));
            if (tokenToInsert.getRange().equals(endTokenOfInsert.getRange())) break;

            //複数行を挿入する時のインデントの調節
            if (tokenToInsert.getKind() == JavaToken.Kind.UNIX_EOL.getKind())
                NodeUtility.adjustmentIndent(targetNode, beginTokenOfTarget, beginRangeOfTarget);
            
            tokenToInsert = tokenToInsert.getNextToken().orElseThrow();
        }

        tokenToInsert = targetNode.getTokenRange().orElseThrow().getBegin();

        while (!tokenToInsert.getText().equals("\n")){
            tokenToInsert = tokenToInsert.getPreviousToken().orElseThrow();
        }

        while (!tokenToInsert.getRange().equals(originalBeginTokenOfTarget.getRange())){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, tokenToInsert.getKind(), tokenToInsert.getText(), null, null));   
            tokenToInsert = tokenToInsert.getNextToken().orElseThrow();
        }
        
        final CompilationUnit compilationUnit = copiedTargetNode.findCompilationUnit().orElseThrow();
        return NodeUtility.reparseCompilationUnit(compilationUnit)
            .map(cu -> findNodeInCompilationUnitByBeginRange(cu, nodeWithTokenToInsert, beginRangeOfTarget));
    }

    /**
     * nodeToInsertをtargetNodeの直前に挿入する
     * Statementノードよりも小さい単位のノードを挿入する際に利用
     * x = 0; を int x = 0; にするなど
     * nodeToInsertとtargetNodeの間には空白が1つ入る
     * 挿入後のコードがパースできない場合nullを返す
     * @param nodeToInsert 挿入するノード
     * @param targetNode 挿入するノードの後ろのノード
     * @return 挿入したノード
     */
    public static Optional<Node> insertNodeInOneLine(Node nodeToInsert, Node targetNode){
        Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);
        Node nodeWithTokenToInsert;
        if (!nodeToInsert.getTokenRange().isPresent()){
            try {
                nodeWithTokenToInsert = NodeUtility.initTokenRange(nodeToInsert).orElseThrow();
            } catch (NoSuchElementException e){
                return Optional.empty();
            }
        } else {
            nodeWithTokenToInsert = NodeUtility.deepCopyByReparse(nodeToInsert);
        }

        JavaToken beginTokenOfTarget = copiedTargetNode.getTokenRange().orElseThrow().getBegin();
        JavaToken tokenToInsert = nodeWithTokenToInsert.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfInsert = nodeWithTokenToInsert.getTokenRange().orElseThrow().getEnd();

        final Range beginRangeOfTarget = targetNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, tokenToInsert.getKind(), tokenToInsert.getText(), null, null));
            if (tokenToInsert.getRange().equals(endTokenOfInsert.getRange())){
                beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, JavaToken.Kind.SPACE.getKind(), " ", null, null));
                break;
            }
            tokenToInsert = tokenToInsert.getNextToken().orElseThrow();
        }
        
        final CompilationUnit compilationUnit = copiedTargetNode.findCompilationUnit().orElseThrow();
        return NodeUtility.reparseCompilationUnit(compilationUnit)
            .map(cu -> NodeUtility.findNodeInCompilationUnitByBeginRange(cu, nodeWithTokenToInsert, beginRangeOfTarget));
    }

    /**
     * targetNodeをnodeToReplaceに置換する
     * 置換後のコードがパースできない場合nullを返す
     * 置換前のコメントは削除される
     * @param nodeToReplaceWith 置換された後のノード(TokenRange等がnullでもOK)
     * @param targetNode 置換される前のノード
     * @return 置換後のASTノード
     */
    public static Optional<Node> replaceNode(Node nodeToReplaceWith, Node targetNode) {
        Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);
        Node nodeWithTokenToReplaceWith;
        try {
            nodeWithTokenToReplaceWith = NodeUtility.initTokenRange(nodeToReplaceWith).orElseThrow();
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }

        JavaToken beginTokenOfTarget = copiedTargetNode.getTokenRange().orElseThrow().getBegin();
        JavaToken tokenToReplaceWith = nodeWithTokenToReplaceWith.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfReplace = nodeWithTokenToReplaceWith.getTokenRange().orElseThrow().getEnd();
        JavaToken endTokenOfTarget = targetNode.getTokenRange().orElseThrow().getEnd();

        // コメントが後ろに付いているノードに対して置換範囲のTokenをコメントの部分まで広げる
        // よってコメントごと置換されるのでコメントは消える
        if (targetNode.getComment().isPresent()) {
            endTokenOfTarget = targetNode.getComment().get().getTokenRange().orElseThrow().getEnd();
            Range range = endTokenOfTarget.getRange().orElseThrow();
            Range newRange = new Range(new Position(range.begin.line, range.end.column + 1), new Position(range.begin.line, range.end.column + 1));
            JavaToken endTokenOfCopiedTarget = copiedTargetNode.getComment().get().getTokenRange().orElseThrow().getEnd();
            endTokenOfCopiedTarget.insertAfter(new JavaToken(newRange, JavaToken.Kind.UNIX_EOL.getKind(), "\n", null, null));
        }

        final Range beginRangeOfTarget = targetNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, tokenToReplaceWith.getKind(), tokenToReplaceWith.getText(), null, null));
            if (tokenToReplaceWith.getRange().equals(endTokenOfReplace.getRange())) break;

            //複数行置換する時のインデントの調整
            if (tokenToReplaceWith.getKind() == JavaToken.Kind.UNIX_EOL.getKind())
                NodeUtility.adjustmentIndent(targetNode, beginTokenOfTarget, beginRangeOfTarget);
            
            tokenToReplaceWith = tokenToReplaceWith.getNextToken().orElseThrow();
        }

        JavaToken tokenToDelete = beginTokenOfTarget.getNextToken().orElseThrow();

        while (true){
            if (beginTokenOfTarget.getRange().equals(endTokenOfTarget.getRange())){
                beginTokenOfTarget.deleteToken();
                break;
            }
            if (tokenToDelete.getRange().equals(endTokenOfTarget.getRange())){
                tokenToDelete.deleteToken();
                beginTokenOfTarget.deleteToken();
                break;
            }
            tokenToDelete.deleteToken();
            tokenToDelete = beginTokenOfTarget.getNextToken().orElseThrow();
        }

        final CompilationUnit compilationUnit = copiedTargetNode.findCompilationUnit().orElseThrow();
        return NodeUtility.reparseCompilationUnit(compilationUnit)
            .map(cu -> NodeUtility.findNodeInCompilationUnitByBeginRange(cu, nodeWithTokenToReplaceWith, beginRangeOfTarget));
        
    }

    /**
     * tokenRangeを,targetNodeの直前の行に挿入
     * tokenRangeの先頭の1つ前のトークンと,末尾の1つ後ろのトークンがnullでないとエラーが起きる
     * @param tokenRange beginの一つ前と,endの1つ後がnullであるtokenRange
     * @param targetNode 
     * @return
     */
    public static Optional<CompilationUnit> insertTokenWithNewLine(TokenRange tokenRange, Node targetNode){
        Node copiedTargetNode = NodeUtility.deepCopyByReparse(targetNode);

        JavaToken beginTokenOfTarget = copiedTargetNode.getTokenRange().orElseThrow().getBegin();
        JavaToken tokenToInsert = tokenRange.getBegin();
        final JavaToken originalBeginTokenOfTarget = targetNode.getTokenRange().orElseThrow().getBegin();

        final Range beginRangeOfTarget = targetNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, tokenToInsert.getKind(), tokenToInsert.getText(), null, null));
            try{
                tokenToInsert.getNextToken().orElseThrow();
            }catch (NoSuchElementException e){
                break;
            }
            tokenToInsert = tokenToInsert.getNextToken().orElseThrow();
        }

        tokenToInsert = targetNode.getTokenRange().orElseThrow().getBegin();

        while (!tokenToInsert.getText().equals("\n")){
            tokenToInsert = tokenToInsert.getPreviousToken().orElseThrow();
        }

        while (!tokenToInsert.getRange().equals(originalBeginTokenOfTarget.getRange())){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, tokenToInsert.getKind(), tokenToInsert.getText(), null, null));   
            tokenToInsert = tokenToInsert.getNextToken().orElseThrow();
        }
        
        final CompilationUnit compilationUnit = copiedTargetNode.findCompilationUnit().orElseThrow();
        return NodeUtility.reparseCompilationUnit(compilationUnit);
    }

    /**
     * compilationUnitから行単位でノードを探す
     * @param compilationUnit パースし直した後のcompilationUnit
     * @param node パースし直す前の探したいノード
     * @param range 探したいノードのrange
     * @return 見つけたノード
     */
    public static Node findNodeInCompilationUnitByBeginRange(CompilationUnit compilationUnit, Node node, Range range) {
        List<Node> nodes = NodeUtility.getAllDescendantNodes(compilationUnit);
        Node newNode = nodes.stream().filter(n -> {
            return n.equals(node) && n.getRange().orElseThrow().begin.equals(range.begin);
        }).findFirst().orElseThrow();
        return newNode;
    }

    /**
     * LexicalPreserverPrinterを用いてcompilationUnitをパースし直す
     * @param compilationUnit パースし直すcompilationUnit
     * @return パースし直したcompilationUnit
     */
    public static Optional<CompilationUnit> reparseCompilationUnit(CompilationUnit compilationUnit){
        String source = lexicalPreservingPrint(compilationUnit);
        try {
            return Optional.of(JavaParser.parse(source));
        } catch (ParseProblemException e){
            return Optional.empty();
        }
    }

    /**
     * 簡易版のlexicalPreservingPrinter
     * Tokenを元にソースコードを生成する
     * @param node ノード
     * @return ソースコード
     */
    public static String lexicalPreservingPrint(Node node){
        final JavaToken begin = node.getTokenRange().orElseThrow().getBegin();
        final JavaToken end = node.getTokenRange().orElseThrow().getEnd();
        JavaToken current = begin;
        StringBuilder sb = new StringBuilder();
        while(!current.equals(end)) {
            sb.append(current.getText());
            current = current.getNextToken().orElseThrow();
        }
        sb.append(current.getText());
        return sb.toString();
    }

    /**
     * StatementまたはExpression型のノードのtoStringをパースすることで
     * TokenRangeを設定する
     * @param node パースしたいのノード(StatementかExpression)
     * @return パースしたノード
     */
    public static Optional<Node> initTokenRange(Node node){
        node.removeComment();
        Node parsedNode;
        if (node instanceof Statement){
            parsedNode = JavaParser.parseStatement(node.toString());
        }else if (node instanceof Expression){
            parsedNode = JavaParser.parseExpression(node.toString());
        }else{
            return Optional.empty();
        }
        return Optional.of(parsedNode);
    }


    public static Optional<Node> parseNodeWithPointer(Node targetNode, Node descendantNode) {
        Node parsedNode;
        if (targetNode instanceof Statement){
            parsedNode = JavaParser.parseStatement(targetNode.toString());
        }else if (targetNode instanceof Expression){
            parsedNode = JavaParser.parseExpression(targetNode.toString());
        }else{
            return Optional.empty();
        }
        List<Node> nodes = NodeUtility.getAllDescendantNodes(parsedNode);
        Node newNode = nodes.stream().filter(n -> {
            return n.equals(descendantNode) && n.getRange().equals(descendantNode.getRange());
        }).findFirst().orElseThrow();
        return Optional.of(newNode);
    }

    public static Optional<Node> replaceNodeWithoutCompilationUnit(Node targetNode, Node nodeToReplaceWith) {
        Node nodeWithTokenToReplaceWith;
        try {
            nodeWithTokenToReplaceWith = NodeUtility.initTokenRange(nodeToReplaceWith).orElseThrow();
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }

        Node rootNode = targetNode.findRootNode();

        JavaToken beginTokenOfTarget = targetNode.getTokenRange().orElseThrow().getBegin();
        JavaToken tokenToReplaceWith = nodeWithTokenToReplaceWith.getTokenRange().orElseThrow().getBegin();
        final JavaToken endTokenOfReplace = nodeWithTokenToReplaceWith.getTokenRange().orElseThrow().getEnd();
        JavaToken endTokenOfTarget = targetNode.getTokenRange().orElseThrow().getEnd();

        // コメントが後ろに付いているノードに対して置換範囲のTokenをコメントの部分まで広げる
        // よってコメントごと置換されるのでコメントは消える
        if (targetNode.getComment().isPresent()) {
            endTokenOfTarget = targetNode.getComment().get().getTokenRange().orElseThrow().getEnd();
            Range range = endTokenOfTarget.getRange().orElseThrow();
            Range newRange = new Range(new Position(range.begin.line, range.end.column + 1), new Position(range.begin.line, range.end.column + 1));
            JavaToken endTokenOfCopiedTarget = targetNode.getComment().get().getTokenRange().orElseThrow().getEnd();
            endTokenOfCopiedTarget.insertAfter(new JavaToken(newRange, JavaToken.Kind.UNIX_EOL.getKind(), "\n", null, null));
        }

        final Range beginRangeOfTarget = targetNode.getTokenRange().orElseThrow().getBegin().getRange().orElseThrow();

        while (true){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, tokenToReplaceWith.getKind(), tokenToReplaceWith.getText(), null, null));
            if (tokenToReplaceWith.getRange().equals(endTokenOfReplace.getRange())) break;

            //複数行置換する時のインデントの調整
            if (tokenToReplaceWith.getKind() == JavaToken.Kind.UNIX_EOL.getKind())
                NodeUtility.adjustmentIndent(targetNode, beginTokenOfTarget, beginRangeOfTarget);
            
            tokenToReplaceWith = tokenToReplaceWith.getNextToken().orElseThrow();
        }

        JavaToken tokenToDelete = beginTokenOfTarget;

        while (true){
            if (beginTokenOfTarget.getRange().equals(endTokenOfTarget.getRange())){
                beginTokenOfTarget.deleteToken();
                break;
            }
            if (tokenToDelete.getRange().equals(endTokenOfTarget.getRange())){
                tokenToDelete.deleteToken();
                break;
            }
            tokenToDelete.deleteToken();
            tokenToDelete = tokenToDelete.getNextToken().orElseThrow();
        }

        JavaToken begin = beginTokenOfTarget.getPreviousToken().orElseThrow();
        JavaToken end = beginTokenOfTarget;
        while (true) {
            if (!begin.getPreviousToken().isPresent())
                break;
            begin = begin.getPreviousToken().orElseThrow();
        }
        while (true) {
            if (!end.getNextToken().isPresent())
                break;
            end = end.getNextToken().orElseThrow();
        }
        
        JavaToken current = begin;
        StringBuilder sb = new StringBuilder();
        while(!current.equals(end)) {
            sb.append(current.getText());
            current = current.getNextToken().orElseThrow();
        }
        sb.append(current.getText());
        String source = sb.toString();

        Node parsedNode;
        if (rootNode instanceof Statement){
            parsedNode = JavaParser.parseStatement(source);
        }else if (rootNode instanceof Expression){
            parsedNode = JavaParser.parseExpression(source);
        }else{
            return Optional.empty();
        }
        return Optional.of(parsedNode);
    }


    /**
     * インデントの調節をする
     * @param originalNode パースし直す前のtargetNode
     * @param beginTokenOfTarget targetNodeの先頭のトークン
     * @param beginRangeOfTarget targetNodeの先頭のトークンのレンジ
     */
    private static void adjustmentIndent(Node originalNode, JavaToken beginTokenOfTarget, Range beginRangeOfTarget){
        JavaToken spaceToken = originalNode.getTokenRange().orElseThrow().getBegin();

        while (!spaceToken.getText().equals("\n")){
        spaceToken = spaceToken.getPreviousToken().orElseThrow();
        }
        spaceToken = spaceToken.getNextToken().orElseThrow();
        while (!spaceToken.getRange().equals(originalNode.getTokenRange().orElseThrow().getBegin().getRange())){
            beginTokenOfTarget.insert(new JavaToken(beginRangeOfTarget, spaceToken.getKind(), spaceToken.getText(), null, null));   
            spaceToken = spaceToken.getNextToken().orElseThrow();
        }
    }
}