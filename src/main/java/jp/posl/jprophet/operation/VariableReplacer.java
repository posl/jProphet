package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;

import jp.posl.jprophet.NodeUtility;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

public class VariableReplacer {

    /**
     * 代入文の右辺の値と，メソッド呼び出しの引数を対象に，クラスのメンバ変数及びメソッドのローカル変数，仮引数で置換を行う 
     * @param targetNode      置換対象
     * @param fieldNames      フィールド名のリスト
     * @param localVarNames   ローカル変数のリスト
     * @param parameterNames  仮引数のリスト
     * @return
     */
    public List<Node> replaceAllVariables(Node targetNode, List<String> fieldNames, List<String> localVarNames,
            List<String> parameterNames) {
        List<Node> replacedNodes = new ArrayList<Node>();

        final Function<String, Expression> constructField = fieldName -> new FieldAccessExpr(new ThisExpr(), fieldName);
        replacedNodes.addAll(this.replaceVariables(targetNode, constructField, fieldNames));

        final Function<String, Expression> constructVar = varName -> new NameExpr(varName);
        replacedNodes.addAll(this.replaceVariables(targetNode, constructVar, localVarNames));
        replacedNodes.addAll(this.replaceVariables(targetNode, constructVar, parameterNames));
        return replacedNodes;
    }

    /**
     * Statementに対して，代入分の右辺の値と，メソッド呼び出し引数を置換する．
     * @param targetNode     置換対象
     * @param fieldNames     フィールド名のリスト
     * @param localVarNames  ローカル変数のリスト
     * @param parameterNames 仮引数のリスト
     * @return
     */
    public List<Node> replaceAllVariablesForStatement(Node targetNode, List<String> fieldNames, List<String> localVarNames,
    List<String> parameterNames) {
        if(targetNode instanceof ExpressionStmt) {
            final List<Node> replacedNodes = replaceAllVariables(((ExpressionStmt)targetNode).getExpression(), fieldNames, localVarNames, parameterNames);
            return replacedNodes.stream().map(expr -> new ExpressionStmt((Expression)expr)).collect(Collectors.toList());
        }
        else {
            final List<Node> replacedNodes = replaceAllVariables(targetNode, fieldNames, localVarNames, parameterNames);
            return replacedNodes;
        }
    }

    /**
     * 代入式の右辺とメソッド呼び出しの実引数を置換する
     * @param targetNode   置換対象
     * @param constructVar 変数名からExpressionノードを作成する関数
     * @param varNames     置換先の変数名のリスト
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<Node> replaceVariables(Node targetNode, Function<String, Expression> constructVar,
            List<String> varNames) {
        List<Node> replacedNodes = new ArrayList<Node>();

        replacedNodes.addAll(this.replaceAssignExpr(targetNode, varNames, constructVar));
        replacedNodes.addAll(this.replaceArgs(targetNode, varNames, constructVar));
        replacedNodes.addAll(this.replaceNameExprInIfCondition(targetNode, varNames,
            constructVar));
        replacedNodes.addAll(this.replaceVarInReturnStmt(targetNode, varNames,
            constructVar));

        return replacedNodes;
    }

    /**
     * 代入文における右辺の変数を置換する
     * @param targetNode    置換対象
     * @param varNames      置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<Node> replaceAssignExpr(Node targetNode, List<String> varNames,
            Function<String, Expression> constructExpr) {
        List<Node> replacedNodes = new ArrayList<Node>();

        if (targetNode instanceof AssignExpr) {
            Expression originalAssignedValue = ((AssignExpr) targetNode).getValue();
            String originalAssignedValueName = originalAssignedValue.findFirst(SimpleName.class).map(v -> v.asString())
                    .orElse(originalAssignedValue.toString());
            for (String varName : varNames) {
                if (originalAssignedValueName.equals(varName)) {
                    continue;
                }
                final AssignExpr copiedNode = (AssignExpr)NodeUtility.initTokenRange((AssignExpr)targetNode.clone()).orElseThrow();
                NodeUtility.replaceNodeWithoutCompilationUnit(copiedNode.getValue(), 
                    constructExpr.apply(varName))
                    .ifPresent(replacedNodes::add);
            }
        }
        return replacedNodes;
    }

    /**
     * メソッド呼び出しの引数における変数の置換を行う
     * @param targetNode    置換対象
     * @param varNames      置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<Node> replaceArgs(Node targetNode, List<String>
        varNames, Function<String, Expression> constructExpr) {
        List<Node> replacedNodes = new ArrayList<Node>();
    
        if (targetNode instanceof MethodCallExpr) {
            final MethodCallExpr copiedNode = (MethodCallExpr)NodeUtility.initTokenRange((MethodCallExpr)targetNode.clone()).orElseThrow();
            List<Expression> args = copiedNode.getArguments();
            for (String varName : varNames) {
                for (Expression arg : args) {
                    if (arg.toString().equals(varName)) {
                        continue;
                    } 
                    final Optional<Node> copiedArg = NodeUtility.parseNodeWithPointer(targetNode, arg);
                    copiedArg.ifPresent(
                        a -> NodeUtility.replaceNodeWithoutCompilationUnit(a, constructExpr.apply(varName)) 
                            .ifPresent(replacedNodes::add)
                    ); 
                }   
            }
        }
        return replacedNodes;
    }
    
    /** 
     * if文の条件式の変数を置換する
     * @param targetNode 置換対象
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<Node> replaceNameExprInIfCondition(Node targetNode,
        List<String> varNames, Function<String, Expression> constructExpr) {
        List<Node> replacedNodes = new ArrayList<Node>();
    
        if (targetNode instanceof IfStmt) { 
            final IfStmt copiedIfStmt = (IfStmt)NodeUtility.initTokenRange(targetNode.clone()).orElseThrow();
            List<NameExpr> varsInCondition = copiedIfStmt.getCondition().findAll(NameExpr.class); 
            for (NameExpr var : varsInCondition) { 
                for(String varName : varNames) { 
                    if (var.toString().equals(varName)) { continue;} 
                    final Node copiedVar = NodeUtility.parseNodeWithPointer(copiedIfStmt, var).orElseThrow();
                    NodeUtility.replaceNodeWithoutCompilationUnit(copiedVar, constructExpr.apply(varName)) 
                        .ifPresent(replacedNodes::add);
                } 
            } 
        } 
        return replacedNodes;
    }
    
    /** 
     * returnされる変数を置換する
     * @param targetNode 置換対象
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<Node> replaceVarInReturnStmt(Node targetNode,
        List<String> varNames, Function<String, Expression> constructExpr) {
        List<Node> replacedNodes = new ArrayList<Node>();
    
        if (targetNode instanceof ReturnStmt) { 
            final ReturnStmt copiedNode = (ReturnStmt)NodeUtility.initTokenRange((ReturnStmt)targetNode.clone()).orElseThrow();
            copiedNode.getExpression().ifPresent(var -> {
                for (String varName : varNames) {
                    if (var.toString().equals(varName)) { continue; } 
                    NodeUtility.parseNodeWithPointer(targetNode, var)
                        .ifPresent(cpv -> NodeUtility.replaceNodeWithoutCompilationUnit(cpv, constructExpr.apply(varName))
                            .ifPresent(replacedNodes::add));
                }
            }); 
        } 
        return replacedNodes;
    }
     

}