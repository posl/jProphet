package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;

import jp.posl.jprophet.RepairUnit;

/**
 * 対象ステートメント中の変数を別のもので置き換える操作を行う
 */
public class VariableReplacementOperation implements AstOperation {
    private final RepairUnit repairUnit;
    private final Node targetNode;
    private final int varNameIndexInSimpleName = 1;
    private final List<String> fieldNames;
    private final List<String> localVarNames;
    private final List<String> parameterNames;

    /**
     * 修正対象のRepairUnitによって新しいVariableReplacementOperationを生成
     * @param repairUnit 修正対象
     */
    public VariableReplacementOperation(final RepairUnit repairUnit){
        this.repairUnit = repairUnit;
        this.targetNode = this.repairUnit.getTargetNode();
        this.fieldNames = this.collectFieldNames();
        this.localVarNames = this.collectLocalVarNames();
        this.parameterNames = this.collectParameterNames();
    }

    /**
     * 変数の置換操作を行い修正パッチ候補を生成する
     * 代入文の右辺の値と，メソッド呼び出しの引数を対象に，クラスのメンバ変数及びメソッドのローカル変数，仮引数で置換を行う
     * 一つの修正パッチ候補につき一箇所の置換
     * @return 生成された修正パッチ候補のリスト
     */
    public List<RepairUnit> exec() {
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        Function<String, Expression> constructField = fieldName -> new FieldAccessExpr(new ThisExpr(), fieldName);
        candidates.addAll(this.replaceAssignExprAndArgsWith(constructField, this.fieldNames));

        Function<String, Expression> constructVar = varName -> new NameExpr(varName);
        candidates.addAll(this.replaceAssignExprAndArgsWith(constructVar, this.localVarNames));
        candidates.addAll(this.replaceAssignExprAndArgsWith(constructVar, this.parameterNames));

        return candidates;
    }

    /**
     * 代入式の右辺とメソッド呼び出しの実引数を置換する
     * 
     * @param constructVar 変数名からExpressionノードを作成する関数
     * @param varNames 置換先の変数名のリスト
     * @return 生成された修正パッチ候補のリスト
     */
    private List<RepairUnit> replaceAssignExprAndArgsWith(Function<String, Expression> constructVar, List<String> varNames){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        candidates.addAll(this.replaceAssignExprWith(varNames, constructVar));
        candidates.addAll(this.replaceArgsWith(varNames, constructVar));

        return candidates;
    }


    /**
     * 修正対象のステートメントが属するクラスのフィールドを集める   
     * @return フィールド名のリスト
     */
    private List<String> collectFieldNames(){
        ClassOrInterfaceDeclaration classNode;
        try {
            classNode = this.targetNode.findParent(ClassOrInterfaceDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<String>();
        }
        final List<FieldDeclaration> fields = classNode.findAll(FieldDeclaration.class);
        final List<String> fieldNames = fields.stream()
                                              .map(field -> field.findAll(SimpleName.class).get(this.varNameIndexInSimpleName).asString())
                                              .collect(Collectors.toList());
        
        return fieldNames;
    }

    /**
     * 修正対象のステートメントが書かれているメソッド中のローカル変数を集める 
     * @return ローカル変数名のリスト
     */
    private List<String> collectLocalVarNames(){
        MethodDeclaration methodNode;
        try {
            methodNode =  this.targetNode.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<String>();
        }
        final List<VariableDeclarationExpr> localVars = methodNode.findAll(VariableDeclarationExpr.class);
        final List<String> localVarNames = localVars.stream()
                                                    .map(localVar -> localVar.findAll(SimpleName.class).get(this.varNameIndexInSimpleName).asString())
                                                    .collect(Collectors.toList());
        return localVarNames;
    }

    /**
     * 修正対象のステートメントが書かれているメソッドの仮引数を集める 
     * @return 仮引数の変数名のリスト
     */
    private List<String> collectParameterNames(){
        MethodDeclaration methodNode;
        try {
            methodNode =  this.targetNode.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<String>();
        }
        final List<Parameter> parameters = methodNode.findAll(Parameter.class);
        final List<String> parameterNames = parameters.stream()
                                                    .map(localVar -> localVar.findAll(SimpleName.class).get(this.varNameIndexInSimpleName).asString())
                                                    .collect(Collectors.toList());
        return parameterNames;
    }


    /**
     * 代入文における右辺の変数を置換する 
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正パッチ候補のリスト
     */
    private List<RepairUnit> replaceAssignExprWith(List<String> varNames, Function<String, Expression> constructExpr){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        if (targetNode instanceof AssignExpr) {
            for(String varName: varNames){
                Expression originalAssignedValue = ((AssignExpr)this.targetNode).getValue();
                String originalAssignedValueName; 
                try {
                    originalAssignedValueName = originalAssignedValue.findFirst(SimpleName.class).orElseThrow().asString();
                } catch (NoSuchElementException e) {
                    originalAssignedValueName = originalAssignedValue.toString();
                }
                if(originalAssignedValueName.equals(varName)){
                    continue;
                }
                RepairUnit newCandidate = RepairUnit.copy(this.repairUnit);
                ((AssignExpr) newCandidate.getTargetNode()).setValue(constructExpr.apply(varName));
                candidates.add(newCandidate);
            }
        }

        return candidates;        
    }

    /**
     * メソッド呼び出しの引数における変数の置換を行う 
     * @param varNames 置換先の変数名のリスト
     * @param constructExpr 置換後の変数のASTノードを生成するラムダ式
     * @return 置換によって生成された修正パッチ候補のリスト
     */
    private List<RepairUnit> replaceArgsWith(List<String> varNames, Function<String, Expression> constructExpr){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        if (this.targetNode instanceof MethodCallExpr){
            final int argc = ((MethodCallExpr)(this.targetNode)).getArguments().size(); 
            for(String varName: varNames){
                for(int i = 0; i < argc; i++){
                    String originalArgValue = ((MethodCallExpr)this.targetNode).getArgument(i).toString();
                    if(originalArgValue.equals(varName)){
                        continue;
                    }
                    RepairUnit newCandidate = RepairUnit.copy(this.repairUnit);
                    MethodCallExpr methodCallExpr = (MethodCallExpr)newCandidate.getTargetNode();
                    methodCallExpr.setArgument(i, constructExpr.apply(varName));
                    candidates.add(newCandidate);
                }
            }
        }

        return candidates; 
    }
}