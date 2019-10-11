package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
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
        Node targetNode = repairUnit.getTargetNode();

        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        if (targetNode instanceof AssignExpr) {
            Node classNode = targetNode.findParent(ClassOrInterfaceDeclaration.class).get();
            Node methodNode =  targetNode.findParent(MethodDeclaration.class).get();
            List<FieldDeclaration> fields = classNode.findAll(FieldDeclaration.class);
            List<VariableDeclarationExpr> localVars = methodNode.findAll(VariableDeclarationExpr.class);

            for(FieldDeclaration field: fields){
                // フィールド宣言文中に，SimpleNameは型名と変数名の二つがある
                int varNameIndexInSimpleNames = 1;
                String fieldName = field.findAll(SimpleName.class).get(varNameIndexInSimpleNames).asString();
                RepairUnit newCandidate = RepairUnit.copy(repairUnit);
                ((AssignExpr) newCandidate.getTargetNode()).setValue(new FieldAccessExpr(
                    new ThisExpr(), fieldName
                ));
                candidates.add(newCandidate);
            }
            for(VariableDeclarationExpr localVar: localVars){
                String varName = localVar.findAll(SimpleName.class).get(1).asString();
                RepairUnit newCandidate = RepairUnit.copy(repairUnit);
                ((AssignExpr) newCandidate.getTargetNode()).setValue(new NameExpr(varName));
                candidates.add(newCandidate);
            }
        }

        return candidates;
    }



}