import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * The class containing replacement selection algorithm
 *
 * @author Guann-Luen Chen
 * @version 2024.11.04
 */
public class ReplacementSelection {

    // ~ Fields ..........................................................
    //
    // ----------------------------------------------------------
    private static final int MAX_BLOCKS = 8;
    private static final int MAX_RECORDS = 
        MAX_BLOCKS * ByteFile.RECORDS_PER_BLOCK;

    private MinHeap<Record> heap;
    private ByteFileProcessor inputProcessor;
    private ByteFileProcessor runProcessor;

    private LinkedList<Record> unsortedList;
    private LinkedList<RunRecord> runRecordList;
    private Record[] heapArray;
    
    private String inputFileName;

    // ~ Constructor ......................................................
    //
    // ----------------------------------------------------------
    /**
     * Initialize file processors
     * @param inputFile
     *        input file name in string
     * @param runFile
     *        run file name in string
     * @throws Exception
     *         Exception (mostly IOExeption)
     */
    public ReplacementSelection(
        String inputFile, 
        String runFile) throws Exception {
        this.inputProcessor = new ByteFileProcessor(inputFile, "rw");
        this.runProcessor = new ByteFileProcessor(runFile, "rw");
        
        this.unsortedList = new LinkedList<>();
        this.runRecordList = new LinkedList<>();
        this.inputFileName = inputFile;
    }
    // ~ Private Method ....................................................
    //
    // ----------------------------------------------------------
    /**
     * Method to build up the heap
     * @throws Exception
     *         Exception (mostly IOExeption)
     */
    private void buildHeap() throws Exception {
        heapArray = new Record[MAX_RECORDS];
        int numRead = 0;

        for (int i = 0; i < MAX_RECORDS 
            && inputProcessor.hasData(); i++) {
            Record record = inputProcessor.readRecord();
            if (record == null) {
                break;
            }
            heapArray[i] = record;
            numRead++;
        }
        heap = new MinHeap<>(heapArray, numRead, MAX_RECORDS);
    }    
    
    // ---------------------------------------------------------- 
    /**
     * Method to build up the heap using record in the linked list
     * @throws Exception
     *         Exception (mostly IOExeption)
     */
    private void buildHeapFromList() throws Exception {
        int maxRecords = MAX_BLOCKS * ByteFile.RECORDS_PER_BLOCK;
        heapArray = new Record[maxRecords]; // Reset heapArray
        int count = 0;

        ListNode<Record> curr = unsortedList.getHead();

        while (curr != null && count < maxRecords) {
            Record record = curr.getData();
            heapArray[count] = record;
            count++;
            curr = curr.getNext();
        }
        unsortedList.clear();

        this.heap = new MinHeap<>(heapArray, count, maxRecords);
    }

    // ----------------------------------------------------------
    /**
     * Replacement selection sort helper
     * @throws Exception
     *         Exception (mostly IOExeption)
     */
    private void sortHelper() throws Exception {
        runProcessor.flushWriteBuffer();
        int recordCount = 0;
        
        // store the starting position of the run
        long runStartPos = runProcessor.getFilePosition();

        while ((heap.heapSize() > 0)) {
            // 1. move the root to output
            Record minRecord = heap.removeMin();
            runProcessor.writeRecord(minRecord);
            recordCount++;

            // read the next input record if available
            Record inRec = null;
            if (inputProcessor.hasData()) {
                inRec = inputProcessor.readRecord();
            }

            if (inRec != null) {
                // compare with the last output record
                if (inRec.compareTo(minRecord) < 0) {
                    // if smaller, defer to next run
                    unsortedList.insertTail(inRec);
                } 
                else {
                    // else, continue with current run
                    heap.insert(inRec);
                }
            }
        }
        
        // After all records from input are processed
        // drain the remaining heap
        while (heap.heapSize() > 0) {
            Record remainingRecord = heap.removeMin();
            runProcessor.writeRecord(remainingRecord);
            recordCount++;
        }

        // record each run into the RunRecord object
        RunRecord runRecord = new RunRecord(
                recordCount,
                runStartPos);

        runRecordList.insertTail(runRecord);

    }
    
    // ~ Public Method ....................................................
    //
    // ----------------------------------------------------------
    /**
     * Control of the sort helper method
     * @throws Exception
     *         Exception (mostly IOExeption)
     */
    public void sort() throws Exception {
        // continue the process until there are no more input
        // or there are no more deferred records
        while (inputProcessor.hasData() || unsortedList.getSize() > 0) {
            // build initial heap if no deferred record
            // but there is input data
            if (unsortedList.getSize() == 0 && inputProcessor.hasData()) {
                buildHeap();
            }
            // build the heap from unsorted record for the next run
            else {
                buildHeapFromList();
            }
            
            // only proceed sorting if there is data in heap
            if (heap.heapSize() > 0) {
                sortHelper();
                runProcessor.flushWriteBuffer();
            }
        }
    }

    // ----------------------------------------------------------
    /**
     * Multi-way merge implementation
     * @throws Exception
     *         Exception (mostly IOExeption)
     */
    public void merge() throws Exception {
        
        int numRuns = runRecordList.getSize();
        // if there are no more runs, then stop merging
        if (numRuns == 0) {
            return;
        }
        
        // set input file position to the beginning
        inputProcessor.setFilePosition(0);
        
        
        // initialize heap for merger
        MinHeap<RunRecord> mergeHeap = new MinHeap<>(
            new RunRecord[numRuns], 
            0, 
            numRuns);
        
        // load first record in each run
        ListNode<RunRecord> currRunNode = runRecordList.getHead();
        
        while (currRunNode != null) {
            RunRecord runRecord = currRunNode.getData();

            if (runRecord.loadNextRecord(runProcessor)) {
                mergeHeap.insert(runRecord);
            }

            currRunNode = currRunNode.getNext();
        }
        
        // merge runs
        while (mergeHeap.heapSize() > 0) {
            RunRecord minRunRecord = mergeHeap.removeMin();
            Record minRecord = minRunRecord.getCurrRecord();
            inputProcessor.writeRecord(minRecord); // Write back to input file

            // Load next record from the same run 
            // and re-insert into heap if not exhausted
            if (minRunRecord.loadNextRecord(runProcessor)) {
                mergeHeap.insert(minRunRecord);
            }
        }
        inputProcessor.flushWriteBuffer();
        long newLength = inputProcessor.getFilePosition();
        inputProcessor.getFile().setLength(newLength);

        // Close runProcessor if done
        runProcessor.closeFile();
        
    }

    // ----------------------------------------------------------
    /**
     * Method to write the data into input file
     * @throws Exception
     */
    public void writeDataToFile() throws Exception {
        // close input file
        inputProcessor.closeFile();
        
        // reopen
        inputProcessor = new ByteFileProcessor(inputFileName, "rw");
        
        // set position to the beginning for both processor
        inputProcessor.setFilePosition(0);
        runProcessor.setFilePosition(0);
        
        // Clear the read buffer of runProcessor
        runProcessor.getReadBuffer().clear();
        runProcessor.getReadBuffer().limit(0);
        
        Record record;
        while ((record = runProcessor.readRecord()) != null) {
            inputProcessor.writeRecord(record);
        }

        // Flush the write buffer to ensure all data is written
        inputProcessor.flushWriteBuffer();
        
        // Truncate the input file to the new length if necessary
        long newLength = inputProcessor.getFilePosition();
        inputProcessor.getFile().setLength(newLength);

    }
    
    
    
    // ----------------------------------------------------------
    /**
     * method to print the record on console
     * @throws Exception
     *         Exception (mostly IOExeption)
     */
    public void print() throws Exception {
        // insure starts from the beginning
        inputProcessor.setFilePosition(0);        
        FileChannel channel = inputProcessor.getFile().getChannel();

        channel.position(0); // reset file position to the beginning

        ByteBuffer buffer = ByteBuffer.allocate(Record.BYTES);
        int count = 0;

        // calculate the total number of blocks
        long fileSize = channel.size();
        int blockCount = (int) (fileSize / ByteFile.BYTES_PER_BLOCK);

        // iterate over each block, reading only the first record of each block
        for (int i = 0; i < blockCount; i++) {
            // move the channel position to the start of the block
            channel.position(i * ByteFile.BYTES_PER_BLOCK);

            // read one record (16 bytes) from the current block
            buffer.clear();
            int bytesRead = channel.read(buffer);

            // if read in full record then break loop
            if (bytesRead < Record.BYTES) {
                break;
            }

            // prepare buffer to read the record data
            buffer.flip();

            long id = buffer.getLong();
            double key = buffer.getDouble();

            System.out.print(id + " " + key);
            count++;

            // formatting: 5 records per line
            if (count % 5 == 0) {
                System.out.println();
            } 
            else {
                System.out.print(" ");
            }
        }

        // print a newline if the last line is incomplete
        if (count % 5 != 0) {
            System.out.println();
        }
    }

}
