package jp.posl.jprophet.learning;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

import difflib.Delta;
import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.PatchCandidateGenerator;
import jp.posl.jprophet.evaluator.AstDiff;
import jp.posl.jprophet.evaluator.extractor.FeatureExtractor;
import jp.posl.jprophet.patch.DefaultPatch;
import jp.posl.jprophet.patch.Patch;

public class Learner {

    static class TrainingCase {
        final public List<Boolean> vectorOfCorrectPatch;
        final public List<List<Boolean>> vectorsOfGeneratedPatch;

        public TrainingCase(List<Boolean> vectorOfCorrectPatch, List<List<Boolean>> vectorsOfGeneratedPatch) {
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
                patches.add(new DefaultPatch(originalSourceCode, fixedSourceCode));
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        final FeatureExtractor extractor = new FeatureExtractor();
        final PatchCandidateGenerator patchGenerator = new PatchCandidateGenerator();
        final List<TrainingCase> trainingCases = new ArrayList<TrainingCase>();
        for (Patch patch : patches) {
            final Node originalRootNode = patch.getOriginalCompilationUnit().findRootNode();
            final Node fixedRootNode = patch.getCompilationUnit().findRootNode();
            
            final List<Node> allNodesForPatchGeneration = this.getAllPatchedNodes(originalRootNode, fixedRootNode);
            final List<Patch> allGeneratedPatches = allNodesForPatchGeneration.stream()
                .flatMap(node -> patchGenerator.applyTemplate(node, config.getOperations()).stream())
                .map(result -> result.getCompilationUnit())
                .map(cu -> new DefaultPatch(patch.getOriginalCompilationUnit(), cu))
                .collect(Collectors.toList());
            final List<List<Boolean>> vectorsOfGeneratedPatches = allGeneratedPatches.stream()
                .map(extractor::extract)
                .map(vector -> vector.get())
                .collect(Collectors.toList());
            trainingCases.add(new TrainingCase(extractor.extract(patch).get(), vectorsOfGeneratedPatches));
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
            if (diffPosition > allOriginalNodes.size()) {
                continue;        
            }
            targetNodes.add(allOriginalNodes.get(diffPosition));
        }
        return targetNodes;
    }
}