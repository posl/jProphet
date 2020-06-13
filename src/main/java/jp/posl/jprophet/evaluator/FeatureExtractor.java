package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.patch.DefaultPatchCandidate;

public class FeatureExtractor {
    public FeatureVector extract(DefaultPatchCandidate patch) {
        Node originalRoot = patch.getOriginalCompilationUnit().findRootNode();
        Node fixedRoot = patch.getCompilationUnit().findRootNode();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalRoot, fixedRoot);
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final ModFeatureExtractor modFeatureExtractor = new ModFeatureExtractor();
        final Map<ProgramChank,ModFeature> modFeatureMap = modFeatureExtractor.extract(nodeWithDiffType, chanks);
        final StatementFeatureExtractor stmtFeatureExtractor = new StatementFeatureExtractor(originalRoot);
        modFeatureMap.keySet().stream()
            .forEach(chank -> {
                final ModFeature modFeature = modFeatureMap.get(chank);
                for(int line = chank.getBegin(); line <= chank.getEnd(); line++) {
                    final StatementFeature stmtFeature = stmtFeatureExtractor.extract(line);
                    this.toIndex(StmtPos.TARGET, stmtFeature, modFeature);
                }
                final int prev3StmtsBegin =  chank.getBegin() - 4;
                final int prev3StmtsEnd =  chank.getBegin() - 1;
                for(int line = prev3StmtsBegin; line <= prev3StmtsEnd; line++) {
                    if(line < 1) continue;
                    final StatementFeature stmtFeature = stmtFeatureExtractor.extract(line);
                    this.toIndex(StmtPos.PREV, stmtFeature, modFeature);
                }
                final int next3StmtsBegin =  chank.getEnd() + 1;
                final int next3StmtsEnd =  chank.getEnd() + 4;
                for(int line = next3StmtsBegin; line <= next3StmtsEnd; line++) {
                    if(line > originalRoot.getEnd().get().line) {
                        break;
                    }
                    final StatementFeature stmtFeature = stmtFeatureExtractor.extract(line);
                    this.toIndex(StmtPos.NEXT, stmtFeature, modFeature);
                }
            });

        return new FeatureVector();
    }

    enum StmtPos {
        TARGET,
        PREV,
        NEXT
    }

    private int toIndex(StmtPos stmtPos, StatementFeature stmtFeature, ModFeature modFeature) {
        return 0;
    }
}