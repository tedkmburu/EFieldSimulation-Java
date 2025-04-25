package org.example;

import org.example.model.config.ConfigManager;
import org.example.controller.InputController;
import org.example.model.PresetConfigurator;
import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import processing.core.PApplet;
import controlP5.ControlEvent;

public class Main extends PApplet {
    SimulationModel simulation; // Holds simulation logic
    InputController inputController;
    private ControlPanel controlPanel;

    public static void main(String[] args) {
        PApplet.main("org.example.Main");
    }

    @Override
    public void settings() {
        size(displayWidth, displayHeight);
    }

    @Override
    public void setup() {
        this.controlPanel = new ControlPanel(this, null);
        this.simulation = new SimulationModel(this, controlPanel);
        this.inputController = new InputController(this, simulation, controlPanel);

        controlPanel.setSimulation(simulation);
        controlPanel.addListener(simulation);

        // create the dipole
        PresetConfigurator.setDipoleConfiguration(simulation);
        // now deselect both charges
        simulation.resetChargeStates();
    }

    public void controlEvent(ControlEvent e) {
        // Forward to controlPanel so it can handle the event
        if (controlPanel != null) {
            controlPanel.controlEvent(e);
        }
    }

    @Override
    public void draw() {
        background(0);
        simulation.update();   // Update simulation state (positions, forces, etc.)
        simulation.display();  // Draw simulation elements (charges, field lines, etc.)
    }


    @Override
    public void mouseMoved() {
        inputController.handleMouseMoved();
    }

    @Override
    public void keyPressed() {
        inputController.handleKeyPressed();
    }

    @Override
    public void mouseDragged() { inputController.handleMouseDragged(); }

    @Override
    public void mouseClicked() {
        if (mouseX < displayWidth - ConfigManager.getInstance().getSidePanelWidth()){
            inputController.handleMouseClicked();
        }
    }

}
