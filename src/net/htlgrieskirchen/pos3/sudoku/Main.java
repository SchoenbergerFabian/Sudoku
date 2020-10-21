package net.htlgrieskirchen.pos3.sudoku;


import java.io.File;

public class Main {
    public static void main(String[] args) {
        SudokuSolver ss = new SudokuSolver();
        int[][] input = ss.readSudoku(new File("2_sudoku_level1.csv"));
        
        
        System.out.println(">--- ORIGINAL ---");
        System.out.println(getSudokuString(input)+'\n');
        
        int[][] output = ss.solveSudoku(input);
        //int[][] output = ss.solveSudokuParallel(input);
        System.out.println("\n>--- SOLUTION ---");
        System.out.println(getSudokuString(output)+'\n');
        
        System.out.println(">----------------");
        System.out.println("SOLVED    = " + ss.checkSudokuParallel(output));
        System.out.println(">----------------");
        
        
        System.out.println("\n>--- BENCHMARK ---");
        System.out.println("SINGLE    = " + ss.benchmark(input) + "ms");
        System.out.println("PARALLEL  = " + ss.benchmarkParallel(input) + "ms");
        System.out.println(">----------------");
    }
    
    private static String getSudokuString(int[][] sudoku){
        String separator = "+---+---+---++---+---+---++---+---+---+";
        StringBuilder sudokuString = new StringBuilder(separator+"\n");
        
        for (int row = 0; row < sudoku.length; row++) {
            
            if(row+1 != sudoku.length){
                sudokuString.append(getSudokuRowString(sudoku[row])+'\n');

                if((row+1)%3 == 0){
                    sudokuString.append(separator.replaceAll("-", "=")+'\n');
                }else{
                    sudokuString.append(separator+'\n');
                }
                
            }else{
                sudokuString.append(getSudokuRowString(sudoku[row])+'\n'+separator);
            }
            
        }
        
        return sudokuString.toString();
    }
    
    private static String getSudokuRowString(int[] row){
        String separator = " |";
        StringBuilder rowString = new StringBuilder("| ");
        for (int column = 0; column < row.length; column++) {
            
            
            if(row[column]==0){
                rowString.append(" "+separator);
            }else{
                rowString.append(row[column]+separator);
            }
            
            if((column+1)%3 == 0 && column+1 != row.length){
                rowString.append("| ");
            }else{
                rowString.append(' ');
            }
            
        }
        return rowString.toString();
    }
}
