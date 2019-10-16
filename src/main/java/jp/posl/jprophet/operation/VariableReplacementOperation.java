package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;

import jp.posl.jprophet.RepairUnit;

/**
 * 対象ステートメント中の変数を別のもので置き換える操作
 */
public class VariableReplacementOperation implements AstOperation {
    private final RepairUnit repairUnit;
    private final Node targetNode;
    private final int varNameIndexInSimpleName = 1;
    private final List<String> fieldNames;
    private final List<String> localVarNames;
    private List<RepairUnit> candidates = new ArrayList<RepairUnit>();

    public VariableReplacementOperation(final RepairUnit repairUnit){
        this.repairUnit = repairUnit;
        this.targetNode = repairUnit.getTargetNode();
        this.fieldNames = collectFieldNames(repairUnit);
        this.localVarNames = collectLocalVarNames(repairUnit);
    }

    private List<String> collectFieldNames(final RepairUnit repairUnit){
        ClassOrInterfaceDeclaration classNode;
        try {
            classNode = this.targetNode.findParent(ClassOrInterfaceDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<String>();
        }
        final List<FieldDeclaration> fields = classNode.findAll(FieldDeclaration.class);
        final List<String> fieldNames = fields.stream()
                            .map(field -> field.findAll(SimpleName.class).get(varNameIndexInSimpleName).asString())
                            .collect(Collectors.toList());
        
        return fieldNames;
    }
    private List<String> collectLocalVarNames(final RepairUnit repairUnit){
        MethodDeclaration methodNode;
        try {
            methodNode =  this.targetNode.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<String>();
        }
        final List<VariableDeclarationExpr> localVars = methodNode.findAll(VariableDeclarationExpr.class);
        final List<String> localVarNames = localVars.stream()
                                 .map(localVar -> localVar.findAll(SimpleName.class).get(varNameIndexInSimpleName).asString())
                                 .collect(Collectors.toList());
        return localVarNames;
    }

    public List<RepairUnit> exec() {
        // 代入文の置換
        if (targetNode instanceof AssignExpr) {
            // メンバ変数で置換
            for(String fieldName: this.fieldNames){
                RepairUnit newCandidate = RepairUnit.copy(this.repairUnit);
                ((AssignExpr) newCandidate.getTargetNode()).setValue(new FieldAccessExpr(
                    new ThisExpr(), fieldName
                ));
                candidates.add(newCandidate);
            }
            // ローカル変数で置換
            for(String localVarName: this.localVarNames){
                RepairUnit newCandidate = RepairUnit.copy(this.repairUnit);
                ((AssignExpr) newCandidate.getTargetNode()).setValue(new NameExpr(localVarName));
                candidates.add(newCandidate);
            }
        }

        // メソッドの引数の置換
        if (targetNode instanceof MethodCallExpr){
            final int argc = ((MethodCallExpr)targetNode).getArguments().size(); 
            // 各引数をメンバ変数で置換   
            for(String fieldName: this.fieldNames){
                for(int i = 0; i < argc; i++){
                    RepairUnit newCandidate = RepairUnit.copy(this.repairUnit);
                    MethodCallExpr methodCallExpr = (MethodCallExpr)newCandidate.getTargetNode();
                    methodCallExpr.setArgument(i, new FieldAccessExpr(new ThisExpr(), fieldName));
                    candidates.add(newCandidate);
                }
            }
            // 各引数をローカル変数で置換   
            for(String localVarName: this.localVarNames){
                for(int i = 0; i < argc; i++){
                    RepairUnit newCandidate = RepairUnit.copy(this.repairUnit);
                    MethodCallExpr methodCallExpr = (MethodCallExpr)newCandidate.getTargetNode();
                    methodCallExpr.setArgument(i, new NameExpr(localVarName));
                    candidates.add(newCandidate);
                }
            }
        }
        return candidates;
    }



}