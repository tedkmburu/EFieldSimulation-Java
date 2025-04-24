package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import processing.core.PApplet;

public class VoltageMode implements SimulationMode {
    private final SimulationModel simulation;
    private final ControlPanel controlPanel;

    public VoltageMode(SimulationModel sim, ControlPanel controlPanel) {
        this.simulation = sim;
        this.controlPanel = controlPanel;
    }

    @Override
    public void update() {
        if (controlPanel.showVoltageMode() && simulation.isVoltageDirty()) {
            simulation.updateVoltageGradient();
        }
    }

    @Override
    public void display(PApplet app) {
        if (controlPanel.showVoltageMode()) {
            simulation.displayVoltage(app);
        }
    }
}
