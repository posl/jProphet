package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;

import jp.posl.jprophet.patch.PatchCandidate;

public class FeatureExtractor {

    public enum StatementPos {
        TARGET,
        PREV,
        NEXT
    }

    static class Statements {
        int begin;
        int end;
        StatementPos pos;
        public Statements(int begin, int end, StatementPos pos) {
            this.begin = begin;
            this.end   = end;
            this.pos   = pos;
        }
    }

    public FeatureVector extract(PatchCandidate patch) {
        Node originalRoot = patch.getOriginalCompilationUnit().findRootNode();
        Node fixedRoot = patch.getCompilationUnit().findRootNode();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalRoot, fixedRoot);
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final ModFeatureExtractor modFeatureExtractor = new ModFeatureExtractor();
        final Map<ProgramChank,ModFeature> modFeatureMap = modFeatureExtractor.extract(nodeWithDiffType, chanks);
        final StatementFeatureExtractor stmtFeatureExtractor = new StatementFeatureExtractor(originalRoot);
        final VariableFeatureExtractor varFeatureExtractor = new VariableFeatureExtractor();
        final List<NameExpr> originalVariables = originalRoot.findAll(NameExpr.class);
        final List<NameExpr> fixedVariables = fixedRoot.findAll(NameExpr.class);
        final FeatureVector vector = new FeatureVector();

        modFeatureMap.keySet().stream()
            .forEach(chank -> {
                final ModFeature modFeature = modFeatureMap.get(chank);
                final int prev3StmtsBegin = chank.getBegin() - 4;
                final int prev3StmtsEnd   = chank.getBegin() - 1;
                final int next3StmtsBegin = chank.getEnd()   + 1;
                final int next3StmtsEnd   = chank.getEnd()   + 4;
                List<Statements> statements = List.of(
                    new Statements(chank.getBegin(), chank.getEnd(), StatementPos.TARGET),
                    new Statements(prev3StmtsBegin,  prev3StmtsEnd,  StatementPos.PREV),
                    new Statements(next3StmtsBegin,  next3StmtsEnd,  StatementPos.NEXT)
                );
                statements.stream().forEach(stmts -> {
                    for (int line = stmts.begin; line <= stmts.end; line++) {
                        if (line < 1) {
                            continue;
                        }
                        if (line > originalRoot.getEnd().get().line) {
                            break;
                        }
                        final StatementFeature stmtFeature = stmtFeatureExtractor.extract(line);
                        vector.add(stmts.pos, stmtFeature, modFeature);

                        fixedVariables.stream()
                            .forEach(fixedVar -> {
                                final Node fixedVarDec = varFeatureExtractor.findDeclarator(fixedVar).orElseThrow();
                                final List<NameExpr> originalVarsWithSameName = originalVariables.stream()
                                    .filter(original -> original.getNameAsString().equals(fixedVar.getNameAsString()))
                                    .collect(Collectors.toList());
                                final List<NameExpr> originalVarsWithSameScope = originalVarsWithSameName.stream()
                                    .filter(original-> {
                                        final Node originalVarDec = varFeatureExtractor.findDeclarator(original).get();
                                        final boolean originalVarIsNotField = originalVarDec.findParent(MethodDeclaration.class).isPresent();
                                        final boolean fixedVarIsNotField = fixedVarDec.findParent(MethodDeclaration.class).isPresent();
                                        final boolean bothIsField = !originalVarIsNotField && !fixedVarIsNotField;
                                        if(bothIsField) return true;
                                        if(!bothIsField) {
                                            final String methodNameWhereOriginalVarWasDeclared = originalVarDec.findParent(MethodDeclaration.class).orElseThrow().getNameAsString();
                                            final String methodNameWhereFixedlVarWasDeclared = fixedVarDec.findParent(MethodDeclaration.class).orElseThrow().getNameAsString();
                                            if(methodNameWhereOriginalVarWasDeclared.equals(methodNameWhereFixedlVarWasDeclared)) {
                                                return true;
                                            }
                                        } 
                                        return false;
                                    })
                                    .collect(Collectors.toList());
                                NameExpr sameVarAsFixedVar;
                                if(originalVarsWithSameScope.size() > 0) {
                                    sameVarAsFixedVar = originalVarsWithSameScope.get(0);
                                    vector.add(StatementPos.TARGET, varFeatureExtractor.extract(fixedVar), varFeatureExtractor.extract(sameVarAsFixedVar));
                                }
                                else if(originalVarsWithSameName.size() > 0) {
                                    sameVarAsFixedVar = originalVarsWithSameName.get(0);
                                    vector.add(StatementPos.TARGET, varFeatureExtractor.extract(fixedVar), varFeatureExtractor.extract(sameVarAsFixedVar));
                                }
                            });
                    }
                });
            });

        return vector;
    }

}