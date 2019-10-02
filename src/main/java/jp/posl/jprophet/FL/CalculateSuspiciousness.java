package jp.posl.jprophet.FL;

import java.util.List;
import java.util.ArrayList;

public class CalculateSuspiciousness {

    public int filenum;
    final public List<Suspiciousness> slist;

    /**
     * テスト結果(カバレッジ情報を含む)から,全てのテスト対象ファイルの各行ごとの疑惑値を計算
     * @param testresults テスト結果
     */
    public CalculateSuspiciousness(TestResults testresults){
        this.filenum = testresults.getTestResults().get(0).getCoverages().size();
        List<Suspiciousness> list = new ArrayList<Suspiciousness>();
        int linelength;
        String testname;

        int NS = testresults.getSuccessedTestResults().size();
        int NF = testresults.getFailedTestResults().size();

        
        for (int i = 0; i < filenum; i++){
            
            //TODO 1つめのメソッドのカバレッジ結果からソースファイルの行数とファイル名を取得している. 他にいい取得方法はないか
            linelength = testresults.getTestResults().get(0).getCoverages().get(i).getLength();
            testname = testresults.getTestResults().get(0).getCoverages().get(i).getName();
            
            for (int k = 1; k <= linelength; k++){
                LineStatus linestatus = new LineStatus(testresults, k, i);
                Suspiciousness suspiciousness = new Suspiciousness(testname, k, jaccard((double)linestatus.NCF, (double)linestatus.NUF, (double)linestatus.NCS, (double)linestatus.NUS, (double)linestatus.NC, (double)linestatus.NU, (double)NS, (double)NF));
                //確認用print
                System.out.println("FQDN           = " + suspiciousness.getPath());
                System.out.println("Line           = " + suspiciousness.getLine());
                System.out.println("Suspiciousness = "+ suspiciousness.getValue() + "\n");
                list.add(suspiciousness);
            }
        }
        
        this.slist = list;
    }

    public Double ochiai(double NCF, double NUF, double NCS, double NUS, double NC, double NU, double NS, double NF){
        //疑惑地の計算
        double suspiciousenesses;
        suspiciousenesses = NCF * 1000 + NUF * 100 + NCS * 10 + NUS;
        return suspiciousenesses;
    }

    public Double jaccard(double NCF, double NUF, double NCS, double NUS, double NC, double NU, double NS, double NF){
        double suspiciousenesses;
        suspiciousenesses = NCF / (NCF + NUF + NCS);
        return suspiciousenesses;
    }

    public Double Tarantula(double NCF, double NUF, double NCS, double NUS, double NC, double NU, double NS, double NF){
        double suspiciousenesses;
        suspiciousenesses = (NCF/NF)/(NCF/NF+NCS/NS);
        return suspiciousenesses;
    }

}