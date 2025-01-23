/**
 * The project goal is to implement external sorting algorithm using I/O
 * if the memory is not large enough to handle the data at once
 */

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

/**
 * The external sort main class
 *
 * @author Guann-Luen Chen
 * @version 2024.11.04
 */
public class Externalsort {

    /**
     * @param args
     *     Command line parameters
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        
        String inputFile = args[0];
        ReplacementSelection rs = new ReplacementSelection(
            inputFile, 
            "runFile.bin");
        
        rs.sort();
        rs.merge();
        rs.print();
    }

}
