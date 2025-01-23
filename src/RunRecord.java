/**
 * The class to record run file's information
 * 
 * @author Guann-Luen Chen
 * @version 2024.11.04
 */
public class RunRecord implements Comparable<RunRecord> {
    // ~ Fields ....................................................
    //
    // ----------------------------------------------------------
    private int runLength;
    private long runPos;
    private long currPos;
    private Record currRecord;

    
    // ~ Constructors ..............................................
    //
    // ----------------------------------------------------------
    /**
     * Initiate RunRecord object to store information of each run
     * @param length
     *        the length of the run
     * @param position
     *        the position of the record in the run
     */
    public RunRecord(int length, long position) {
        runLength = length;
        runPos = position;
        currPos = position;
    }
    
    // ~ Public Method ....................................................
    //
    // ----------------------------------------------------------
    /**
     * Get run file's position
     * @return
     *         run file position
     */
    public long getRunPos() {
        return this.runPos;
    }
    
    // ----------------------------------------------------------    
    /**
     * get length of the run
     * @return
     *         length of the run
     */
    public long getRunLength() {
        return this.runLength;
    }
    
    // ----------------------------------------------------------
    /**
     * get current record in the run
     * @return
     *         current record
     */
    public Record getCurrRecord() {
        return this.currRecord;
    }
    
    // ----------------------------------------------------------
    /**
     * load the next record
     * @param inputFile
     *        input file processor
     * @return
     *        true if the next record is loaded
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public boolean loadNextRecord(ByteFileProcessor inputFile) 
        throws Exception {
        if (runLength > 0) {
            inputFile.setFilePosition(currPos);
            currRecord = inputFile.readRecord();
            if (currRecord != null) {
                runLength--;
                currPos += Record.BYTES;
                return true;
            }
        }
        currRecord = null;
        return false;
    }

    // ----------------------------------------------------------
    /**
     * Override method of compreTo
     */
    @Override
    public int compareTo(RunRecord o) {
        if (this.currRecord == null && o.currRecord == null) {
            return 0;
        }
        if (this.currRecord == null) {
            return 1;
        }
        if (o.currRecord == null) {
            return -1;
        }
        return this.currRecord.compareTo(o.currRecord);
    }

}
