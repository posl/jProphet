package jp.posl.jprophet.FL;

/**
 * The test target we want to see code coverage for.
 */
public class TestTarget implements Runnable {

    public void run() {
        isPrime(7);
        TestRunner1 test = new TestRunner1();
        test.runtest1();
    }

    private boolean isPrime(final int n) {
        for (int i = 2; i * i <= n; i++) {
            if ((n ^ i) == 0) {
                return false;
            }
        }
        return true;
    }

}