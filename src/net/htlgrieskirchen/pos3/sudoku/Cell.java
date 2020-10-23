/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.htlgrieskirchen.pos3.sudoku;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fabian
 */
public class Cell {
    private int selectedValue;
    private List<Integer> possibleValues;

    public Cell() {
        this(0);
    }

    public Cell(int selectedValue) {
        this.selectedValue = selectedValue;
        this.possibleValues = new ArrayList<>();
    }
    
    public boolean hasSelectedValue(){
        return selectedValue!=0;
    }
    
    public int getSelectedValue(){
        return selectedValue;
    }
    
    public List<Integer> getPossibleValues(){
        return this.possibleValues;
    }
    
    public boolean hasSinglePossibleValue(){
        return possibleValues.size()==1;
    }
    
    public int getSinglePossibleValue(){
        return possibleValues.get(0);
    }
    
    public synchronized void removePossibleValue(int value){
        possibleValues.remove(new Integer(value));
    }
    
    public boolean selectValue(int value){
        this.selectedValue = value;
        return true;
    }
    
}
