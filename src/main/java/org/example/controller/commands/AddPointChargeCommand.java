package org.example.controller.commands;

import org.example.model.SimulationModel;
import processing.core.PVector;

public class AddPointChargeCommand implements Command {
    private final SimulationModel simulation;
    private final PVector pos;

    public AddPointChargeCommand(SimulationModel simulation, PVector pos) {
        this.simulation = simulation;
        // copy so later undo can identify the same object
        this.pos = pos.copy();
    }

    @Override
    public void execute() {
        simulation.addPointCharge(pos);
    }

    @Override
    public void undo() {
        // remove the last-added charge
        int last = simulation.getPointCharges().size() - 1;
        simulation.getPointCharges().remove(last);
    }
}