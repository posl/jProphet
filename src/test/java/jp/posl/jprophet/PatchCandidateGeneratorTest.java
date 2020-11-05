package jp.posl.jprophet;

import org.junit.Test;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;
import jp.posl.jprophet.project.FileLocator;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;


public class PatchCandidateGeneratorTest {
    private PatchCandidateGenerator patchCandidateGenerator = new PatchCandidateGenerator();

    /**
     * テスト用のプロジェクトを用意し
     * 全テンプレートoperationが呼び出されて修正パッチ候補が生成されているかテスト 
     */
    @Test public void testForExec(){
        // 一つのファイルを持ったプロジェクトのスタブを生成
        String filePath = "src/test/resources/test01.java";
        List<FileLocator> fileLocatorsForTest = new ArrayList<FileLocator>(Arrays.asList(new FileLocator(filePath, "test01")));
        
        // 必ず一つのRepairUnitを返すAstOperationの匿名クラスを実装してGeneratorに注入
        AstOperation stubOperation = new AstOperation(){
            @Override
            public List<OperationDiff> exec(Node targetNode) {
                return List.of(new OperationDiff (ModifyType.NONE, targetNode, targetNode)); 
            }
        };

        List<Suspiciousness> suspiciousenesses = new ArrayList<Suspiciousness>();
        suspiciousenesses.add(new Suspiciousness("test01", 1, 1));
        suspiciousenesses.add(new Suspiciousness("test01", 2, 1));
        suspiciousenesses.add(new Suspiciousness("test01", 3, 1));
        suspiciousenesses.add(new Suspiciousness("test01", 4, 1));
        suspiciousenesses.add(new Suspiciousness("test01", 5, 1));
        suspiciousenesses.add(new Suspiciousness("test01", 6, 1)); 

        Map<FileLocator, CompilationUnit> fileLocatorMap = new HashMap<FileLocator, CompilationUnit>(); 
        for (FileLocator targetFileLocator: fileLocatorsForTest) {
            try {
                final List<String> lines = Files.readAllLines(Paths.get(targetFileLocator.getPath()), StandardCharsets.UTF_8);
                final String sourceCode = String.join("\n", lines);
                fileLocatorMap.put(targetFileLocator, JavaParser.parse(sourceCode));
            } catch (Exception e) {
                break;
            }
        }

        List<PatchCandidate> candidates = this.patchCandidateGenerator.exec(List.of(stubOperation), suspiciousenesses, fileLocatorMap);

        final int astNodeNumOfTest01 = 17;
        assertThat(candidates.size()).isEqualTo(astNodeNumOfTest01);
    }

    /**
     * 疑惑値が0の文のパッチを生成していないか
     */
    @Test public void testForZeroSuspiciousness(){
        // 一つのファイルを持ったプロジェクトのスタブを生成
        String filePath = "src/test/resources/test01.java";
        List<FileLocator> fileLocatorsForTest = new ArrayList<FileLocator>(Arrays.asList(new FileLocator(filePath, "test01")));
        
        // 必ず一つのRepairUnitを返すAstOperationの匿名クラスを実装してGeneratorに注入
        AstOperation stubOperation = new AstOperation(){
            @Override
            public List<OperationDiff> exec(Node targetNode) {
                return List.of(new OperationDiff (ModifyType.NONE, targetNode, targetNode)); 
            }
        };

        List<Suspiciousness> suspiciousenesses = new ArrayList<Suspiciousness>();
        suspiciousenesses.add(new Suspiciousness("test01", 1, 0));
        suspiciousenesses.add(new Suspiciousness("test01", 2, 0));
        suspiciousenesses.add(new Suspiciousness("test01", 3, 0));
        suspiciousenesses.add(new Suspiciousness("test01", 4, 1));
        suspiciousenesses.add(new Suspiciousness("test01", 5, 0));
        suspiciousenesses.add(new Suspiciousness("test01", 5, 0));

        Map<FileLocator, CompilationUnit> fileLocatorMap = new HashMap<FileLocator, CompilationUnit>(); 
        for (FileLocator targetFileLocator: fileLocatorsForTest) {
            try {
                final List<String> lines = Files.readAllLines(Paths.get(targetFileLocator.getPath()), StandardCharsets.UTF_8);
                final String sourceCode = String.join("\n", lines);
                fileLocatorMap.put(targetFileLocator, JavaParser.parse(sourceCode));
            } catch (Exception e) {
                break;
            }
        }
 
        List<PatchCandidate> candidates = this.patchCandidateGenerator.exec(List.of(stubOperation), suspiciousenesses, fileLocatorMap);

        final int lineOfPatchedStatement = 4;
        for (PatchCandidate candidate : candidates){
            assertThat(candidate.getLineNumber().get()).isEqualTo(lineOfPatchedStatement);
        }
    }
}