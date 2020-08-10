package jp.posl.jprophet.evaluator.extractor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.AstDiff;
import jp.posl.jprophet.evaluator.NodeWithDiffType;
import jp.posl.jprophet.evaluator.ProgramChunk;
import jp.posl.jprophet.evaluator.extractor.StatementKindExtractor.StatementKind;
import jp.posl.jprophet.evaluator.extractor.ModKinds.ModKind;
import jp.posl.jprophet.evaluator.extractor.VariableCharacteristics.VarChar;
import jp.posl.jprophet.patch.Patch;

public class FeatureExtractor {

    /**
     * 対象のステートメントが,
     * パッチの修正部分(TARGET)かその前後(PREV, NEXT)かを表現
     */
    public enum StatementPos {
        TARGET,
        PREV,
        NEXT
    }

    /**
     * パッチから特徴ベクトルを抽出
     * @param patch 特徴抽出を行うパッチ
     * @return 特徴ベクトル
     */
    public FeatureVector extract(Patch patch) {
        final Node originalRoot = patch.getOriginalCompilationUnit().findRootNode();
        final Node fixedRoot = patch.getFixedCompilationUnit().findRootNode();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalRoot, fixedRoot);
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final ModKindExtractor modKindExtractor = new ModKindExtractor();
        final Map<ProgramChunk,ModKinds> modKindMap = modKindExtractor.extract(nodeWithDiffType, chunks);
        final FeatureVector vector = new FeatureVector();

        final Set<Entry<ProgramChunk, ModKinds>> set = modKindMap.entrySet();
        set.stream()
            .forEach(entry -> {
                final ProgramChunk patchTargetChunk = entry.getKey();
                final ModKinds modKinds = entry.getValue();
                vector.add(this.extractModFeature(patchTargetChunk, modKinds, fixedRoot));
                vector.add(this.extractVariableFeature(patchTargetChunk, originalRoot, fixedRoot));
            });

        return vector;
    }

    /**
     * パッチの変更に関する特徴抽出を行う
     * パッチの種類の集合とパッチが適量されたプログラムに登場するステートメントの種類の集合の間の直積集合の要素ごとに
     * 特徴抽出を行う
     * @param patchTargetChunk パッチ適用部分のコード
     * @param modKinds 修正の種別
     * @param fixedRoot 修正前コードのルートノード
     * @return 特徴ベクトル
     */
    private FeatureVector extractModFeature(ProgramChunk patchTargetChunk, ModKinds modKinds, Node fixedRoot) {
        final FeatureVector vector = new FeatureVector();
        final StatementKindExtractor stmtKindExtractor = new StatementKindExtractor();

        modKinds.getKinds().stream()
            .forEach(modType -> vector.add(modType));

        final List<Node> allFixedNodes = NodeUtility.getAllNodesInDepthFirstOrder(fixedRoot);
        final List<Node> allStmts = allFixedNodes.stream()
            .filter(node -> stmtKindExtractor.extract(node).isPresent())
            .collect(Collectors.toList());

        allStmts.stream().forEach(stmt -> {
            final int stmtBegin = stmt.getBegin().orElseThrow().line;
            final boolean nodeComesBeforeChunk = stmtBegin < patchTargetChunk.getBegin(); 
            final boolean nodeComesAfterChunk = patchTargetChunk.getEnd() < stmtBegin; 
            StatementPos pos = StatementPos.TARGET;
            if (nodeComesBeforeChunk) {
                pos = StatementPos.PREV;
            }
            if (nodeComesAfterChunk) {
                pos = StatementPos.NEXT;
            }
            final StatementKind stmtType = stmtKindExtractor.extract(stmt).orElseThrow();
            for (ModKind modType: modKinds.getKinds()) {
                vector.add(pos, stmtType, modType);
            }
        });
        return vector;
    }

    /**
     * パッチ中に登場する変数に関する特徴抽出を行う 
     * 修正後のプログラムに登場する変数を集め，それぞれの変数と同一の変数を
     * 修正前プログラムから探索し，その二つの変数の組み合わせごとに特徴抽出を行う
     * @param patchTargetChunk パッチ適用部分のコード
     * @param originalRoot 修正前コードのルートノード
     * @param fixedRoot 修正後コードのルートノード
     * @return 特徴ベクトル
     */
    private FeatureVector extractVariableFeature(ProgramChunk patchTargetChunk, Node originalRoot, Node fixedRoot) {
        final FeatureVector vector = new FeatureVector();
        final VariableCharacteristicExtractor varCharExtractor = new VariableCharacteristicExtractor();
        final List<NameExpr> originalVariables = originalRoot.findAll(NameExpr.class);
        final List<NameExpr> fixedVariables = fixedRoot.findAll(NameExpr.class);
        for(NameExpr fixedVar: fixedVariables) {
            if(!varCharExtractor.findDeclarator(fixedVar).isPresent()) {
                continue;
            }
            final Node fixedVarDec = varCharExtractor.findDeclarator(fixedVar).orElseThrow();
            final List<NameExpr> originalVarsWithSameName = originalVariables.stream()
                .filter(original -> original.getNameAsString().equals(fixedVar.getNameAsString()))
                .collect(Collectors.toList());
            final List<NameExpr> originalVarsWithSameScope = originalVarsWithSameName.stream()
                .filter(original-> {
                    final Optional<Node> declarator = varCharExtractor.findDeclarator(original);
                    if(!declarator.isPresent()) {
                        return false;
                    }
                    final Node originalVarDec = declarator.orElseThrow();
                    final boolean originalVarIsNotField = originalVarDec.findParent(MethodDeclaration.class).isPresent();
                    final boolean fixedVarIsNotField = fixedVarDec.findParent(MethodDeclaration.class).isPresent();
                    final boolean bothIsField = !originalVarIsNotField && !fixedVarIsNotField;
                    if(bothIsField) return true;
                    if(!bothIsField) {
                        final Optional<MethodDeclaration> methodWhereOriginalVarWasDeclared = originalVarDec.findParent(MethodDeclaration.class);
                        final Optional<MethodDeclaration> methodWhereFixedVarWasDeclared = fixedVarDec.findParent(MethodDeclaration.class);
                        if (!methodWhereOriginalVarWasDeclared.isPresent() || !methodWhereFixedVarWasDeclared.isPresent()) {
                            return false;
                        }
                        final String methodNameWhereOriginalVarWasDeclared = methodWhereOriginalVarWasDeclared.orElseThrow().getNameAsString();
                        final String methodNameWhereFixedlVarWasDeclared = methodWhereFixedVarWasDeclared.orElseThrow().getNameAsString();
                        if(methodNameWhereOriginalVarWasDeclared.equals(methodNameWhereFixedlVarWasDeclared)) {
                            return true;
                        }
                    } 
                    return false;
                })
                .collect(Collectors.toList());
            if(originalVarsWithSameScope.size() > 0) {
                final NameExpr sameVarAsFixedVar = originalVarsWithSameScope.get(0);
                vector.add(this.createVectorByVarRelation(patchTargetChunk, sameVarAsFixedVar, fixedVar));
            }
            else if(originalVarsWithSameName.size() > 0) {
                final NameExpr sameVarAsFixedVar = originalVarsWithSameName.get(0);
                vector.add(this.createVectorByVarRelation(patchTargetChunk, sameVarAsFixedVar, fixedVar));
            }
        }
        return vector;
    }

    /**
     * 修正前と修正後の二つの変数の間の特徴ベクトルを抽出する
     * 修正前の変数をa，修正後の変数をbとした時に変数aのもつ特性の集合Aと
     * 変数bのもつ特性の集合Bの直積集合の要素を一つずつベクトルに変換する
     * @param patchTargetChunk パッチ適用部分のコード
     * @param originalVar 修正前コード中の変数
     * @param fixedVar 修正後コード中の変数
     * @return 特徴ベクトル
     */
    private FeatureVector createVectorByVarRelation(ProgramChunk patchTargetChunk, NameExpr originalVar, NameExpr fixedVar) {
        final FeatureVector vector = new FeatureVector();
        final VariableCharacteristicExtractor varCharsExtractor = new VariableCharacteristicExtractor();
        final VariableCharacteristics originalVarChars = varCharsExtractor.extract(originalVar);
        final VariableCharacteristics fixedVarChars = varCharsExtractor.extract(fixedVar);
        final int nodeBegin = fixedVar.getBegin().orElseThrow().line;
        final boolean nodeComesBeforeChunk = nodeBegin < patchTargetChunk.getBegin(); 
        final boolean nodeComesAfterChunk = patchTargetChunk.getEnd() < nodeBegin; 
        StatementPos pos = StatementPos.TARGET;
        if (nodeComesBeforeChunk) {
            pos = StatementPos.PREV;
        }
        if (nodeComesAfterChunk) {
            pos = StatementPos.NEXT;
        }
        for (VarChar originalVarChar: originalVarChars.getTypes()) {
            for (VarChar fixedVarChar: fixedVarChars.getTypes()) {
                vector.add(pos, originalVarChar, fixedVarChar);
            }
        }
        return vector;
    }

}