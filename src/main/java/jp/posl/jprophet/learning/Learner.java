package jp.posl.jprophet.learning;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import difflib.Delta;
import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.PatchCandidateGenerator;
import jp.posl.jprophet.evaluator.AstDiff;
import jp.posl.jprophet.evaluator.extractor.FeatureExtractor;
import jp.posl.jprophet.evaluator.extractor.FeatureVector;
import jp.posl.jprophet.patch.DefaultPatch;
import jp.posl.jprophet.patch.Patch;

public class Learner {

    static class TrainingCase {
        final public FeatureVector vectorOfCorrectPatch;
        final public List<FeatureVector> vectorsOfGeneratedPatch;

        public TrainingCase(FeatureVector vectorOfCorrectPatch, List<FeatureVector> vectorsOfGeneratedPatch) {
            this.vectorOfCorrectPatch = vectorOfCorrectPatch;
            this.vectorsOfGeneratedPatch = vectorsOfGeneratedPatch;
        }
    }

    public List<TrainingCase> generateTrainingCase(TrainingConfig config) {
        final Path dirPath = Paths.get(config.getDirPath());
        List<Path> trainingCasePaths;
        try {
            trainingCasePaths = Files.list(dirPath).collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
        final List<Patch> patches = new ArrayList<Patch>();
        for (Path trainingCasePath: trainingCasePaths) {
            if (!Files.isDirectory(trainingCasePath)) {
                continue;
            }
            try {
                final Optional<Path> originalDirPath = Files.list(trainingCasePath)
                    .filter(path -> Files.isDirectory(path))
                    .filter(path -> path.getFileName().toString().equals(config.getOriginalDirName()))
                    .findFirst();
                final Optional<Path> fixedDirPath = Files.list(trainingCasePath)
                    .filter(path -> Files.isDirectory(path))
                    .filter(path -> path.getFileName().toString().equals(config.getFixedDirName()))
                    .findFirst();
                if (!originalDirPath.isPresent() || !fixedDirPath.isPresent()) {
                    continue;
                }
                final List<Path> pathesInOriginalDir = Files.list(originalDirPath.orElseThrow()).collect(Collectors.toList());
                final List<Path> pathesInFixedDir = Files.list(fixedDirPath.orElseThrow()).collect(Collectors.toList());
                if (!(pathesInOriginalDir.size() == 1) || !(pathesInFixedDir.size() == 1)) {
                    continue;
                }
                final Path originalFilePath = pathesInOriginalDir.get(0);
                final Path fixedFilePath = pathesInFixedDir.get(0);
                final String originalSourceCode = String.join("\n", Files.readAllLines(originalFilePath));
                final String fixedSourceCode = String.join("\n", Files.readAllLines(fixedFilePath));
                try {
                    patches.add(new DefaultPatch(originalSourceCode, fixedSourceCode));
                } catch (ParseProblemException e) {
                    continue;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        final FeatureExtractor extractor = new FeatureExtractor();
        final PatchCandidateGenerator patchGenerator = new PatchCandidateGenerator();
        final List<TrainingCase> trainingCases = new ArrayList<TrainingCase>();
        for (Patch patch : patches) {
            CompilationUnit originalCu = patch.getOriginalCompilationUnit();
            CompilationUnit fixedCu = patch.getCompilationUnit();
            // コメントの削除
            // コード変形後にコメントとノードが合体するとequal判定の結果が変わり，
            // 挿入したノードの検索が不可能になる
            NodeUtility.getAllNodesInDepthFirstOrder(originalCu.findRootNode()).stream()
                .forEach(n-> n.removeComment());
            NodeUtility.getAllNodesInDepthFirstOrder(fixedCu.findRootNode()).stream()
                .forEach(n-> n.removeComment());
            originalCu = JavaParser.parse(originalCu.toString());
            fixedCu = JavaParser.parse(fixedCu.toString());
            
            final List<Node> allNodesForPatchGeneration = this.getAllPatchedNodes(originalCu.findRootNode(), fixedCu.findRootNode());

            // ランダムな10個のパッチを使用
            Collections.shuffle(allNodesForPatchGeneration);
            final List<Patch> generatedPatches = allNodesForPatchGeneration.stream()
                .flatMap(node -> patchGenerator.applyTemplate(node, config.getOperations()).stream())
                .map(result -> result.getCompilationUnit())
                .map(cu -> new DefaultPatch(patch.getOriginalCompilationUnit(), cu))
                .limit(10)
                .collect(Collectors.toList());
            final List<FeatureVector> vectorsOfGeneratedPatches = generatedPatches.stream()
                .map(extractor::extract)
                .collect(Collectors.toList());
            trainingCases.add(new TrainingCase(extractor.extract(patch), vectorsOfGeneratedPatches));
        }
        return trainingCases;
    }

    private List<Node> getAllPatchedNodes(Node original, Node revised) {
        final List<Node> allOriginalNodes = NodeUtility.getAllNodesInDepthFirstOrder(original);
        final AstDiff astDiff = new AstDiff();
        final List<Delta<String>> deltas = astDiff.diff(original, revised);
        final List<Node> targetNodes = new ArrayList<Node>();
        for (Delta<String> delta : deltas) {
            final int diffPosition = delta.getOriginal().getPosition(); 
            if (diffPosition >= allOriginalNodes.size()) {
                continue;        
            }
            targetNodes.add(allOriginalNodes.get(diffPosition));
        }
        return targetNodes;
    }
}