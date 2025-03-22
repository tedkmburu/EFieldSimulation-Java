package org.example.model;

import processing.core.PApplet;
import processing.core.PVector;

public class FieldLineArrow {
    private PVector position;
    private float direction; // in radians

    public FieldLineArrow(PVector position, float direction) {
        this.position = position;
        this.direction = direction;
    }

    // Draw the arrow at its position with the given direction.
    public void display(PApplet app) {
        app.pushMatrix();
        app.translate(position.x, position.y);
        app.rotate(direction);
        app.stroke(255);
        app.fill(255);
        // Draw a triangle pointing left (adjust size as needed)
        app.triangle(0, 0, -10, -5, -10, 5);
        app.popMatrix();
    }
}
