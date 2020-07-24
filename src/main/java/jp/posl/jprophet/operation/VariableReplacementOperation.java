package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import jp.posl.jprophet.patch.DiffWithType;

import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;


/**
 * 対象ステートメント中の変数を別のもので置き換える操作を行う
 */
public class VariableReplacementOperation implements AstOperation {
    /**
     * <h4>変数の置換操作を行い修正パッチ候補を生成する</h4>
     * <p>
     * 代入文の右辺の値と，メソッド呼び出しの引数を対象に，クラスのメンバ変数及びメソッドのローカル変数，仮引数で置換を行う
     * 一つの修正パッチ候補につき一箇所の置換
     * </p>
     * <p>MethodCallExprとAssignExprをtargetNodeとして受け取った場合に置換が行われる</p>
     * @return 生成された修正後のCompilationUnitのリスト
     */
    public List<DiffWithType> exec(Node targetNode) {
        final List<String> fieldNames = this.collectFieldNames(targetNode);
        final List<String> localVarNames = this.collectLocalVarNames(targetNode);
        final List<String> parameterNames = this.collectParameterNames(targetNode);

        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

        final Function<String, Expression> constructField = fieldName -> new FieldAccessExpr(new ThisExpr(), fieldName);
        candidates.addAll(this.replaceVariables(targetNode, constructField, fieldNames));

        final Function<String, Expression> constructVar = varName -> new NameExpr(varName);
        candidates.addAll(this.replaceVariables(targetNode, constructVar, localVarNames));
        candidates.addAll(this.replaceVariables(targetNode, constructVar, parameterNames));

        //return candidates;
        return new ArrayList<DiffWithType>();
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
     * @param targetNode 置換対象
     * @param constructVar 変数名からExpressionノードを作成する関数
     * @param varNames 置換先の変数名のリスト
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<CompilationUnit> replaceVariables(Node targetNode, Function<String, Expression> constructVar, List<String> varNames){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

        candidates.addAll(this.replaceAssignExpr(targetNode, varNames, constructVar));
        candidates.addAll(this.replaceArgs(targetNode, varNames, constructVar));
        candidates.addAll(this.replaceNameExprInIfCondition(targetNode, varNames, constructVar));
        candidates.addAll(this.replaceVarInReturnStmt(targetNode, varNames, constructVar));

        return candidates;
    }

    /**
     * 代入文における右辺の変数を置換する 
     * @param targetNode 置換対象 
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<CompilationUnit> replaceAssignExpr(Node targetNode, List<String> varNames, Function<String, Expression> constructExpr){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

        if (targetNode instanceof AssignExpr) {
            Expression originalAssignedValue = ((AssignExpr)targetNode).getValue();
            String originalAssignedValueName = originalAssignedValue.findFirst(SimpleName.class)
                .map(v -> v.asString())
                .orElse(originalAssignedValue.toString());
            for(String varName: varNames){
                if(originalAssignedValueName.equals(varName)){
                    continue;
                }
                NodeUtility.replaceNode(constructExpr.apply(varName), ((AssignExpr)targetNode).getValue())
                    .flatMap(n -> n.findCompilationUnit())
                    .ifPresent(candidates::add);
            }
        }
        return candidates;        
    }

    /**
     * メソッド呼び出しの引数における変数の置換を行う 
     * @param targetNode 置換対象 
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<CompilationUnit> replaceArgs(Node targetNode, List<String> varNames, Function<String, Expression> constructExpr){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

        if (targetNode instanceof MethodCallExpr){
            List<Expression> args = ((MethodCallExpr)targetNode).getArguments();
            for(String varName: varNames){
                for(Expression arg: args){
                    if(arg.toString().equals(varName)){
                        continue;
                    }
                    NodeUtility.replaceNode(constructExpr.apply(varName), arg)
                        .flatMap(n -> n.findCompilationUnit())
                        .ifPresent(candidates::add);
                }
            }
        }
        return candidates; 
    }

    /**
     * if文の条件式の変数を置換する
     * @param targetNode 置換対象 
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<CompilationUnit> replaceNameExprInIfCondition(Node targetNode, List<String> varNames, Function<String, Expression> constructExpr){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        
        if (targetNode instanceof IfStmt) {
            Expression condition = ((IfStmt)targetNode).getCondition();
            List<NameExpr> varsInCondition = condition.findAll(NameExpr.class);
            for(NameExpr var: varsInCondition){
                for(String varName: varNames){
                    if(var.toString().equals(varName)){
                        continue;
                    }
                    NodeUtility.replaceNode(constructExpr.apply(varName), var)
                        .flatMap(n -> n.findCompilationUnit())
                        .ifPresent(candidates::add);
                }
            }
        }
        return candidates;        
    }

    /**
     * returnされる変数を置換する
     * @param targetNode 置換対象 
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<CompilationUnit> replaceVarInReturnStmt(Node targetNode, List<String> varNames, Function<String, Expression> constructExpr){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        
        if (targetNode instanceof ReturnStmt) {
            ((ReturnStmt)targetNode).getExpression().ifPresent(var -> {
                for(String varName: varNames){
                    if(var.toString().equals(varName)){
                        continue;
                    }
                    NodeUtility.replaceNode(constructExpr.apply(varName), var)
                        .flatMap(n -> n.findCompilationUnit())
                        .ifPresent(candidates::add);
                }
            });
        }
        return candidates;        
    }
}