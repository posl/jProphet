package jp.posl.jprophet;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.project.FileLocator;

/**
 * ASTを生成する
 */
public class AstGenerator {
    
    /**
     * 疑惑値が0より大きい行が１行でも存在するファイル(クラス)を全てASTに変換する
     * @param suspiciousenesses 疑惑値のリスト
     * @param fileLocators ファイル情報のリスト
     * @return ファイル情報とCompilationUnitのMap
     */
    public Map<FileLocator,CompilationUnit> exec(List<Suspiciousness> suspiciousenesses, List<FileLocator> fileLocators) {
        Map<FileLocator, CompilationUnit> fileLocatorMap = new HashMap<FileLocator, CompilationUnit>();
        List<String> excutedSourceFqn = suspiciousenesses.stream()
            .filter(s -> s.getValue() > 0)
            .map(s -> s.getFQN())
            .distinct()
            .collect(Collectors.toList());
        
        List<FileLocator> targetFileLocators = new ArrayList<FileLocator>();
        for (String fqn : excutedSourceFqn) {
            fileLocators.stream()
                .filter(f -> f.getFqn().equals(fqn))
                .findFirst().ifPresent(s -> targetFileLocators.add(s));
        }

        for (FileLocator targetFileLocator: targetFileLocators) {
            try {
                final List<String> lines = Files.readAllLines(Paths.get(targetFileLocator.getPath()), StandardCharsets.UTF_8);
                final String sourceCode = String.join("\n", lines);
                fileLocatorMap.put(targetFileLocator, JavaParser.parse(sourceCode));
            } catch (Exception e) {
                break;
            }
        }

        return fileLocatorMap;

    }
}