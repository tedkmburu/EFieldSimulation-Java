package org.example.controller.commands;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;


public class CreateTestChargeMapCommand implements Command {
    private final SimulationModel sim;
    private Boolean prevMode;
    private final ControlPanel controlPanel;

    public CreateTestChargeMapCommand(SimulationModel simulation, ControlPanel controlPanel) {
        this.sim = simulation;
        this.controlPanel = controlPanel;
        this.prevMode = controlPanel.testChargeMode();
        controlPanel.setTestChargeMode(true);
    }

    @Override
    public void execute() {
        // remember old mode
        prevMode = sim.controlPanel.testChargeMode();
        // turn on test-charge mode
        sim.onTestChargeModeToggled(true);
        // generate the map
        sim.onCreateTestChargeMap();
    }

    @Override
    public void undo() {
        // clear whatever was drawn
        sim.clearTestCharges();
        // restore old mode
        sim.onTestChargeModeToggled(prevMode);
    }
}
