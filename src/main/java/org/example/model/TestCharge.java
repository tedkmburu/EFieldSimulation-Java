package org.example.model;

import org.example.model.config.ConfigManager;
import org.example.view.style.Style;
import org.example.view.style.StyleFactory;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static org.example.model.CommonMath.*;


public class TestCharge extends Charge {

    private PVector velocity;
    private PVector acceleration;
    private float radius;
    private float diameter;
    private int displayColor; // Processing color

    private static final Style POS_TC = StyleFactory.getStyle(ConfigManager.getInstance().getPositiveChargeColor(), 0, 1f, true);
    private static final Style NEG_TC = StyleFactory.getStyle(ConfigManager.getInstance().getNegativeChargeColor(), 0, 1f, true);
    private static final Style NEU_TC = StyleFactory.getStyle(ConfigManager.getInstance().getNeutralChargeColor(), 0, 1f, true);

    public TestCharge(PVector position, float charge) {
        super(position, charge);
        this.velocity = createVector();
        this.acceleration = createVector();
        this.radius = ConfigManager.getInstance().getTestChargeRadius();
        this.diameter = ConfigManager.getInstance().getTestChargeDiameter();

    }

    // Display the test charge as an ellipse
    public void display(PApplet app) {
            app.pushMatrix();
            Style sty = charge>0 ? POS_TC : charge<0 ? NEG_TC : NEU_TC;
            sty.apply(app);
            app.ellipse(position.x, position.y, diameter, diameter);
            app.popMatrix();
    }

    // Update the position based on a given force vector.
    public void move(ArrayList<PointCharge> pointCharges) {
        PVector force = netForceAtPoint(position, pointCharges).mult(-1);
        if (!Float.isInfinite(force.mag())) {
            // a = (qE) and assume mass = 1 for simplicity
            acceleration = force.copy().mult(charge);
            velocity.add(acceleration);
            position.add(velocity);
        }
    }
}
