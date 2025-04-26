package org.example.model;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;

public class EquiLine {
    private ArrayList<PVector> points;
    private Integer strokeColor;

    public EquiLine(PApplet app, ArrayList<PVector> points, float voltage) {
        this.points = points;
        Float absVoltage = Math.abs(voltage);
        // Map voltage to an intensity (adjust the range as needed)
        Float intensity = app.map(absVoltage, 0, 475, 0, 255);
        Float red = 0.0f;
        Float blue = 0.0f;
        Float alpha = (intensity * 10.0f);
        if (voltage > 0) {
            red = intensity;
        } else if (voltage < 0) {
            blue = intensity;
        }
        if (voltage == 0) {
            strokeColor = app.color(255, 255, 255, 255);
        } else {
            strokeColor = app.color(red, 0, blue, alpha);
        }
    }

    // Draw the equipotential line
    public void display(PApplet app) {
        app.pushMatrix();
        app.noFill();
        app.stroke(strokeColor);
        app.strokeWeight(2);
        app.beginShape();
        for (PVector p : points) {
            app.curveVertex(p.x, p.y);
        }
        app.endShape();
        app.popMatrix();
    }
}
