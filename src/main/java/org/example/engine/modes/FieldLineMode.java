package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import org.example.factories.FieldElementFactory;
import processing.core.PApplet;

public class FieldLineMode implements SimulationMode {
    private final SimulationModel simulation;
    private final ControlPanel controlPanel;

    public FieldLineMode(SimulationModel simulation,
                         ControlPanel controlPanel,
                         FieldElementFactory fieldElementFactory) {
        this.simulation = simulation;
        this.controlPanel = controlPanel;
    }

    @Override
    public void update() {
        if (controlPanel.showFieldLinesMode()) {
            simulation.createFieldLines();
        }
    }

    @Override
    public void display(PApplet app) {
        if (controlPanel.showFieldLinesMode()) {
            simulation.displayFieldLines(app);
        }
    }
}
