package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import jp.posl.jprophet.NodeUtility;

import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;


/**
 * 対象ステートメント中の変数を別のもので置き換える操作を行う
 */
public class VariableReplacementOperation implements AstOperation {
    /**
     * 変数の置換操作を行い修正パッチ候補を生成する
     * 代入文の右辺の値と，メソッド呼び出しの引数を対象に，クラスのメンバ変数及びメソッドのローカル変数，仮引数で置換を行う
     * 一つの修正パッチ候補につき一箇所の置換
     * @return 生成された修正後のCompilationUnitのリスト
     */
    public List<CompilationUnit> exec(Node targetNode) {
        final List<String> fieldNames = this.collectFieldNames(targetNode);
        final List<String> localVarNames = this.collectLocalVarNames(targetNode);
        final List<String> parameterNames = this.collectParameterNames(targetNode);

        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

        Function<String, Expression> constructField = fieldName -> new FieldAccessExpr(new ThisExpr(), fieldName);
        candidates.addAll(this.replaceAssignExprAndArgsWith(targetNode, constructField, fieldNames));

        Function<String, Expression> constructVar = varName -> new NameExpr(varName);
        candidates.addAll(this.replaceAssignExprAndArgsWith(targetNode, constructVar, localVarNames));
        candidates.addAll(this.replaceAssignExprAndArgsWith(targetNode, constructVar, parameterNames));

        return candidates;
    }

    /**
     * 修正対象のステートメントが属するクラスのフィールドを集める   
     * @param node 修正対象 
     * @return フィールド名のリスト
     */
    private List<String> collectFieldNames(Node node){
        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> fields = collector.collectFileds(node);
        final List<String> fieldNames = fields.stream()
            .map(field -> field.getName().asString())
            .collect(Collectors.toList());
        return fieldNames;
    }

    /**
     * 修正対象のステートメントが書かれているメソッド中のローカル変数を集める 
     * @param node 修正対象 
     * @return ローカル変数名のリスト
     */
    private List<String> collectLocalVarNames(Node node){
        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> localVars = collector.collectLocalVarsDeclared(node);
        final List<String> localVarNames = localVars.stream()
            .map(localVar -> localVar.getName().asString())
            .collect(Collectors.toList());
        return localVarNames;
    }

    /**
     * 修正対象のステートメントが書かれているメソッドの仮引数を集める 
     * @param node 修正対象 
     * @return 仮引数の変数名のリスト
     */
    private List<String> collectParameterNames(Node node){
        final DeclarationCollector collector = new DeclarationCollector();
        final List<Parameter> parameters = collector.collectParameters(node);
        final List<String> parameterNames = parameters.stream()
            .map(localVar -> localVar.getName().asString())
            .collect(Collectors.toList());
        return parameterNames;
    }

    /**
     * 代入式の右辺とメソッド呼び出しの実引数を置換する
     * 
     * @param node 置換対象
     * @param constructVar 変数名からExpressionノードを作成する関数
     * @param varNames 置換先の変数名のリスト
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<CompilationUnit> replaceAssignExprAndArgsWith(Node node, Function<String, Expression> constructVar, List<String> varNames){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

        candidates.addAll(this.replaceAssignExprWith(node, varNames, constructVar));
        candidates.addAll(this.replaceArgsWith(node, varNames, constructVar));

        return candidates;
    }

    /**
     * 代入文における右辺の変数を置換する 
     * @param node 置換対象 
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<CompilationUnit> replaceAssignExprWith(Node node, List<String> varNames, Function<String, Expression> constructExpr){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

        if (node instanceof AssignExpr) {
            for(String varName: varNames){
                Expression originalAssignedValue = ((AssignExpr)node).getValue();
                String originalAssignedValueName; 
                try {
                    originalAssignedValueName = originalAssignedValue.findFirst(SimpleName.class).orElseThrow().asString();
                } catch (NoSuchElementException e) {
                    originalAssignedValueName = originalAssignedValue.toString();
                }
                if(originalAssignedValueName.equals(varName)){
                    continue;
                }
                Node newCandidate = NodeUtility.deepCopyByReparse(node);
                Optional<Node> replacedCandidate = NodeUtility.replaceNode(constructExpr.apply(varName), ((AssignExpr)newCandidate).getValue());
                try{
                    candidates.add(replacedCandidate.orElseThrow().findCompilationUnit().orElseThrow());
                } catch (NoSuchElementException e){}
                
            }
        }

        return candidates;        
    }

    /**
     * メソッド呼び出しの引数における変数の置換を行う 
     * @param node 置換対象 
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<CompilationUnit> replaceArgsWith(Node node, List<String> varNames, Function<String, Expression> constructExpr){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

        if (node instanceof MethodCallExpr){
            final int argc = ((MethodCallExpr)(node)).getArguments().size(); 
            for(String varName: varNames){
                for(int i = 0; i < argc; i++){
                    String originalArgValue = ((MethodCallExpr)node).getArgument(i).toString();
                    if(originalArgValue.equals(varName)){
                        continue;
                    }
                    Node newCandidate = NodeUtility.deepCopyByReparse(node);
                    MethodCallExpr methodCallExpr = (MethodCallExpr)newCandidate;
                    Optional<Node> replacedCandidate = NodeUtility.replaceNode(constructExpr.apply(varName), methodCallExpr.getArgument(i));
                    try{
                        candidates.add(replacedCandidate.orElseThrow().findCompilationUnit().orElseThrow());
                    } catch (NoSuchElementException e){}
                }
            }
        }

        return candidates; 
    }
}