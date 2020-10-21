/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.htlgrieskirchen.pos3.sudoku;

import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Fabian
 */
public class SelecterCallable implements Callable<Boolean> {
    
    private final List<Unit> units;

    public SelecterCallable(List<Unit> units) {
        this.units = units;
    }
    
    @Override
    public Boolean call() throws Exception {
        return units.stream()
                .map(unit -> {
                    return unit.tryToSelectValue();
                })
                .anyMatch(unitCorrect -> unitCorrect==true);
    }
    
}
