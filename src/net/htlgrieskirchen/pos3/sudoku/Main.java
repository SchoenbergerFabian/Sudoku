package net.htlgrieskirchen.pos3.sudoku;


import java.io.File;

public class Main {
    public static void main(String[] args) {
        SudokuSolver ss = new SudokuSolver(new File("1_sudoku_level1.csv"));
        
        System.out.println(">--- ORIGINAL ---");
        System.out.println(getSudoku(ss.getRawSudoku())+'\n');
        
        int[][] output = ss.solveSudoku();
        System.out.println("\n>--- SOLUTION ---");
        System.out.println(getSudoku(output)+'\n');
        
        System.out.println(">----------------");
        System.out.println("SOLVED    = " + ss.checkSudoku());
        System.out.println(">----------------");
    }
    
    private static String getSudoku(int[][] sudoku){
        String separator = "+---+---+---++---+---+---++---+---+---+";
        StringBuilder sudokuString = new StringBuilder(separator+"\n");
        
        for (int row = 0; row < sudoku.length; row++) {
            
            if(row+1 != sudoku.length){
                sudokuString.append(getSudokuRow(sudoku[row])+'\n');

                if((row+1)%3 == 0){
                    sudokuString.append(separator.replaceAll("-", "=")+'\n');
                }else{
                    sudokuString.append(separator+'\n');
                }
                
            }else{
                sudokuString.append(getSudokuRow(sudoku[row])+'\n'+separator);
            }
            
        }
        
        return sudokuString.toString();
    }
    
    private static String getSudokuRow(int[] row){
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
