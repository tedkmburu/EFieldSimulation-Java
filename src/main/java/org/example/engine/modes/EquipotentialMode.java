package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import processing.core.PApplet;

public class EquipotentialMode implements SimulationMode {
    private final SimulationModel simulation;
    private final ControlPanel controlPanel;

    public EquipotentialMode(SimulationModel simulation, ControlPanel controlPanel) {
        this.simulation = simulation;
        this.controlPanel = controlPanel;
    }

    @Override
    public void update() {
        // nothing to do on every update;
        // equipotentials are generated on click instead
    }

    @Override
    public void display(PApplet app) {
        if (controlPanel.showEquipotentialLinesMode()) {
            simulation.displayEquipotentialLines(app);
        }
    }
}
