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
    final List<Boolean> modTypeVector;
    final List<Boolean> modFeatureVector;
    final List<Boolean> varFeatureVector;

    final private int numOfStmtPosTypes = StatementPos.values().length;
    final private int numOfStmtTypes = StatementType.values().length;
    final private int numOfModTypes = ModType.values().length;
    final private int numOfVarTypes = VarType.values().length;

    public FeatureVector() {
        this.modTypeVector = new ArrayList<>(Collections.nCopies(numOfModTypes, false));
        this.modFeatureVector = new ArrayList<>(Collections.nCopies(numOfStmtPosTypes * numOfStmtTypes * numOfModTypes, false));
        this.varFeatureVector = new ArrayList<>(Collections.nCopies(numOfStmtPosTypes * numOfVarTypes * numOfVarTypes, false));
    }

    public List<Boolean> get() {
        List<Boolean> vector = new ArrayList<>();
        vector.addAll(this.modTypeVector);
        vector.addAll(this.modFeatureVector);
        vector.addAll(this.varFeatureVector);
        return vector;
    }

    public void add(ModType modType) {
        final int index = modType.ordinal();        
        this.modTypeVector.set(index, true);
    }

    public void add(StatementPos stmtPos, StatementType stmtType, ModType modType) {
        final int index = this.toIndex(stmtPos, stmtType, modType);
        this.modFeatureVector.set(index, true);
    }

    public void add(StatementPos stmtPos, VarType originalVarType, VarType fixedVarType) {
        final int index =  this.toIndex(stmtPos, originalVarType, fixedVarType);
        this.varFeatureVector.set(index, true);
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