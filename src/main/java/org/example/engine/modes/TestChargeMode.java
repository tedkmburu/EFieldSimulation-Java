package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import processing.core.PApplet;

public class TestChargeMode implements SimulationMode {
    private final SimulationModel simulation;
    private final ControlPanel controlPanel;

    public TestChargeMode(SimulationModel sim, ControlPanel controlPanel) {
        this.simulation = sim;
        this.controlPanel  = controlPanel;
    }

    @Override
    public void update() {
        if (controlPanel.testChargeMode()) {
            simulation.moveTestCharges();
        }
    }

    @Override
    public void display(PApplet app) {
        if (controlPanel.testChargeMode()) {
            simulation.displayTestCharges(app);
        }
    }
}
