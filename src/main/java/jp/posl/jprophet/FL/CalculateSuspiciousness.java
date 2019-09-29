package jp.posl.jprophet.FL;

import java.util.List;
import java.util.ArrayList;

public class CalculateSuspiciousness {

    public int filenum;
    final public List<Suspiciousness> slist;

    public CalculateSuspiciousness(TestResults testresults){
        this.filenum = testresults.getSuccessedTestResults().get(0).getCoverages().size();
        List<Suspiciousness> list = new ArrayList<Suspiciousness>();
        int linelength;
        String testname;

        for (int i = 0; i < filenum; i++){
            linelength = testresults.getSuccessedTestResults().get(0).getCoverages().get(i).getLength();
            testname = testresults.getSuccessedTestResults().get(0).getCoverages().get(i).getName();

            for (int k = 1; k < linelength; k++){
                LineStatus linestatus = new LineStatus(testresults, k, i);
                Suspiciousness suspiciousness = new Suspiciousness(testname, k, ochiai(linestatus.NCF, linestatus.NUF, linestatus.NCS, linestatus.NUS));
                //確認用print
                System.out.println(suspiciousness.getPath());
                System.out.println(suspiciousness.getLine());
                System.out.println(suspiciousness.getValue());
                list.add(suspiciousness);
            }
        }
        this.slist = list;
    }

    public Double ochiai(int NCF, int NUF, int NCS, int NUS){
        //疑惑地の計算
        double suspiciousenesses;
        suspiciousenesses = NCF * 1000 + NUF * 100 + NCS * 10 + NUS;
        return suspiciousenesses;
    }

}