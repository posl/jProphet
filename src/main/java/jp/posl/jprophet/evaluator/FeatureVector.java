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
    final List<Integer> modFeatureVector;
    final List<Integer> varFeatureVector;

    final private int numOfStmtPosTypes = StatementPos.values().length;
    final private int numOfStmtTypes = StatementType.values().length;
    final private int numOfModTypes = ModType.values().length;
    final private int numOfVarTypes = VarType.values().length;

    public FeatureVector() {
        this.modFeatureVector = new ArrayList<>(Collections.nCopies(numOfStmtPosTypes * numOfStmtTypes * numOfModTypes, 0));
        this.varFeatureVector = new ArrayList<>(Collections.nCopies(numOfStmtPosTypes * numOfVarTypes * numOfVarTypes, 0));
    }

    public List<Integer> get() {
        List<Integer> vector = new ArrayList<>();
        vector.addAll(this.modFeatureVector);
        vector.addAll(this.varFeatureVector);
        return vector;
    }

    public void add(StatementPos stmtPos, StatementType stmtType, ModType modType) {
        final int index = this.toIndex(stmtPos, stmtType, modType);
        this.modFeatureVector.set(index, 1);
    }

    public void add(StatementPos stmtPos, VarType originalVarType, VarType fixedVarType) {
        final int index =  this.toIndex(stmtPos, originalVarType, fixedVarType);
        this.varFeatureVector.set(index, 1);
    }
    
    private int toIndex(StatementPos stmtPos, StatementType stmtType, ModType modType) {
        final int stmtPosOrdinal = stmtPos.ordinal();
        final int stmtTypeOrdinal = stmtType.ordinal();
        final int modTypeOrdinal = modType.ordinal();

        final int index = stmtPosOrdinal + (numOfStmtPosTypes * stmtTypeOrdinal) + (numOfStmtPosTypes * numOfStmtTypes * modTypeOrdinal);
        return index;
    }

    private int toIndex(StatementPos stmtPos, VarType originalVarType, VarType fixedVarType) {
        final int stmtPosOrdinal = stmtPos.ordinal();
        final int originalVarTypeOrdinal = originalVarType.ordinal();
        final int fixedVarTypeOrdinal = fixedVarType.ordinal();

        final int index = stmtPosOrdinal + (numOfStmtPosTypes * originalVarTypeOrdinal) + (numOfStmtPosTypes * numOfVarTypes * fixedVarTypeOrdinal);
        return index;
    }
}