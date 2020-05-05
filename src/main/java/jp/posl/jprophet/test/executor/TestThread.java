package jp.posl.jprophet.test.executor;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * junitによるテスト実行をスレッドで実行する
 */
public class TestThread extends Thread {
    private JUnitCore junitCore;
    private Class<?> testClass;
    public boolean isSuccess;

    /**
     * 
     * @param junitCore
     * @param testClass
     */
    public TestThread(JUnitCore junitCore, Class<?> testClass){
        this.junitCore = junitCore;
        this.testClass = testClass;
        this.isSuccess = false;
    }

    @Override
    public void run(){
        this.isSuccess = junitCore.run(testClass).wasSuccessful();
    }

    public boolean getIsSuccess(){
        return this.isSuccess;
    }
}