package org.example.model;

import org.example.CommonMath;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static org.example.CommonMath.*;
import static org.example.Constants.*;


public class TestCharge extends Charge {

    private PVector velocity;
    private PVector acceleration;
    private float radius;
    private int displayColor; // Processing color

    public TestCharge(PVector position, float charge) {
        super(position, charge);
        this.velocity = createVector();
        this.acceleration = createVector();
        this.radius = TEST_CHARGE_RADIUS;

        // Determine color based on charge polarity (using colors defined in Constants)
        if (charge > 0) {
            this.displayColor = POSITIVE_CHARGE_COLOR;
        } else if (charge < 0) {
            this.displayColor = NEGATIVE_CHARGE_COLOR;
        } else {
            this.displayColor = NEUTRAL_CHARGE_COLOR;
        }
    }

    // Display the test charge as an ellipse
    public void display(PApplet app) {
        app.pushMatrix();
        app.stroke(0);
        app.fill(displayColor);
        app.ellipse(position.x, position.y, TEST_CHARGE_DIAMETER, TEST_CHARGE_DIAMETER);
        app.popMatrix();
    }

    // Update the position based on a given force vector.
    public void move(ArrayList<PointCharge> pointCharges) {
        PVector force = netForceAtPoint(position, pointCharges).mult(-1); //new PVector(position.x, position.y);
        if (!Float.isInfinite(force.mag())) {
            // a = (qE) and assume mass = 1 for simplicity
            acceleration = force.copy().mult(charge);
            velocity.add(acceleration);
            position.add(velocity);
        }
    }
}
