package jp.posl.jprophet.spotbugs;

import java.time.temporal.ValueRange;

public class BugInstance {

    private final String type;
    private final String filePath;
    private final int positionStart;
    private final int positionEnd;
    
    public BugInstance(final String _type, final String _filePath, final int _start, final int _end){
        this.type = _type;
        this.filePath = _filePath;
        this.positionStart = _start;
        this.positionEnd = _end;
    }


    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }


    /**
     * @return the filePath
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * @return the positionStart
     */
    public int getPositionStart() {
        return this.positionStart;
    }

    /**
     * @return the positionEnd
     */
    public int getPositionEnd() {
        return this.positionEnd;
    }

}
