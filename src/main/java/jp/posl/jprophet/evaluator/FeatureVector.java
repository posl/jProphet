package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jp.posl.jprophet.evaluator.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.ModFeature.ModType;
import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;
import jp.posl.jprophet.evaluator.StatementFeature.StatementType;
import jp.posl.jprophet.evaluator.VariableFeature.VarType;

public class FeatureVector {
    final private int size1 = StatementPos.values().length + StatementType.values().length + ModType.values().length;
    final private int size2 = StatementPos.values().length + VarType.values().length * 2;
    // final List<Long> vector;

    public FeatureVector() {
        // vector = new ArrayList<>(Collections.nCopies(size1 + size2, 0));
    }

    public void add(StatementPos stmtPos, StatementFeature stmtFeature, ModFeature modFeature) {
        final Long index = this.toIndex(stmtPos, stmtFeature, modFeature);
        // vector.set(index, 1);
        return;
    }

    public void add(StatementPos stmtPos, VariableFeature originalVarFeature, VariableFeature fixedVarFeature) {

    }
    
    private Long toIndex(StatementPos stmtPos, StatementFeature stmtFeature, ModFeature modFeature) {
        final int stmtOrdinal = stmtPos.ordinal();
        List<Integer> is = new ArrayList<>(Collections.nCopies(StatementPos.values().length, 0));
        is.set(stmtOrdinal, 1);

        final List<Integer> stmtTypeOrdinals = stmtFeature.getTypes().stream()
            .map(type -> type.ordinal()).collect(Collectors.toList());
        List<Integer> is1 = new ArrayList<>(Collections.nCopies(StatementType.values().length, 0));
        stmtTypeOrdinals.stream()
            .forEach(index -> is1.set(index, 1));

        final List<Integer> modTypeOrdinals = modFeature.getTypes().stream()
            .map(type -> type.ordinal()).collect(Collectors.toList());
        List<Integer> is2 = new ArrayList<>(Collections.nCopies(ModType.values().length, 0));
        modTypeOrdinals.stream()
            .forEach(index -> is2.set(index, 1));

        StringBuilder hoge = new StringBuilder();
        is.stream().forEach(i -> hoge.append(i.toString()));
        is1.stream().forEach(i -> hoge.append(i.toString()));
        is2.stream().forEach(i -> hoge.append(i.toString()));
        for(int i = 0; i < this.size2; i++) {
            hoge.append("0");
        }
        
        return Long.parseLong(hoge.toString(), 2);
    }

    private int toIndex(StatementPos stmtPos, VariableFeature originalVarFeature, VariableFeature fixedVarFeature) {
        return 0;
    }
}