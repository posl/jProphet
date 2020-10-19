package jp.posl.jprophet.evaluator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.patch.PatchCandidate;

public class PatchEvaluatorTest {
    /**
     * 学習済みパラメータを用いない場合
     */
    @Test public void testWhenSortedBySuspiciousness(){
        final List<PatchCandidate> candidates = new ArrayList<PatchCandidate>();
        final PatchCandidate candidate1 = mock(PatchCandidate.class);
        when(candidate1.getLineNumber()).thenReturn(Optional.of(1));
        when(candidate1.getFqn()).thenReturn("a");
        final PatchCandidate candidate2 = mock(PatchCandidate.class);
        when(candidate2.getLineNumber()).thenReturn(Optional.of(2));
        when(candidate2.getFqn()).thenReturn("b");
        final PatchCandidate candidate3 = mock(PatchCandidate.class);
        when(candidate3.getLineNumber()).thenReturn(Optional.of(3));
        when(candidate3.getFqn()).thenReturn("c");

        candidates.add(candidate1);
        candidates.add(candidate2);
        candidates.add(candidate3);

        final List<Suspiciousness> suspiciousenesses = new ArrayList<Suspiciousness>();
        suspiciousenesses.add(new Suspiciousness("a", 1, 2));
        suspiciousenesses.add(new Suspiciousness("b", 2, 1));
        suspiciousenesses.add(new Suspiciousness("c", 3, 3));

        final PatchEvaluator evaluator = new PatchEvaluator();
        final RepairConfiguration config = new RepairConfiguration(null, null, null);
        final List<PatchCandidate> sortedCandidates = evaluator.sort(candidates, suspiciousenesses, config);

        assertThat(sortedCandidates.get(0)).isEqualTo(candidate3);
        assertThat(sortedCandidates.get(1)).isEqualTo(candidate1);
        assertThat(sortedCandidates.get(2)).isEqualTo(candidate2);
    }

    /**
     * 学習済みパラメータを用いる場合
     */
    @Test public void testWhenSortedByScore() {
        final String originalSource = new StringBuilder().append("")
            .append("public class A {\n") 
            .append("    private void ma() {\n")
            .append("       return;\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        final String fixedSource1 = new StringBuilder().append("")
            .append("public class A {\n") 
            .append("    private void ma() {\n")
            .append("       hoge();\n")
            .append("       return;\n")
            .append("    }\n")
            .append("}\n").toString();
        final String fixedSource2 = new StringBuilder().append("")
            .append("public class A {\n") 
            .append("    private void ma() {\n")
            .append("       if (true)\n")
            .append("           return;\n")
            .append("    }\n")
            .append("}\n").toString();
        final String fixedSource3 = new StringBuilder().append("")
            .append("public class A {\n") 
            .append("    private void ma() {\n")
            .append("       hoge();\n")
            .append("       return;\n")
            .append("    }\n")
            .append("}\n").toString();
        final Node targetNodeBeforeFix = NodeUtility.getAllNodesFromCode(originalSource).get(6);
        final CompilationUnit fixedCu1 = JavaParser.parse(fixedSource1);
        final CompilationUnit fixedCu2 = JavaParser.parse(fixedSource2);
        final CompilationUnit fixedCu3 = JavaParser.parse(fixedSource3);
        final List<PatchCandidate> candidates = List.of(
            new PatchCandidate(targetNodeBeforeFix, fixedCu1, null, "a", null, 0),
            new PatchCandidate(targetNodeBeforeFix, fixedCu2, null, "b", null, 0),
            new PatchCandidate(targetNodeBeforeFix, fixedCu3, null, "c", null, 0)
        );
        final List<Suspiciousness> suspiciousenesses = List.of(
            new Suspiciousness("a", 3, 1),
            new Suspiciousness("b", 3, 0.5),
            new Suspiciousness("c", 3, 0.1)
        );
        final RepairConfiguration config = new RepairConfiguration(null, null, null, "parameters/para.csv");
        final PatchEvaluator evaluator = new PatchEvaluator();
        final List<PatchCandidate> sortedPatches = evaluator.sort(candidates, suspiciousenesses, config);

        assertThat(sortedPatches.get(0).getFqn()).contains("a");
        assertThat(sortedPatches.get(1).getFqn()).contains("c");
        assertThat(sortedPatches.get(2).getFqn()).contains("b");
    }
}

