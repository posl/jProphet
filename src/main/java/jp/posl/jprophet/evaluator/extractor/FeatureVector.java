package jp.posl.jprophet.evaluator.extractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.posl.jprophet.evaluator.extractor.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.extractor.StatementKindExtractor.StatementType;
import jp.posl.jprophet.evaluator.extractor.feature.ModKinds.ModKind;
import jp.posl.jprophet.evaluator.extractor.feature.VariableKinds.VarKind;

public class FeatureVector {
    final List<Boolean> modTypeVector;
    final List<Boolean> modKindVector;
    final List<Boolean> varFeatureVector;

    final private int numOfStmtPosTypes = StatementPos.values().length;
    final private int numOfStmtTypes = StatementType.values().length;
    final private int numOfModTypes = ModKind.values().length;
    final private int numOfVarTypes = VarKind.values().length;

    public FeatureVector() {
        this.modTypeVector = new ArrayList<>(Collections.nCopies(numOfModTypes, false));
        this.modKindVector = new ArrayList<>(Collections.nCopies(numOfStmtPosTypes * numOfStmtTypes * numOfModTypes, false));
        this.varFeatureVector = new ArrayList<>(Collections.nCopies(numOfStmtPosTypes * numOfVarTypes * numOfVarTypes, false));
    }

    public List<Boolean> get() {
        List<Boolean> vector = new ArrayList<>();
        vector.addAll(this.modTypeVector);
        vector.addAll(this.modKindVector);
        vector.addAll(this.varFeatureVector);
        return vector;
    }

    public void add(ModKind modType) {
        final int index = modType.ordinal();        
        this.modTypeVector.set(index, true);
    }

    public void add(StatementPos stmtPos, StatementType stmtType, ModKind modType) {
        final int index = this.toIndex(stmtPos, stmtType, modType);
        this.modKindVector.set(index, true);
    }

    public void add(StatementPos stmtPos, VarKind originalVarType, VarKind fixedVarType) {
        final int index =  this.toIndex(stmtPos, originalVarType, fixedVarType);
        this.varFeatureVector.set(index, true);
    }
    
    private int toIndex(StatementPos stmtPos, StatementType stmtType, ModKind modType) {
        final int stmtPosOrdinal = stmtPos.ordinal();
        final int stmtTypeOrdinal = stmtType.ordinal();
        final int modTypeOrdinal = modType.ordinal();

        final int index = stmtPosOrdinal + (numOfStmtPosTypes * stmtTypeOrdinal) + (numOfStmtPosTypes * numOfStmtTypes * modTypeOrdinal);
        return index;
    }

    private int toIndex(StatementPos stmtPos, VarKind originalVarType, VarKind fixedVarType) {
        final int stmtPosOrdinal = stmtPos.ordinal();
        final int originalVarTypeOrdinal = originalVarType.ordinal();
        final int fixedVarTypeOrdinal = fixedVarType.ordinal();

        final int index = stmtPosOrdinal + (numOfStmtPosTypes * originalVarTypeOrdinal) + (numOfStmtPosTypes * numOfVarTypes * fixedVarTypeOrdinal);
        return index;
    }
}