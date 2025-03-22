package org.example.model;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;

public class EquiLine {
    private ArrayList<PVector> points;
    private int strokeColor;

    public EquiLine(PApplet app, ArrayList<PVector> points, float voltage) {
        this.points = points;
        float absVoltage = Math.abs(voltage);
        // Map voltage to an intensity (adjust the range as needed)
        float intensity = app.map(absVoltage, 0, 475, 0, 255);
        int red = 0;
        int blue = 0;
        int alpha = (int)(intensity * 10);
        if (voltage > 0) {
            red = (int) intensity;
        } else if (voltage < 0) {
            blue = (int) intensity;
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
