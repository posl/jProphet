package jp.posl.jprophet.evaluator.extractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.posl.jprophet.evaluator.extractor.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.extractor.StatementKindExtractor.StatementKind;
import jp.posl.jprophet.evaluator.extractor.ModKinds.ModKind;
import jp.posl.jprophet.evaluator.extractor.VariableCharacteristics.VarChar;

/**
 * パッチの特徴ベクトルを表現するクラス
 */
public class FeatureVector {
    final List<Boolean> modKindVector;
    final List<Boolean> modFeatureVector;
    final List<Boolean> varFeatureVector;

    final private int numOfStmtPosTypes = StatementPos.values().length;
    final private int numOfStmtTypes = StatementKind.values().length;
    final private int numOfModTypes = ModKind.values().length;
    final private int numOfVarTypes = VarChar.values().length;

    public FeatureVector() {
        this.modKindVector = new ArrayList<>(Collections.nCopies(numOfModTypes, false));
        this.modFeatureVector = new ArrayList<>(Collections.nCopies(numOfStmtPosTypes * numOfStmtTypes * numOfModTypes, false));
        this.varFeatureVector = new ArrayList<>(Collections.nCopies(numOfStmtPosTypes * numOfVarTypes * numOfVarTypes, false));
    }

    /**
     * バイナリベクトルとして取得
     * @return ベクトル
     */
    public List<Boolean> get() {
        List<Boolean> vector = new ArrayList<>();
        vector.addAll(this.modKindVector);
        vector.addAll(this.modFeatureVector);
        vector.addAll(this.varFeatureVector);
        return vector;
    }

    /**
     * ベクトルを加算する(論理和)
     * @param vector 加算ベクトル
     */
    public void add(FeatureVector vector) {
        for(int i = 0; i < this.modKindVector.size(); i++) {
            if (vector.modKindVector.get(i) == true) {
                this.modKindVector.set(i, true);
            }
        }
        for(int i = 0; i < this.modFeatureVector.size(); i++) {
            if (vector.modFeatureVector.get(i) == true) {
                this.modFeatureVector.set(i, true);
            }
        }
        for(int i = 0; i < this.varFeatureVector.size(); i++) {
            if (vector.varFeatureVector.get(i) == true) {
                this.varFeatureVector.set(i, true);
            }
        }
    }

    /**
     * 特徴の追加
     * @param modKind 変更の種類
     */
    public void add(ModKind modKind) {
        final int index = modKind.ordinal();        
        this.modKindVector.set(index, true);
    }

    /**
     * 特徴の追加
     * @param stmtPos ステートの位置
     * @param stmtKind ステートの種類
     * @param modKind 変更の種類
     */
    public void add(StatementPos stmtPos, StatementKind stmtKind, ModKind modKind) {
        final int index = this.toIndex(stmtPos, stmtKind, modKind);
        this.modFeatureVector.set(index, true);
    }

    /**
     * 特徴の追加
     * @param stmtPos ステートのいち
     * @param originalVarChar 修正前コードの変数の種別
     * @param fixedVarChar 修正後コードの変数の種別
     */
    public void add(StatementPos stmtPos, VarChar originalVarChar, VarChar fixedVarChar) {
        final int index =  this.toIndex(stmtPos, originalVarChar, fixedVarChar);
        this.varFeatureVector.set(index, true);
    }
    
    /**
     * ステートの種類と変更の種類とステート位置を元にインデックス(整数値)に変換
     * @param stmtPos ステートの位置
     * @param stmtKind ステートの種類
     * @param modKind 変更の種類
     * @return インデックス
     */
    private int toIndex(StatementPos stmtPos, StatementKind stmtKind, ModKind modKind) {
        final int stmtPosOrdinal = stmtPos.ordinal();
        final int stmtTypeOrdinal = stmtKind.ordinal();
        final int modTypeOrdinal = modKind.ordinal();

        final int index = stmtPosOrdinal + (numOfStmtPosTypes * stmtTypeOrdinal) + (numOfStmtPosTypes * numOfStmtTypes * modTypeOrdinal);
        return index;
    }

    /**
     * ステートの位置と変数の特徴を元にインデックス(整数値)に変換
     * @param stmtPos ステートのいち
     * @param originalVarChar 修正前コードの変数の種別
     * @param fixedVarChar 修正後コードの変数の種別
     * @return インデックス
     */
    private int toIndex(StatementPos stmtPos, VarChar originalVarChar, VarChar fixedVarChar) {
        final int stmtPosOrdinal = stmtPos.ordinal();
        final int originalVarTypeOrdinal = originalVarChar.ordinal();
        final int fixedVarTypeOrdinal = fixedVarChar.ordinal();

        final int index = stmtPosOrdinal + (numOfStmtPosTypes * originalVarTypeOrdinal) + (numOfStmtPosTypes * numOfVarTypes * fixedVarTypeOrdinal);
        return index;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MK: ");
        for (int i = 0; i < this.modKindVector.size(); i++) {
            if (this.modKindVector.get(i) == true) {
                sb.append(i).append(", ");
            }
        }
        sb.append(" MF: ");
        for (int i = 0; i < this.modFeatureVector.size(); i++) {
            if (this.modFeatureVector.get(i) == true) {
                sb.append(i).append(", ");
            }
        }
        sb.append(" VF: ");
        for (int i = 0; i < this.varFeatureVector.size(); i++) {
            if (this.varFeatureVector.get(i) == true) {
                sb.append(i).append(", ");
            }
        }
        return sb.toString();
    }
}