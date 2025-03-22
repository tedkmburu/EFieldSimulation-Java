package org.example.model;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;

public class FieldLine {
    private ArrayList<PVector> points;

    public FieldLine(ArrayList<PVector> points) {
        this.points = points;
    }

    // Draw the field line as a curve
    public void display(PApplet app) {
        app.pushMatrix();
        app.noFill();
        app.stroke(255);
        app.strokeWeight(3);
        app.beginShape();
        // Use the first point with vertex(), then curveVertex() for smooth curves.
        if (!points.isEmpty()) {
            app.vertex(points.getFirst().x, points.getFirst().y);
            for (PVector p : points) {
                app.curveVertex(p.x, p.y);
            }
        }
        app.endShape();
        app.popMatrix();
    }
}
