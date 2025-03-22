package org.example.model;

import org.example.Constants;
import processing.core.PApplet;
import processing.core.PVector;
import org.example.Constants.*;

import static org.example.Constants.CHARGE_INCREMENT_DELTA;

public class PointCharge extends Charge {
    public boolean selected = false;
    public boolean dragging = false;

    public PointCharge(PVector position, float charge) {
        super(position, charge);
    }

    @Override
    public void display(PApplet app) {
        // Set color based on charge
        if (charge > 0) {
            app.fill(210, 41, 45);
        } else if (charge < 0) {
            app.fill(23, 97, 176);
        } else {
            app.fill(85, 85, 85, 190);
        }

        // Highlight if it has been selected
        if (selected) {
            app.strokeWeight(2);
            app.stroke(255);
        } else {
            app.strokeWeight(1);
            app.stroke(0, 0, 0, (float) 255 / 2);
        }

        // Draw the circle
        app.ellipse(position.x, position.y, Constants.CHARGE_DIAMETER, Constants.CHARGE_DIAMETER);

        // Display the numeric charge
        app.fill(255);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text((int) charge, position.x, position.y);
    }

    public boolean isDragging() {
        return dragging;
    }

    public void select() {
        this.selected = true;
    }

    public void increaseCharge() { this.charge += CHARGE_INCREMENT_DELTA; }

    public void decreaseCharge() {
        this.charge -= CHARGE_INCREMENT_DELTA;
    }
}
