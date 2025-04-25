package org.example.engine.modes;

import processing.core.PApplet;

public interface SimulationMode {

    void update();

    void display(PApplet app);
}