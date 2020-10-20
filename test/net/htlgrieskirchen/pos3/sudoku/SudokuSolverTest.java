/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.htlgrieskirchen.pos3.sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rsickinger
 */
public class SudokuSolverTest {
    // in the test class of the teacher another sudoku is used
    private static final int[][] STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION = new int[][] {
            {1,7,5 ,0,0,0, 3,0,0},
            {0,0,0 ,0,7,1, 5,0,0},
            {0,0,2 ,4,0,0, 0,1,0},
            
            {0,0,0 ,0,0,9, 0,4,0},
            {0,9,0 ,6,1,8, 2,3,0},
            {6,1,0 ,7,0,0, 0,0,0},
            
            {4,3,0 ,8,0,7, 6,0,0},
            {0,0,8 ,1,4,0, 0,0,0},
            {0,0,0 ,0,6,0, 0,0,1}
            };
    
    private static final int[][] STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION_SOLVED = new int[][] {
            {1,7,5 ,9,8,2, 3,6,4},
            {9,4,6 ,3,7,1, 5,2,8},
            {3,8,2 ,4,5,6, 7,1,9},
            
            {8,2,7 ,5,3,9, 1,4,6},
            {5,9,4 ,6,1,8, 2,3,7},
            {6,1,3 ,7,2,4, 8,9,5},
            
            {4,3,1 ,8,9,7, 6,5,2},
            {2,6,8 ,1,4,5, 9,7,3},
            {7,5,9 ,2,6,3, 4,8,1}
            };
    
    private static final String TEST_FILENAME = "test_sudoku.csv";
    private File sudokuTestFile = new File(TEST_FILENAME);
    
    private void writeSudokuToFile(File file) throws FileNotFoundException, IOException {
        try(PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for(int i = 0; i < 9; i++) {
                StringBuffer line = new StringBuffer();
                for(int j = 0; j < 9; j++) {
                    line.append(STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION[i][j] + "");
                    if(j <= 7) line.append(";");
                }
                out.println(line);
            }
        }
    }
    
    private boolean matricesEqual(int[][] a, int[][] b) {
        if(a.length != b.length)
            return false;
        
        for(int i = 0; i < a.length; i++) {
            if(a[i].length != b[i].length)
                return false;
            
            for(int j = 0; j < a[i].length; j++)
                if(a[i][j] != b[i][j])
                    return false;
        }
        
        return true;
    }
    
    @Test
    public void testReadSudoku() throws IOException {
        writeSudokuToFile(sudokuTestFile);
        SudokuSolver testSolver = new SudokuSolver();
        
        int[][] actualValue = testSolver.readSudoku(sudokuTestFile);
        sudokuTestFile.delete();
        
        assertTrue(matricesEqual(STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION, actualValue));
    }

    @Test
    public void testCheckSudokuUnsolved() {
        SudokuSolver testSolver = new SudokuSolver();
        assertFalse(testSolver.checkSudoku(STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION));
    }
    
    @Test
    public void testCheckSudokuSolved() {
        SudokuSolver testSolver = new SudokuSolver();
        assertTrue(testSolver.checkSudoku(STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION_SOLVED));
    }

    @Test
    // this test needs to be positive to get a PLUS!
    public void testSolveSudoku() {
        SudokuSolver testSolver = new SudokuSolver();
        int[][] actualValue = testSolver.solveSudoku(STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION);
        
        assertTrue(matricesEqual(STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION_SOLVED, actualValue));
    }
    
    @Test
    // this test needs to be positive to get a PLUS!
    public void testSolveSudokuParallel() {
        SudokuSolver testSolver = new SudokuSolver();
        int[][] actualValue = testSolver.solveSudokuParallel(STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION);
        
        assertTrue(matricesEqual(STUDENT_SUDOKU_WITH_ONLY_ONE_SOLUTION_SOLVED, actualValue));
    }
    
}
