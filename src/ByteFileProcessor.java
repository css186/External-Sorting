import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * The class is design to process the byte file by 
 * creating a file processor object and assign a read/write mode
 *
 * @author Guann-Luen Chen
 * @version 2024.11.04
 */
public class ByteFileProcessor {
    // ~ Fields.....................................................
    //
    // ----------------------------------------------------------
    private RandomAccessFile file;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private FileChannel channel;

    // ~ Constructor.................................................
    //
    // ----------------------------------------------------------
    /**
     * ByteFileProcessor constructor
     * @param filename
     *        filename in string
     * @param mode
     *        read, write or read and write mode in string
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public ByteFileProcessor(
            String filename,
            String mode) throws Exception {

        // if write mode
        if (mode.equals("r")) {
            this.file = new RandomAccessFile(filename, "r");
        } 
        // if read&write mode
        else if (mode.equals("rw")) {
            this.file = new RandomAccessFile(filename, "rw");
        }
        
        this.readBuffer = ByteBuffer.allocate(
                ByteFile.BYTES_PER_BLOCK); // 8192
        
        // set buffer start at the beginning
        this.readBuffer.position(0);
        // Set limit to zero to make buffer empty
        this.readBuffer.limit(0);

        this.writeBuffer = ByteBuffer.allocate(
                ByteFile.BYTES_PER_BLOCK); // 8192
        this.channel = file.getChannel();
    }
    
    // ~ Public Method ....................................................
    //
    // ----------------------------------------------------------
    /**
     * Method to read record from random access file
     * @return
     *         Record read
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public Record readRecord() throws Exception {
        if (readBuffer.remaining() < Record.BYTES) {
            readBuffer.clear();
            int bytesRead = channel.read(readBuffer);
            if (bytesRead == -1) {
                return null;
            }
            readBuffer.flip();
        }
        
        // If there is not enough data for a full record
        // then return null
        if (readBuffer.remaining() < Record.BYTES) {
            return null;
        }
        
        long id = readBuffer.getLong();
        double key = readBuffer.getDouble();
        return new Record(id, key);
    }

    // ----------------------------------------------------------
    /**
     * Method to write record into buffer
     * @param record
     *        Record object
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public void writeRecord(Record record) throws Exception {
        if (writeBuffer.remaining() < Record.BYTES) {
            flushWriteBuffer();
        }

        writeBuffer.putLong(record.getID());
        writeBuffer.putDouble(record.getKey());

    }

    // ----------------------------------------------------------
    /**
     * Method to flush and clear the write buffer
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public void flushWriteBuffer() throws Exception {
        writeBuffer.flip();
        
        while (writeBuffer.hasRemaining()) {
            channel.write(writeBuffer);
        }
        
        writeBuffer.clear();
    }

    // ----------------------------------------------------------
    /**
     * Method to close any resource opened
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public void closeFile() throws Exception {
        flushWriteBuffer();
        channel.close();
        file.close();
    }

    // ----------------------------------------------------------
    /**
     * Method to get current position of the file
     * @return
     *         current position in file + position in write buffer
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public long getFilePosition() throws Exception {
        return file.getFilePointer() + writeBuffer.position();
    }
    
    // ----------------------------------------------------------
    /**
     * Method to set file position
     * @param position
     *        position to set
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public void setFilePosition(long position) throws Exception {
        file.seek(position);
        
        // clear the read buffer 
        readBuffer.clear();
        readBuffer.limit(0);
        // clear the write buffer
        writeBuffer.clear();
        writeBuffer.flip();
    }

    // ----------------------------------------------------------
    /**
     * Get the end of the file
     * @return
     *         file length (end position)
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public long getEndPos() throws Exception {
        return file.length();
    }

    // ----------------------------------------------------------
    /**
     * Seek position in file
     * @param position
     *        file position
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public void seek(long position) throws Exception {
        file.seek(position);
    }

    // ----------------------------------------------------------
    /**
     * Method to check if file still contains data
     * @return
     *         true if there is still data in file
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public boolean hasData() throws Exception {
        return (channel.position() < channel.size()) ||
                (readBuffer.hasRemaining());
    }

    // ----------------------------------------------------------
    /**
     * Method to print out data in file
     * @throws Exception
     *         Exception (mostly IOException)
     */
    public void print() throws Exception {
        byte[] block = new byte[ByteFile.BYTES_PER_BLOCK];
        int count = 0;

        while (file.read(block) != -1) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(block);

            long id = byteBuffer.getLong();
            double key = byteBuffer.getDouble();

            System.out.print(id + " " + key);
            count++;

            if (count % 5 == 0) {
                System.out.println();
            } 
            else {
                System.out.print(" ");
            }

        }
        if (count % 5 != 0) {
            System.out.println();
        }
        file.close();
    }
    
    // ----------------------------------------------------------
    /**
     * Method to get file object
     * @return
     *         file object
     */
    public RandomAccessFile getFile() {
        return this.file;
    }
    
    // ----------------------------------------------------------
    /**
     * Method to get read buffer
     * @return
     *         read buffer
     */
    public ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

}
