package net.htlgrieskirchen.pos3.sudoku;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/* time used for this project: ~ 20 hours
 * <answerTask1.6>
 *    It's not faster and I didn't expect that because I thought it would be 3x faster, because I split it into 3 pieces.
 * </answerTask1.6>
 */
public class SudokuSolver implements ISodukoSolver {
    
    private List<Unit> rows;
    private List<Unit> columns;
    private List<Unit> blocks;
    
    public SudokuSolver() {
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
    public boolean checkSudoku(int[][] rawSudoku) {
        initializeUnits(wrapSudoku(rawSudoku));
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
    
    public boolean checkSudokuParallel(int[][]rawSudoku) {
        Cell[][] wrappedSudoku = wrapSudoku(rawSudoku);
        initializeUnits(wrappedSudoku);
        
        ExecutorService executor =
                Executors.newCachedThreadPool();

        List<CheckerCallable> tasks = new ArrayList<>();
        
        tasks.add(new CheckerCallable(rows));
        tasks.add(new CheckerCallable(columns));
        tasks.add(new CheckerCallable(blocks));
        
        boolean correct = false;
        try {
            List<Future<Boolean>> corrects = executor.invokeAll(tasks);
            executor.shutdown();
            
            correct = corrects.stream()
                    .map(future -> {
                        try{
                            return future.get();
                        } catch(InterruptedException ex){
                            ex.printStackTrace();
                        } catch (ExecutionException ex) {
                            ex.printStackTrace();
                        }
                        return false;
                    })
                    .allMatch(isCorrect -> isCorrect==true);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        return correct;
    }

    @Override
    public int[][] solveSudoku(int[][] rawSudoku) {
        Cell[][] wrappedSudoku = wrapSudoku(rawSudoku);
        initializeUnits(wrappedSudoku);
        
        boolean changeRows;
        boolean changeColumns;
        boolean changeBlocks;
        do{
            
            changeRows = rows.stream()
                    .map(unit -> {
                        unit.reducePossibleValues();
                        return unit.tryToSelectValue();
                    })
                    .anyMatch(valueSelected -> valueSelected == true);
            changeColumns = columns.stream()
                    .map(unit -> {
                        unit.reducePossibleValues();
                        return unit.tryToSelectValue();
                    })
                    .anyMatch(valueSelected -> valueSelected == true);
            changeBlocks = blocks.stream()
                    .map(unit -> {
                        unit.reducePossibleValues();
                        return unit.tryToSelectValue();
                    })
                    .anyMatch(valueSelected -> valueSelected == true);
            
        }while(changeRows||changeColumns||changeBlocks);
        
        return unwrapSudoku(wrappedSudoku);

    }
    
    @Override
    public int[][] solveSudokuParallel(int[][] rawSudoku) {
        Cell[][] wrappedSudoku = wrapSudoku(rawSudoku);
        initializeUnits(wrappedSudoku);
        
        ExecutorService executor =
                Executors.newCachedThreadPool();

        List<ReducerCallable> tasksReduce = new ArrayList<>();
        List<SelecterCallable> tasksSelect = new ArrayList<>();
        
        tasksReduce.add(new ReducerCallable(rows));
        tasksReduce.add(new ReducerCallable(columns));
        tasksReduce.add(new ReducerCallable(blocks));
        
        tasksSelect.add(new SelecterCallable(rows));
        tasksSelect.add(new SelecterCallable(columns));
        tasksSelect.add(new SelecterCallable(blocks));
        
        boolean change;
        try {
            do{
                //Reduce all
                List<Future<Boolean>> finished = executor.invokeAll(tasksReduce);
                finished.forEach(future -> {
                            try{
                                future.get();
                            } catch(InterruptedException ex){
                                ex.printStackTrace();
                            } catch (ExecutionException ex) {
                                ex.printStackTrace();
                            }
                        });
                
                //Select all
                List<Future<Boolean>> changes = executor.invokeAll(tasksSelect);
                change = changes.stream()
                        .map(future -> {
                            try{
                                return future.get();
                            } catch(InterruptedException ex){
                                ex.printStackTrace();
                            } catch (ExecutionException ex) {
                                ex.printStackTrace();
                            }
                            return false;
                        })
                        .anyMatch(changed -> changed==true);
            }while(change);
            executor.shutdown();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return unwrapSudoku(wrappedSudoku);
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
    
    public long benchmark(int[][] rawSudoku){
        long sum = 0;
        for(int i = 0; i < 10; i++){
            long start = System.currentTimeMillis();
                
                int[][] output = solveSudoku(rawSudoku);
                checkSudoku(output);
            
            long end = System.currentTimeMillis();
            sum += end-start;
        }
        return sum/10;
    }
    
    public long benchmarkParallel(int[][] rawSudoku){
        long sum = 0;
        for(int i = 0; i < 10; i++){
            long start = System.currentTimeMillis();
                
                int[][] output = solveSudokuParallel(rawSudoku);
                checkSudokuParallel(output);
            
            long end = System.currentTimeMillis();
            sum += end-start;
        }
        return sum/10;
    }
}
