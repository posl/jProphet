package jp.posl.jprophet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.project.FileLocator;

public class AstGenerator {
    
    public List<CompilationUnit> exec(List<Suspiciousness> suspiciousenesses, List<FileLocator> fileLocators) {
        final List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
        List<String> excutedSourceFqn = suspiciousenesses.stream()
            .filter(s -> s.getValue() > 0)
            .map(s -> s.getFQN())
            .distinct()
            .collect(Collectors.toList());
        
        List<String> executedSourcePaths = new ArrayList<String>();
        for (String fqn : excutedSourceFqn) {
            fileLocators.stream()
                .filter(f -> f.getFqn().equals(fqn))
                .map(f -> f.getPath())
                .findFirst().ifPresent(s -> executedSourcePaths.add(s));
        }

        for (String executedSourcePath: executedSourcePaths) {
            try {
                final List<String> lines = Files.readAllLines(Paths.get(executedSourcePath), StandardCharsets.UTF_8);
                final String sourceCode = String.join("\n", lines);
                compilationUnits.add(JavaParser.parse(sourceCode));
            } catch (Exception e) {
                break;
            }
        }

        return compilationUnits;

    }
}