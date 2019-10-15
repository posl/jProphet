package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
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
    public List<RepairUnit> exec(RepairUnit repairUnit) {
        final Node targetNode = repairUnit.getTargetNode();
        final Node classNode = targetNode.findParent(ClassOrInterfaceDeclaration.class).get();
        final Node methodNode =  targetNode.findParent(MethodDeclaration.class).get();
        final List<FieldDeclaration> fields = classNode.findAll(FieldDeclaration.class);
        final List<VariableDeclarationExpr> localVars = methodNode.findAll(VariableDeclarationExpr.class);
        final int varNameIndexInSimpleName = 1;
        final List<String> fieldNames = fields.stream()
                                        .map(field -> field.findAll(SimpleName.class).get(varNameIndexInSimpleName).asString())
                                        .collect(Collectors.toList());
        final List<String> localVarNames = localVars.stream()
                                         .map(localVar -> localVar.findAll(SimpleName.class).get(varNameIndexInSimpleName).asString())
                                         .collect(Collectors.toList());


        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        // 代入文の置換
        if (targetNode instanceof AssignExpr) {
            // メンバ変数で置換
            for(String fieldName: fieldNames){
                RepairUnit newCandidate = RepairUnit.copy(repairUnit);
                ((AssignExpr) newCandidate.getTargetNode()).setValue(new FieldAccessExpr(
                    new ThisExpr(), fieldName
                ));
                candidates.add(newCandidate);
            }
            // ローカル変数で置換
            for(String localVarName: localVarNames){
                RepairUnit newCandidate = RepairUnit.copy(repairUnit);
                ((AssignExpr) newCandidate.getTargetNode()).setValue(new NameExpr(localVarName));
                candidates.add(newCandidate);
            }
        }

        // メソッドの引数の置換
        if (targetNode instanceof MethodCallExpr){
            final int argc = ((MethodCallExpr)targetNode).getArguments().size(); 
            // 各引数をメンバ変数で置換   
            for(String fieldName: fieldNames){
                for(int i = 0; i < argc; i++){
                    RepairUnit newCandidate = RepairUnit.copy(repairUnit);
                    MethodCallExpr methodCallExpr = (MethodCallExpr)newCandidate.getTargetNode();
                    methodCallExpr.setArgument(i, new FieldAccessExpr(new ThisExpr(), fieldName));
                    candidates.add(newCandidate);
                }
            }
            // 各引数をローカル変数で置換   
            for(String localVarName: localVarNames){
                for(int i = 0; i < argc; i++){
                    RepairUnit newCandidate = RepairUnit.copy(repairUnit);
                    MethodCallExpr methodCallExpr = (MethodCallExpr)newCandidate.getTargetNode();
                    methodCallExpr.setArgument(i, new NameExpr(localVarName));
                    candidates.add(newCandidate);
                }
            }
        }
        return candidates;
    }



}