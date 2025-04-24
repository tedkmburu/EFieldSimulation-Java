package org.example.controller.commands;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;

/**
 * Toggles the field-lines display on/off.
 */
public class ToggleFieldLinesCommand implements Command {
    private final ControlPanel panel;
    private final SimulationModel simulation;
    private boolean previous;

    public ToggleFieldLinesCommand(SimulationModel simulation, ControlPanel panel) {
        this.simulation = simulation;
        this.panel = panel;
    }

    @Override
    public void execute() {
        previous = panel.showFieldLinesMode();
        panel.toggleFieldLines(!previous);
    }

    @Override
    public void undo() {
        panel.toggleFieldLines(previous);
    }
}
