import student.TestCase;

/**
 * @author Guann-Luen Chen
 * @version 2024.11.04
 */
public class ExternalsortTest extends TestCase {
    
    
    /**
     * set up for tests
     */
    public void setUp() {
        //nothing to set up.
    }
    
    /**
     * Test
     * @throws Exception 
     */
    public void testExternalsort() throws Exception {
        String[] args = {"sampleInput16.bin"};
        Externalsort.main(args);
    }

}
