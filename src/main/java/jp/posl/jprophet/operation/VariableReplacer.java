package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;

import jp.posl.jprophet.NodeUtility;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

public class VariableReplacer {

    /**
     * targetNode中において，代入文の右辺の変数と，メソッド呼び出しの引数を対象に，クラスのメンバ変数及びメソッドのローカル変数，仮引数で置換を行う 
     * @param targetNode      置換対象
     * @param fieldNameToType      フィールド名のリスト
     * @param localVarNameToType   ローカル変数のリスト
     * @param parameterNames  仮引数のリスト
     * @return
     */
    public List<Node> replaceAllVariables(Node targetNode, Map<String, String> fieldNameToType, Map<String, String> localVarNameToType,
            Map<String, String> parameterNameToType) {
        List<Node> replacedNodes = new ArrayList<Node>();

        final Map<String, String> allVarNameToType = new HashMap<String, String>();
        allVarNameToType.putAll(fieldNameToType);
        allVarNameToType.putAll(localVarNameToType);
        allVarNameToType.putAll(parameterNameToType);

        final Function<String, Expression> constructField = fieldName -> new FieldAccessExpr(new ThisExpr(), fieldName);
        replacedNodes.addAll(this.replaceVariables(targetNode, constructField, fieldNameToType, allVarNameToType));

        final Function<String, Expression> constructVar = varName -> new NameExpr(varName);
        replacedNodes.addAll(this.replaceVariables(targetNode, constructVar, localVarNameToType, allVarNameToType));
        replacedNodes.addAll(this.replaceVariables(targetNode, constructVar, parameterNameToType, allVarNameToType));
        return replacedNodes;
    }

    /**
     * Statementに対して，代入分の右辺の値と，メソッド呼び出し引数を置換する．
     * @param targetNode     置換対象
     * @param fieldNameToType     フィールド名のリスト
     * @param localVarNameToType  ローカル変数のリスト
     * @param parameterNameToType 仮引数のリスト
     * @return
     */
    public List<Node> replaceAllVariablesForStatement(Node targetNode, Map<String, String> fieldNameToType, Map<String, String> localVarNameToType,
            Map<String, String> parameterNameToType) {
        if(targetNode instanceof ExpressionStmt) {
            final List<Node> replacedNodes = replaceAllVariables(((ExpressionStmt)targetNode).getExpression(), fieldNameToType, localVarNameToType, parameterNameToType);
            return replacedNodes.stream().map(expr -> new ExpressionStmt((Expression)expr)).collect(Collectors.toList());
        }
        else {
            final List<Node> replacedNodes = replaceAllVariables(targetNode, fieldNameToType, localVarNameToType, parameterNameToType);
            return replacedNodes;
        }
    }

    /**
     * 代入式の右辺とメソッド呼び出しの実引数を置換する
     * @param targetNode   置換対象
     * @param constructVar 変数名からExpressionノードを作成する関数
     * @param varNameToType     置換先の変数名のリスト
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<Node> replaceVariables(Node targetNode, Function<String, Expression> constructVar,
            Map<String, String> varNameToType, Map<String, String> allVarNameToType) {
        List<Node> replacedNodes = new ArrayList<Node>();

        replacedNodes.addAll(this.replaceAssignExpr(targetNode, varNameToType, constructVar, allVarNameToType));
        replacedNodes.addAll(this.replaceArgs(targetNode, varNameToType, constructVar, allVarNameToType));
        replacedNodes.addAll(this.replaceNameExprInIfCondition(targetNode, varNameToType,
            constructVar, allVarNameToType));
        replacedNodes.addAll(this.replaceVarInReturnStmt(targetNode, varNameToType,
            constructVar, allVarNameToType));

        return replacedNodes;
    }

    /**
     * 代入文における右辺の変数を置換する
     * @param targetNode    置換対象
     * @param varNames      置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<Node> replaceAssignExpr(Node targetNode, Map<String, String> varNameToType,
            Function<String, Expression> constructExpr, Map<String, String> allVarNameToType) {
        final List<Node> replacedNodes = new ArrayList<Node>();

        if (targetNode instanceof AssignExpr) {
            final Expression targetExpr = ((AssignExpr) targetNode).getValue();
            final String targetVarName = targetExpr.findFirst(SimpleName.class).map(v -> v.asString())
                .orElse(targetExpr.toString());

            String targetVarType;
            if (this.getLiteralTypeName(targetExpr, targetVarName, allVarNameToType).isPresent()) {
                targetVarType = this.getLiteralTypeName(targetExpr, targetVarName, allVarNameToType).orElseThrow();
            }
            else {
                return Collections.emptyList();
            }

            for (Map.Entry<String, String> entry : varNameToType.entrySet()) {
                final String varName = entry.getKey();
                final String type = entry.getValue();
                if (targetVarName.equals(varName) || !(targetVarType.equals(type))) {
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
     * @param varNameToType      置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正後のCompilationUnitのリスト
     */
    private List<Node> replaceArgs(Node targetNode, Map<String, String> varNameToType, Function<String, Expression> constructExpr,
            Map<String, String> allVarNameToType) { 
        List<Node> replacedNodes = new ArrayList<Node>();
    
        if (targetNode instanceof MethodCallExpr) {
            final MethodCallExpr copiedNode = (MethodCallExpr)NodeUtility.initTokenRange((MethodCallExpr)targetNode.clone()).orElseThrow();
            List<Expression> targetArgs = copiedNode.getArguments();
            for (Map.Entry<String, String> entry : varNameToType.entrySet()) {
                final String varName = entry.getKey();
                final String type = entry.getValue();
                for (Expression targetArg : targetArgs) {
                    final String targetArgName = targetArg.toString();
                    String targetArgType;
                    if (this.getLiteralTypeName(targetArg, targetArgName, allVarNameToType).isPresent()) {
                        targetArgType = this.getLiteralTypeName(targetArg, targetArgName, allVarNameToType).orElseThrow();
                    }
                    else {
                        return Collections.emptyList();
                    }
                    if (targetArgName.equals(varName) || !(targetArgType.equals(type))) {
                        continue;
                    } 
                    final Optional<Node> copiedArg = NodeUtility.parseNodeWithPointer(targetNode, targetArg);
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
    private List<Node> replaceNameExprInIfCondition(Node targetNode, Map<String, String> varNameToType, Function<String, Expression> constructExpr,
            Map<String, String> allVarNameToType) {
        List<Node> replacedNodes = new ArrayList<Node>();
    
        if (targetNode instanceof IfStmt) { 
            final IfStmt copiedIfStmt = (IfStmt)NodeUtility.initTokenRange(targetNode.clone()).orElseThrow();
            List<NameExpr> varsInCondition = copiedIfStmt.getCondition().findAll(NameExpr.class); 
            for (NameExpr targetVar : varsInCondition) { 
                for(Map.Entry<String, String> entry : varNameToType.entrySet()) { 
                    final String varName = entry.getKey();
                    final String type = entry.getValue();
                    final String targetVarName = targetVar.toString();
                    final String targetVarType = allVarNameToType.get(targetVarName);
                    if (targetVarType == null) {
                        return Collections.emptyList();
                    }
                    if (targetVarName.equals(varName) || !(targetVarType.equals(type))) {
                        continue;
                    } 
                    final Node copiedVar = NodeUtility.parseNodeWithPointer(copiedIfStmt, targetVar).orElseThrow();
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
    private List<Node> replaceVarInReturnStmt(Node targetNode, Map<String, String> varNameToType, Function<String, Expression> constructExpr,
            Map<String, String> allVarNameToType) {
        List<Node> replacedNodes = new ArrayList<Node>();
    
        if (targetNode instanceof ReturnStmt) { 
            final ReturnStmt copiedNode = (ReturnStmt)NodeUtility.initTokenRange((ReturnStmt)targetNode.clone()).orElseThrow();
            copiedNode.getExpression().ifPresent(targetVarExpr -> {
                for (Map.Entry<String, String> entry : varNameToType.entrySet()) { 
                    final String varName = entry.getKey();
                    final String type = entry.getValue();
                    final String targetVarName = targetVarExpr.toString();
                    String targetVarType;
                    if (this.getLiteralTypeName(targetVarExpr, targetVarName, allVarNameToType).isPresent()) {
                        targetVarType = this.getLiteralTypeName(targetVarExpr, targetVarName, allVarNameToType).orElseThrow();
                    }
                    else {
                        break;
                    }
                    if (targetVarName.equals(varName) || !(targetVarType.equals(type))) {
                        continue;
                    } 
                    NodeUtility.parseNodeWithPointer(targetNode, targetVarExpr)
                        .ifPresent(cpv -> NodeUtility.replaceNodeWithoutCompilationUnit(cpv, constructExpr.apply(varName))
                            .ifPresent(replacedNodes::add));
                }
            }); 
        } 
        return replacedNodes;
    }
     
    private Optional<String> getLiteralTypeName(Expression targetExpr, String targetName, Map<String, String> allVarNameToType) {
        if (targetExpr instanceof BooleanLiteralExpr) {
            return Optional.of("boolean");
        }
        if (targetExpr instanceof StringLiteralExpr) {
            return Optional.of("String");
        }
        if (targetExpr instanceof CharLiteralExpr) {
            return Optional.of("char");
        }
        if (targetExpr instanceof IntegerLiteralExpr) {
            return Optional.of("int");
        }
        if (targetExpr instanceof LongLiteralExpr) {
            return Optional.of("long");
        }
        if (targetExpr instanceof DoubleLiteralExpr) {
            return Optional.of("double");
        }
        if (allVarNameToType.get(targetName) != null) {
            return Optional.of(allVarNameToType.get(targetName));
        }
        return Optional.empty();
    }

}