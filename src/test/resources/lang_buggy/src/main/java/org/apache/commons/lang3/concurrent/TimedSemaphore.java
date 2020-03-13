















package org.apache.commons.lang3.concurrent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;









































































































public class TimedSemaphore {





    public static final int NO_LIMIT = 0;

    
    private static final int THREAD_POOL_SIZE = 1;

    
    private final ScheduledExecutorService executorService;

    
    private final long period;

    
    private final TimeUnit unit;

    
    private final boolean ownExecutor;

    
    private ScheduledFuture<?> task; 

    
    private long totalAcquireCount; 





    private long periodCount; 

    
    private int limit; 

    
    private int acquireCount;  

    
    private int lastCallsPerPeriod; 

    
    private boolean shutdown;  










    public TimedSemaphore(final long timePeriod, final TimeUnit timeUnit, final int limit) {
        this(null, timePeriod, timeUnit, limit);
    }













    public TimedSemaphore(final ScheduledExecutorService service, final long timePeriod,
            final TimeUnit timeUnit, final int limit) {
        if (timePeriod <= 0) {
            throw new IllegalArgumentException("Time period must be greater 0!");
        }

        period = timePeriod;
        unit = timeUnit;

        if (service != null) {
            executorService = service;
            ownExecutor = false;
        } else {
            final ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(
                    THREAD_POOL_SIZE);
            s.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            s.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            executorService = s;
            ownExecutor = true;
        }

        setLimit(limit);
    }








    public final synchronized int getLimit() {
        return limit;
    }











    public final synchronized void setLimit(final int limit) {
        this.limit = limit;
    }






    public synchronized void shutdown() {
        if (!shutdown) {

            if (ownExecutor) {
                
                
                getExecutorService().shutdownNow();
            }
            if (task != null) {
                task.cancel(false);
            }

            shutdown = true;
        }
    }








    public synchronized boolean isShutdown() {
        return shutdown;
    }












    public synchronized void acquire() throws InterruptedException {
        if (isShutdown()) {
            throw new IllegalStateException("TimedSemaphore is shut down!");
        }

        if (task == null) {
            task = startTimer();
        }

        boolean canPass = false;
        do {
            canPass = getLimit() <= NO_LIMIT || acquireCount < getLimit();
            if (!canPass) {
                wait();
            } else {
                acquireCount++;
            }
        } while (!canPass);
    }











    public synchronized int getLastAcquiresPerPeriod() {
        return lastCallsPerPeriod;
    }







    public synchronized int getAcquireCount() {
        return acquireCount;
    }












    public synchronized int getAvailablePermits() {
        return getLimit() - getAcquireCount();
    }










    public synchronized double getAverageCallsPerPeriod() {
        return periodCount == 0 ? 0 : (double) totalAcquireCount
                / (double) periodCount;
    }








    public long getPeriod() {
        return period;
    }






    public TimeUnit getUnit() {
        return unit;
    }






    protected ScheduledExecutorService getExecutorService() {
        return executorService;
    }








    protected ScheduledFuture<?> startTimer() {
        return getExecutorService().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                endOfPeriod();
            }
        }, getPeriod(), getPeriod(), getUnit());
    }






    synchronized void endOfPeriod() {
        lastCallsPerPeriod = acquireCount;
        totalAcquireCount += acquireCount;
        periodCount++;
        acquireCount = 0;
        notifyAll();
    }
}
