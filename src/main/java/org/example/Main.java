package org.example;

import org.example.model.PointCharge;
import processing.core.PApplet;
import controlP5.ControlEvent;

import java.util.ArrayList;

import static org.example.Constants.*;

public class Main extends PApplet {
    Simulation simulation; // Holds simulation logic
    InputHandler inputHandler;
    private ControlPanel controlPanel;
//    ControlP5 cp5;

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
        this.simulation = new Simulation(this, controlPanel);
        this.inputHandler = new InputHandler(this, simulation, controlPanel);

        controlPanel.setSimulation(simulation);
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
    public void mousePressed() {
        inputHandler.handleMousePressed();
    }

    @Override
    public void mouseMoved() {
        inputHandler.handleMouseMoved();
    }

    @Override
    public void keyPressed() {
        inputHandler.handleKeyPressed();
    }

    @Override
    public void mouseDragged() { inputHandler.handleMouseDragged(); }

    @Override
    public void mouseClicked() {
        if (mouseX < displayWidth - SIDE_PANEL_WIDTH){
            inputHandler.handleMouseClicked();
        }
    }

}
