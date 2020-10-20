/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.htlgrieskirchen.pos3.sudoku;

import java.io.File;

public interface ISodukoSolver {
    int[][] readSudoku(File file);
    
    boolean checkSudoku(int[][] rawSudoku);
    
    int[][] solveSudoku(int[][] rawSudoku);   
    
    int[][] solveSudokuParallel(int[][] rawSudoku);  
}
