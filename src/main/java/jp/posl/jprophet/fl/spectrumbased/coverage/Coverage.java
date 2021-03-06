package jp.posl.jprophet.fl.spectrumbased.coverage;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;


/**
 * 1つのテストメソッドに対する1つのソースコードのカバレッジを格納
 */
public class Coverage {

    public enum Status {
        /**
         * Status flag for no items (value is 0x00).
         */
        EMPTY,
        /**
         * Status flag when all items are not covered (value is 0x01).
         */
        NOT_COVERED,
        /**
         * Status flag when all items are covered (value is 0x02).
         */
        COVERED,
        /**
         * Status flag when items are partly covered (value is 0x03). どういう時に起きるか不明．
         */
        PARTLY_COVERED
    }

    final private String className;
    final private List<Status> statuses;

    /**
     * 
     * className : Coverage計測対象のクラス名
     * statuses : Coverage計測の結果
     * @param classCoverage
     */
    public Coverage(IClassCoverage classCoverage) {
        this.className = classCoverage.getName().replaceAll("/", ".");
        this.statuses = convertClassCoverage(classCoverage);
    }

    /**
     * ClassCoverageに格納されたCoverageをList<Status>に変換する．
     * 実質enumの型変換やってるだけ．
     * 
     * @param classCoverage
     * @return
     */
    private List<Status> convertClassCoverage(IClassCoverage classCoverage) {
        final List<Coverage.Status> statuses = new ArrayList<>();
        for (int i = 1; i <= classCoverage.getLastLine(); i++) {
            final Coverage.Status status;
            final int s = classCoverage.getLine(i).getStatus();

            if (s == ICounter.EMPTY) {
                status = Coverage.Status.EMPTY;
            } else if (s == ICounter.FULLY_COVERED || s == ICounter.PARTLY_COVERED) {
                status = Coverage.Status.COVERED;
            } else if (s == ICounter.NOT_COVERED) {
                status = Coverage.Status.NOT_COVERED;
            } else {
                status = Coverage.Status.EMPTY;
            }
            statuses.add(status);
        }
        return statuses;
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String separator = " ";
        sb.append(className + "\n");
        for (int i = 0; i < statuses.size(); i++) {
            sb.append(String.format("%2d", i + 1));
            sb.append(separator);
        }
        sb.append("\n");
        for (Status status : statuses) {
            sb.append(String.format("%2d", status.ordinal()));
            sb.append(separator);
        }
        return sb.toString();
    }

    /**
     * 各行のカバレッジ(0,1,2,3)をHashMapで取得
     * @return 各行のカバレッジ
     */
    public HashMap<Integer, Status> getStatusesOfLine(){
        HashMap<Integer, Status> map = new HashMap<Integer, Status>();
        for (int k = 1; k <= statuses.size(); k++){
            map.put(k, statuses.get(k-1));
        }
        return map;                
    }

    /**
     * line行目のStatusを返す
     * @param line Statusを取得する行
     * @return line行目のStatus
     */
    public Status getStatus(final int line) {
        return statuses.get(line - 1);
    }

    /**
     * テスト対象のソースコードのクラス名を取得
     * @return テスト対象のソースコードのクラス名
     */
    public String getName(){ 
        return className;
    }

    /**
     * テスト対象のソースコードの行数を取得
     * @return テスト対象のソースコードの行数
     */
    public int getLength(){
        return statuses.size();
    }
}