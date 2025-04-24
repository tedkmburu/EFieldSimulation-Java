package org.example.engine.modes;

import processing.core.PApplet;

public interface SimulationMode {
    /** advance simulation state (e.g. generate field‐vectors) */
    void update();
    /** render whatever this mode is responsible for */
    void display(PApplet app);
}