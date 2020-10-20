package net.htlgrieskirchen.pos3.sudoku;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/* Please enter here an answer to task four between the tags:
 * <answerTask4>
 *    Hier sollte die Antwort auf die Aufgabe 4 stehen.
 * </answerTask4>
 */
public class SudokuSolver implements ISodukoSolver {
    
    private int[][] wrappedSudok;
    private Cell[][] wrappedSudoku;
    
    private List<Unit> rows;
    private List<Unit> columns;
    private List<Unit> blocks;
    
    public SudokuSolver(File file) {
        wrappedSudok = readSudoku(file);
        wrappedSudoku = wrapSudoku(wrappedSudok);
        initializeUnits(wrappedSudoku);
    }

    public int[][] getRawSudoku() {
        return wrappedSudok;
    }

    @Override
    public final int[][] readSudoku(File file){
        List<int[]> numbersList = new ArrayList<>();
        
        try {
            Files.lines(file.toPath())
                    .forEach(string -> {
                        String[] numberStrings = string.split(";");
                        
                        int[] row = new int[9];
                        for(int i = 0; i<row.length; i++){
                            row[i] = Integer.parseInt(numberStrings[i]);
                        }
                        numbersList.add(row);
                    });
        } catch (IOException ex) {
            error("ERROR: couldn't read file!",true);
        }
        
        int[][] numbers = new int[9][];
        for (int i = 0; i < numbersList.size(); i++) {
            numbers[i]=numbersList.get(i);
        }
        
        return numbers;
    }

    @Override
    public boolean checkSudoku() {
        return rows.stream()
                .map(unit -> unit.isCorrect())
                .allMatch(unitCorrect -> unitCorrect==true)
                && columns.stream()
                .map(unit -> unit.isCorrect())
                .allMatch(unitCorrect -> unitCorrect==true)
                && blocks.stream()
                .map(unit -> unit.isCorrect())
                .allMatch(unitCorrect -> unitCorrect==true);
    }
    

    @Override
    public int[][] solveSudoku() {
        boolean change = false;
        do{
            change = rows.stream()
                    .map(unit -> {
                        unit.reducePossibleValues();
                        return unit.tryToSelectValue();
                    })
                    .anyMatch(valueSelected -> valueSelected == true)
                    || columns.stream()
                    .map(unit -> {
                        unit.reducePossibleValues();
                        return unit.tryToSelectValue();
                    })
                    .anyMatch(valueSelected -> valueSelected == true)
                    || blocks.stream()
                    .map(unit -> {
                        unit.reducePossibleValues();
                        return unit.tryToSelectValue();
                    })
                    .anyMatch(valueSelected -> valueSelected == true);
        }while(change);
        
        return unwrapSudoku(wrappedSudoku);
//        int[][] solution = new int[9][9];
//        
//        List<List<List<Integer>>> possibleNumbers_temp = new ArrayList<>();
//        
//        //initialise
//        for (int row = 0; row < 9; row++) {
//            possibleNumbers_temp.add(new ArrayList<>());
//            
//            for (int column = 0; column < 9; column++) {
//                possibleNumbers_temp.get(row).add(new ArrayList<>());
//            
//                if(rawSudoku[row][column]==0){
//                    for (int number = 1; number <= 9; number++) {
//                        possibleNumbers_temp.get(row).get(column).add(number);
//                    }
//                }else{
//                    solution[row][column] = rawSudoku[row][column];
//                }
//            }
//        }
//        
//        boolean repeat = true;
//        while(repeat){
//            repeat = false;
//            
//            //reduce
//            for (int row = 0; row < 9; row++) {
//                for (int column = 0; column < 9; column++) {
//
//                    if(!possibleNumbers_temp.get(row).get(column).isEmpty()){
//
//                        //check row
//                        for (int innerColumn = 0; innerColumn < 9; innerColumn++) {
//                            possibleNumbers_temp.get(row).get(column)
//                                    .remove(Integer.valueOf(solution[row][innerColumn]));
//                        }
//
//                        //check column
//                        for (int innerRow = 0; innerRow < 9; innerRow++) {
//                            possibleNumbers_temp.get(row).get(column)
//                                    .remove(Integer.valueOf(solution[innerRow][column]));
//                        }
//
//                        //check 3x3
//                        int temp1 = (int)Math.ceil(row/3)*3;
//                        for (int innerRow = temp1; innerRow < temp1+3; innerRow++) {
//                            int temp2 = (int)Math.ceil(column/3)*3;
//                            for(int innerColumn = temp2; innerColumn < temp2+3; innerColumn++)
//                            possibleNumbers_temp.get(row).get(column)
//                                    .remove(Integer.valueOf(solution[innerRow][innerColumn]));
//                        }
//
//                    }
//
//                }
//            }
//        
//            //fixate
//            for (int row = 0; row < 9; row++) {
//                for (int column = 0; column < 9; column++) {
//
//                    if(possibleNumbers_temp.get(row).get(column).size()==1){
//                        solution[row][column] = possibleNumbers_temp.get(row).get(column).get(0);
//                        possibleNumbers_temp.get(row).get(column).clear();
//                        repeat = true;
//                    }
//
//                }
//            }
//            
//        }
//        
//        return solution;
    }
    
    @Override
    public int[][] solveSudokuParallel() {
        // implement this method
        return new int[0][0]; // delete this line!
    }
    
    public long benchmark(){
        return 0; //TODO
    }

    @Override
    public String toString() {
        return super.toString(); //TODO
    }
    
    private Cell[][] wrapSudoku(int[][] rawSudoku){
        Cell[][] wrappedSudoku = new Cell[9][9];
        for(int row = 0; row < rawSudoku.length; row++){
            for(int column = 0; column < rawSudoku[row].length; column++){
                if(rawSudoku[row][column]!=0){
                    wrappedSudoku[row][column]=new Cell(rawSudoku[row][column]);
                }else{
                    Cell initCell = new Cell();
                    for(int counter = 1; counter <= 9; counter++){
                        initCell.getPossibleValues().add(counter);
                    }
                    wrappedSudoku[row][column]=initCell;
                }
            }
        }
        return wrappedSudoku;
    }
    
    private int[][] unwrapSudoku(Cell[][] wrappedSudoku){
        int[][] rawSudoku = new int[9][9];
        for(int row = 0; row < rawSudoku.length; row++){
            for(int column = 0; column < rawSudoku[row].length; column++){
                if(wrappedSudoku[row][column].hasSelectedValue()){
                    rawSudoku[row][column] = wrappedSudoku[row][column].getSelectedValue();
                }else{
                    rawSudoku[row][column] = 0;
                }
            }
        }
        return rawSudoku;
    }
    
    private void initializeUnits(Cell[][] wrappedSudoku){
        //rows and columns
        rows = new ArrayList<>();
        columns = new ArrayList<>();
        for(int row = 0; row < wrappedSudoku.length; row++){
            List<Cell> rowCells = new ArrayList<>();
            List<Cell> columnCells = new ArrayList<>();
            for(int column = 0; column < wrappedSudoku[row].length; column++){
                rowCells.add(wrappedSudoku[row][column]);
                columnCells.add(wrappedSudoku[column][row]);
            }
            rows.add(new Unit(rowCells));
            columns.add(new Unit(columnCells));
        }
        
        //blocks
        blocks = new ArrayList<>();
        List<Cell> blockCells = new ArrayList<>();
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 3; column++){
                
                for(int innerRow = 0; innerRow < 3; innerRow++){
                    for(int innerColumn = 0; innerColumn < 3; innerColumn++){
                        blockCells.add(wrappedSudoku[innerRow+(row*3)][innerColumn+(column*3)]);
                    }
                }
                blocks.add(new Unit(blockCells));
                blockCells = new ArrayList<>();
            }
        }
    }

    
    private void error(String message, boolean exit){
        System.out.println(message);
        if(exit){
            System.exit(0);
        }
    }
}
