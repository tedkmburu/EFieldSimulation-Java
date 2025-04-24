package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import processing.core.PApplet;

public class FieldVectorMode implements SimulationMode {
    private final SimulationModel simulation;
    private final ControlPanel controlPanel;

    public FieldVectorMode(SimulationModel simulation,
                           ControlPanel controlPanel) {
        this.simulation = simulation;
        this.controlPanel = controlPanel;
    }

    @Override
    public void update() {
        if (controlPanel.showFieldVectorsMode()) {
            simulation.createFieldVectors();
        }
    }

    @Override
    public void display(PApplet app) {
        if (controlPanel.showFieldVectorsMode()) {
            simulation.displayFieldVectors(app);
        }
    }
}
