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
public class ReducerCallable implements Callable<Boolean> {
    
    private final List<Unit> units;

    public ReducerCallable(List<Unit> units) {
        this.units = units;
    }
    
    @Override
    public Boolean call() throws Exception {
        units.stream()
                .forEach(unit -> {
                    unit.reducePossibleValues();
                });
        return true;
    }
    
}
