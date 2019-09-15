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
 * 対象ステートメントの変数や関数，定数などを別のもので置き換える操作
 */
public class ValueReplacementOperation implements AstOperation {
    public List<RepairUnit> exec(RepairUnit repairUnit) {
        Node targetNode = repairUnit.getNode();

        if (targetNode instanceof AssignExpr) {
            // Node clone = targetNode.clone();
            // ((AssignExpr) targetNode).setValue(new FieldAccessExpr(new ThisExpr(),
            // "hoge"));
            // ((AssignExpr) targetNode).setValue(new NameExpr("lc"));
            Node classNode = targetNode.findParent(ClassOrInterfaceDeclaration.class).get();
            Node methodNode =  targetNode.findParent(MethodDeclaration.class).get();
            List<FieldDeclaration> fields = classNode.findAll(FieldDeclaration.class);
            List<VariableDeclarationExpr> localVars = methodNode.findAll(VariableDeclarationExpr.class);

            for(FieldDeclaration field: fields){
                String fieldName = field.findAll(SimpleName.class).get(1).asString();
                // List<SimpleName> fieldNames = field.findAll(SimpleName.class);
                ((AssignExpr) targetNode).setValue(new FieldAccessExpr(
                    new ThisExpr(), fieldName
                ));
            }
            for(VariableDeclarationExpr localVar: localVars){
                String varName = localVar.findAll(SimpleName.class).get(1).asString();
                ((AssignExpr) targetNode).setValue(new NameExpr(varName));
            }
        }

        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }



}