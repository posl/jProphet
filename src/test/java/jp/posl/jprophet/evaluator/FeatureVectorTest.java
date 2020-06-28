package jp.posl.jprophet.evaluator;

import org.junit.Test;

import jp.posl.jprophet.evaluator.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.ModFeature.ModType;
import jp.posl.jprophet.evaluator.StatementFeature.StatementType;
import jp.posl.jprophet.evaluator.VariableFeature.VarType;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeatureVectorTest {
    final private int numOfStmtPosTypes = StatementPos.values().length;
    final private int numOfStmtTypes = StatementType.values().length;
    final private int numOfModTypes = ModType.values().length;
    final private int numOfVarTypes = VarType.values().length;
    final private int modFeatureVectorSize = numOfStmtPosTypes * numOfStmtTypes * numOfModTypes;
    final private int varFeatureVectorSize = numOfStmtPosTypes * numOfVarTypes * numOfVarTypes;
    final private int vectorSize = modFeatureVectorSize + varFeatureVectorSize;

    //TODO
    @Test public void hoge() {
        FeatureVector vector = new FeatureVector();
        final VarType originalVarType = VarType.NONCOMMUTATIVE_OPERAND_RIGHT;
        final VarType fixedVarType = VarType.NONCOMMUTATIVE_OPERAND_RIGHT;
        vector.add(StatementPos.NEXT, originalVarType, fixedVarType);

        final List<Integer> expectVector = new ArrayList<>(Collections.nCopies(vectorSize, 0));
        expectVector.set(vectorSize - 1, 1);
        assertThat(vector.get()).containsExactlyElementsOf(expectVector);
    }

    //TODO
    @Test public void hoge2() {
        FeatureVector vector = new FeatureVector();
        final VarType originalVarType = VarType.BOOLEAN;
        final VarType fixedVarType = VarType.BOOLEAN;
        vector.add(StatementPos.TARGET, originalVarType, fixedVarType);

        final List<Integer> expectVector = new ArrayList<>(Collections.nCopies(vectorSize, 0));
        expectVector.set(modFeatureVectorSize, 1);
        assertThat(vector.get()).containsExactlyElementsOf(expectVector);
    }

    //TODO
    @Test public void hoge3() {
        FeatureVector vector = new FeatureVector();
        final StatementType stmtType = StatementType.ASSIGN;
        final ModType modType = ModType.INSERT_CONTROL;
        vector.add(StatementPos.TARGET, stmtType, modType);

        final List<Integer> expectVector = new ArrayList<>(Collections.nCopies(vectorSize, 0));
        expectVector.set(0, 1);
        assertThat(vector.get()).containsExactlyElementsOf(expectVector);
    }

    //TODO
    @Test public void hoge4() {
        FeatureVector vector = new FeatureVector();
        final StatementType stmtType = StatementType.CONTINUE;
        final ModType modType = ModType.INSERT_STMT;
        vector.add(StatementPos.NEXT, stmtType, modType);

        final List<Integer> expectVector = new ArrayList<>(Collections.nCopies(vectorSize, 0));
        expectVector.set(modFeatureVectorSize - 1, 1);
        assertThat(vector.get()).containsExactlyElementsOf(expectVector);
    }
}