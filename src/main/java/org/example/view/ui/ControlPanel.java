package org.example.view.ui;

import controlP5.*;
import org.example.model.SimulationModel;
import org.example.controller.commands.CreateTestChargeMapCommand;
import org.example.controller.commands.Invoker;
import org.example.controller.commands.ToggleFieldLinesCommand;
import org.example.model.config.ConfigManager;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;


public class ControlPanel {
    private PApplet parent;
    private ControlP5 cp5;
    private SimulationModel simulation;

    // Example booleans to reflect Toggle states
    private boolean showFieldLines = true;
    private boolean showFieldVectors = false;
    private boolean showEquipotentialLines = false;
    private boolean showVoltage = false;
    private boolean numericalValue = false;
    private boolean showGrid = true;
    private boolean snapToGrid = false;
    private boolean testChargeMode = false;

    private final List<ControlPanelListener> listeners = new ArrayList<>();
    private final Invoker invoker = new Invoker();

    public void addListener(ControlPanelListener l) {
        listeners.add(l);
    }
    public void removeListener(ControlPanelListener l) {
        listeners.remove(l);
    }

    public void controlEvent(ControlEvent e) {
        String name = e.getController().getName();
        boolean value = e.getController().getValue() == 1.0;
        switch (name) {
            case "fieldLinesToggle":
                invoker.execute(new ToggleFieldLinesCommand(simulation, this));
                break;
            case "fieldVectorsToggle":
                showFieldVectors = value;
                listeners.forEach(l -> l.onFieldVectorsToggled(value));
                break;
            case "equipotentialToggle":
                showEquipotentialLines = value;
                listeners.forEach(l -> l.onEquipotentialToggled(value));
                break;
            case "voltageToggle":
                showVoltage = value;
                listeners.forEach(l -> l.onVoltageToggled(value));
                break;
            case "showGridToggle":
                showGrid = value;
                listeners.forEach(l -> l.onGridToggled(value));
                break;
            case "snapToGridToggle":
                snapToGrid = value;
                listeners.forEach(l -> l.onSnapToGridToggled(value));
                break;
            case "testChargeModeToggle":
                testChargeMode = value;
                listeners.forEach(l -> l.onTestChargeModeToggled(value));
                break;

            case "singleButton":
                listeners.forEach(ControlPanelListener::onSinglePreset);
                break;
            case "dipoleButton":
                listeners.forEach(ControlPanelListener::onDipolePreset);
                break;
            case "rowButton":
                listeners.forEach(ControlPanelListener::onRowPreset);
                break;
            case "dipoleRowButton":
                listeners.forEach(ControlPanelListener::onDipoleRowPreset);
                break;
            case "randomButton":
                listeners.forEach(ControlPanelListener::onRandomPreset);
                break;
            case "removeAllButton":
                listeners.forEach(ControlPanelListener::onRemoveAllCharges);
                break;
            case "createTestChargeMapButton":
                this.testChargeMode = true;
                invoker.execute(new CreateTestChargeMapCommand(simulation, this));
                break;
            case "clearTestChargesButton":
                listeners.forEach(ControlPanelListener::onClearTestCharges);
                break;
        }
    }

    public void toggleFieldLines(boolean on) {
        this.showFieldLines = on;
        listeners.forEach(l -> l.onFieldLinesToggled(on));
    }

    public ControlPanel(PApplet parent, SimulationModel simulation) {
        this(parent, simulation, false);  // calls the 3-arg constructor
    }

    public ControlPanel(PApplet parent, SimulationModel simulation, boolean skipUI) {
        this.parent = parent;
        this.simulation = simulation;

        if (!skipUI) {
            // Only create ControlP5 if we're not skipping the UI
            cp5 = new ControlP5(parent);
            createGUI();
        }
    }

    public void setSimulation(SimulationModel simulation) {
        this.simulation = simulation;
    }

    private void createGUI() {
        int panelX = parent.width - ConfigManager.getInstance().getSidePanelWidth(); // near the right edge
        int startY = 30;
        int spacing = 40;
        int ToggleSize = 20;
        int buttonWidth = 150;
        int buttonHeight = 25;

        int textColor = parent.color(0);

        // --------------------------------------------------------
        // 1) Togglees for various features
        // --------------------------------------------------------
        // Field Lines
        cp5.addToggle("fieldLinesToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
                .setValue(showFieldLines)
                .setColorLabel(textColor)
                .setLabel("Field Lines");
        startY += spacing;

        // Field Vectors
        cp5.addToggle("fieldVectorsToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
                .setColorLabel(textColor)
                .setValue(showFieldVectors)
                .setLabel("Field Vectors");
        startY += spacing;

        // Equipotential Lines
        cp5.addToggle("equipotentialToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
                .setValue(showEquipotentialLines)
                .setColorLabel(textColor)
                .setLabel("Equipotential Lines");
        startY += spacing;

        // Voltage
        cp5.addToggle("voltageToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
                .setColorLabel(textColor)
                .setValue(showVoltage)
                .setLabel("Voltage");
        startY += spacing;

        // Numerical Value
        panelX += ToggleSize;
        cp5.addToggle("numericalValueToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
                .setValue(numericalValue)
                .setColorLabel(textColor)
                .setLabel("Numerical Value");

        startY += spacing;
        panelX -= ToggleSize;


        // Show Grid
        cp5.addToggle("showGridToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
                .setColorLabel(textColor)
                .setValue(showGrid)
                .setLabel("Show Grid");
        startY += spacing;

        // Snap to Grid
        panelX += ToggleSize;
        cp5.addToggle("snapToGridToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
                .setColorLabel(textColor)
                .setValue(snapToGrid)
                .setLabel("Snap to Grid");

        startY += spacing * 2;
        panelX -= ToggleSize;


        // --------------------------------------------------------
        // 2) Label for "Premade Configurations:"
        // --------------------------------------------------------
        cp5.addTextlabel("premadeConfigsLabel")
                .setText("Premade Configurations:")
                .setPosition(panelX, startY)
                .setColor(textColor)
                .setFont(parent.createFont("Arial", 14));
        startY += spacing;

        // Single
        cp5.addButton("singleButton")
                .setPosition(panelX, startY)
                .setSize(buttonWidth, buttonHeight)
                .setLabel("Single");
        startY += spacing;

        // Dipole
        cp5.addButton("dipoleButton")
                .setPosition(panelX, startY)
                .setSize(buttonWidth, buttonHeight)
                .setLabel("Dipole");
        startY += spacing;

        // Row
        cp5.addButton("rowButton")
                .setPosition(panelX, startY)
                .setSize(buttonWidth, buttonHeight)
                .setLabel("Row");
        startY += spacing;

        // Dipole Row
        cp5.addButton("dipoleRowButton")
                .setPosition(panelX, startY)
                .setSize(buttonWidth, buttonHeight)
                .setLabel("Dipole Row");
        startY += spacing;

        // Random
        cp5.addButton("randomButton")
                .setPosition(panelX, startY)
                .setSize(buttonWidth, buttonHeight)
                .setLabel("Random");
        startY += spacing;

        // Remove All Charges
        cp5.addButton("removeAllButton")
                .setPosition(panelX, startY)
                .setSize(buttonWidth, buttonHeight)
                .setLabel("Remove All Charges");
        startY += spacing;

        // --------------------------------------------------------
        // 3) Test Charge Mode
        // --------------------------------------------------------
        cp5.addToggle("testChargeModeToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
                .setColorLabel(textColor)
                .setLabel("Test Charge Mode");
        startY += spacing;

        // Create Test Charge Map
        cp5.addButton("createTestChargeMapButton")
                .setPosition(panelX, startY)
                .setSize(buttonWidth, buttonHeight)
                .setLabel("Create Test Charge Map");
        startY += spacing;

        // Clear Test Charges
        cp5.addButton("clearTestChargesButton")
                .setPosition(panelX, startY)
                .setSize(buttonWidth, buttonHeight)
                .setLabel("Clear Test Charges");
    }

    // Getters for Simulation code to see which flags are on/off
    public boolean showFieldLinesMode() { return showFieldLines; }
    public boolean showFieldVectorsMode() { return showFieldVectors; }
    public boolean showEquipotentialLinesMode() { return showEquipotentialLines; }
    public boolean showVoltageMode() { return showVoltage; }
    public boolean numericalValueMode() { return numericalValue; }
    public boolean showGridMode() { return showGrid; }
    public boolean snapToGridMode() { return snapToGrid; }
    public boolean testChargeMode() { return testChargeMode; }

    public void toggleTestChargeMode(boolean b) {
    }
}
