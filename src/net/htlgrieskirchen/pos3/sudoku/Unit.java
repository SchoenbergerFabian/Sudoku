/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.htlgrieskirchen.pos3.sudoku;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Fabian
 */
public class Unit {
    private List<Cell> cells;

    public Unit(List<Cell> cells) {
        this.cells = cells;
    }
    
    public boolean isCorrect(){
        Set<Integer> checker = new HashSet<>();
        cells.stream()
                .forEach(cell -> {
                    if(cell.hasSelectedValue()){
                        checker.add(cell.getSelectedValue());
                    }
                });
        return checker.size()==9;
    }
    
    public void reducePossibleValues(){
        cells.stream()
                .forEach(cell_value -> {
                    if(cell_value.hasSelectedValue()){
                        cells.stream()
                                .forEach(cell -> cell.removePossibleValue(cell_value.getSelectedValue()));
                    }
                });
    }
    
    public boolean tryToSelectValue(){
        return cells.stream()
                .map(cell -> {
                        if(cell.hasSelectedValue()){
                            return false;
                        }
                        if(cell.hasSinglePossibleValue()){
                            return cell.selectValue(cell.getSinglePossibleValue());
                        }
                        return false;
                })
                .anyMatch(valueSelcted -> valueSelcted == true);
    }
    
}
