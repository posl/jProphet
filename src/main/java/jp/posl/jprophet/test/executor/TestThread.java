package jp.posl.jprophet.test.executor;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

/**
 * junitによるテスト実行をスレッドで実行する
 */
public class TestThread extends Thread {
    private JUnitCore junitCore;
    private Class<?> testClass;
    private String methodName;
    private boolean isSingleRun;
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
        this.isSingleRun = false;
    }

    public TestThread(JUnitCore junitCore, Class<?> testClass, String methodName){
        this.junitCore = junitCore;
        this.testClass = testClass;
        this.isSuccess = false;
        this.methodName = methodName;
        this.isSingleRun = true;
    }

    @Override
    public void run(){
        if (isSingleRun) {
            this.isSuccess = junitCore.run(Request.method(testClass, methodName)).wasSuccessful();
        } else {
            this.isSuccess = junitCore.run(testClass).wasSuccessful();
        }
    }

    public boolean getIsSuccess(){
        return this.isSuccess;
    }
}