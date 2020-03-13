
















package org.apache.commons.lang3.time;







































public class StopWatch {

    private static final long NANO_2_MILLIS = 1000000L;

    
    private static final int STATE_UNSTARTED = 0;

    private static final int STATE_RUNNING = 1;

    private static final int STATE_STOPPED = 2;

    private static final int STATE_SUSPENDED = 3;

    
    private static final int STATE_UNSPLIT = 10;

    private static final int STATE_SPLIT = 11;




    private int runningState = STATE_UNSTARTED;




    private int splitState = STATE_UNSPLIT;




    private long startTime;






    private long startTimeMillis;




    private long stopTime;






    public StopWatch() {
        super();
    }













    public void start() {
        if (this.runningState == STATE_STOPPED) {
            throw new IllegalStateException("Stopwatch must be reset before being restarted. ");
        }
        if (this.runningState != STATE_UNSTARTED) {
            throw new IllegalStateException("Stopwatch already started. ");
        }
        this.startTime = System.nanoTime();
        this.startTimeMillis = System.currentTimeMillis();
        this.runningState = STATE_RUNNING;
    }













    public void stop() {
        if (this.runningState != STATE_RUNNING && this.runningState != STATE_SUSPENDED) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
        if (this.runningState == STATE_RUNNING) {
            this.stopTime = System.nanoTime();
        }
        this.runningState = STATE_STOPPED;
    }










    public void reset() {
        this.runningState = STATE_UNSTARTED;
        this.splitState = STATE_UNSPLIT;
    }














    public void split() {
        if (this.runningState != STATE_RUNNING) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
        this.stopTime = System.nanoTime();
        this.splitState = STATE_SPLIT;
    }














    public void unsplit() {
        if (this.splitState != STATE_SPLIT) {
            throw new IllegalStateException("Stopwatch has not been split. ");
        }
        this.splitState = STATE_UNSPLIT;
    }














    public void suspend() {
        if (this.runningState != STATE_RUNNING) {
            throw new IllegalStateException("Stopwatch must be running to suspend. ");
        }
        this.stopTime = System.nanoTime();
        this.runningState = STATE_SUSPENDED;
    }














    public void resume() {
        if (this.runningState != STATE_SUSPENDED) {
            throw new IllegalStateException("Stopwatch must be suspended to resume. ");
        }
        this.startTime += System.nanoTime() - this.stopTime;
        this.runningState = STATE_RUNNING;
    }













    public long getTime() {
        return getNanoTime() / NANO_2_MILLIS;
    }













    public long getNanoTime() {
        if (this.runningState == STATE_STOPPED || this.runningState == STATE_SUSPENDED) {
            return this.stopTime - this.startTime;
        } else if (this.runningState == STATE_UNSTARTED) {
            return 0;
        } else if (this.runningState == STATE_RUNNING) {
            return System.nanoTime() - this.startTime;
        }
        throw new RuntimeException("Illegal running state has occurred.");
    }
















    public long getSplitTime() {
        return getSplitNanoTime() / NANO_2_MILLIS;
    }















    public long getSplitNanoTime() {
        if (this.splitState != STATE_SPLIT) {
            throw new IllegalStateException("Stopwatch must be split to get the split time. ");
        }
        return this.stopTime - this.startTime;
    }









    public long getStartTime() {
        if (this.runningState == STATE_UNSTARTED) {
            throw new IllegalStateException("Stopwatch has not been started");
        }
        
        return this.startTimeMillis;
    }












    @Override
    public String toString() {
        return DurationFormatUtils.formatDurationHMS(getTime());
    }













    public String toSplitString() {
        return DurationFormatUtils.formatDurationHMS(getSplitTime());
    }

}
