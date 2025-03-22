package org.example;

import controlP5.*;
import processing.core.PApplet;

import static org.example.Constants.*;

public class ControlPanel {
    private PApplet parent;
    private ControlP5 cp5;
    private Simulation simulation;

    // Example booleans to reflect Toggle states
    private boolean showFieldLines = false;
    private boolean showFieldVectors = false;
    private boolean showEquipotentialLines = false;
    private boolean showVoltage = false;
    private boolean numericalValue = false;
    private boolean showGrid = true;
    private boolean snapToGrid = false;
    private boolean testChargeMode = false;


    public ControlPanel(PApplet parent, Simulation simulation) {
        this(parent, simulation, false);  // calls the 3-arg constructor
    }

    public ControlPanel(PApplet parent, Simulation simulation, boolean skipUI) {
        this.parent = parent;
        this.simulation = simulation;

        if (!skipUI) {
            // Only create ControlP5 if we're not skipping the UI
            cp5 = new ControlP5(parent);
            createGUI();
        }
    }

//    public ControlPanel(PApplet parent, Simulation simulation) {
//        this.parent = parent;
//        this.simulation = simulation;
//
//        cp5 = new ControlP5(parent);
//
//        createGUI();
//    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Creates the UI elements using ControlP5.
     * Positions are chosen so that everything appears on the right side.
     */
    private void createGUI() {
        int panelX = parent.width - SIDE_PANEL_WIDTH; // near the right edge
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
//                .activate(0) // make it checked by default if desired
                .setValue(true)
                .setColorLabel(textColor)
                .setLabel("Field Lines");
        startY += spacing;

        // Field Vectors
        cp5.addToggle("fieldVectorsToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
//                .addItem("Field Vectors", 1)
                .setColorLabel(textColor)
                .setValue(true)
                .setLabel("Field Vectors");
        startY += spacing;

        // Equipotential Lines
        cp5.addToggle("equipotentialToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
//                .addItem("Equipotential Lines", 1)
                .setColorLabel(textColor)
                .setLabel("Equipotential Lines");
        startY += spacing;

        // Voltage
        cp5.addToggle("voltageToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
//                .addItem("Voltage", 1)
                .setColorLabel(textColor)
                .setLabel("Voltage");
        startY += spacing;

        // Numerical Value
        panelX += ToggleSize;
        cp5.addToggle("numericalValueToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
//                .addItem("Numerical Value", 1)
                .setColorLabel(textColor)
                .setLabel("Numerical Value");
        startY += spacing;
        panelX -= ToggleSize;


        // Show Grid
        cp5.addToggle("showGridToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
//                .addItem("Show Grid", 1)
//                .activate(0) // checked by default
                .setColorLabel(textColor)
                .setValue(true)
                .setLabel("Show Grid");
        startY += spacing;

        // Snap to Grid
        panelX += ToggleSize;
        cp5.addToggle("snapToGridToggle")
                .setPosition(panelX, startY)
                .setSize(ToggleSize, ToggleSize)
//                .addItem("Snap to Grid", 1)
                .setColorLabel(textColor)
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
//                .addItem("Test Charge Mode", 1)
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

    /**
     * Called automatically by ControlP5 when a UI element changes or a button is clicked.
     */
    public void controlEvent(ControlEvent e) {
        String controllerName = e.getController().getName();

        // For each Toggle or button, handle the logic you want:
        switch (controllerName) {
            // Togglees
            case "fieldLinesToggle":
                showFieldLines = e.getController().getValue() == 1.0;
                break;
            case "fieldVectorsToggle":
                showFieldVectors = e.getController().getValue() == 1.0;

                break;
            case "equipotentialToggle":
                showEquipotentialLines = e.getController().getValue() == 1.0;;
                break;
            case "voltageToggle":
                showVoltage = e.getController().getValue() == 1.0;
                break;
            case "numericalValueToggle":
                numericalValue = e.getController().getValue() == 1.0;
                break;
            case "showGridToggle":
                showGrid = e.getController().getValue() == 1.0;
                break;
            case "snapToGridToggle":
                snapToGrid = e.getController().getValue() == 1.0;
                break;
            case "testChargeModeToggle":
                testChargeMode = e.getController().getValue() == 1.0;;
                break;

            // Buttons
            case "singleButton":
                PredefinedConfigs.setSingleConfiguration(simulation);
                break;
            case "dipoleButton":
                PredefinedConfigs.setDipoleConfiguration(simulation);
                break;
            case "rowButton":
                PredefinedConfigs.setRowConfiguration(simulation);
                break;
            case "dipoleRowButton":
                PredefinedConfigs.setDipoleRowConfiguration(simulation);
                break;
            case "randomButton":
                PredefinedConfigs.setRandomConfiguration(simulation);
                break;
            case "removeAllButton":
                simulation.removeAllPointCharges();
                break;
            case "createTestChargeMapButton":
                testChargeMode = true;
                PredefinedConfigs.createTestChargeMap(simulation);
                break;
            case "clearTestChargesButton":
                simulation.clearTestCharges();
                break;
        }
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
}
