package org.example.model;

import processing.core.PApplet;
import processing.core.PVector;

public class FieldVector {
    private PVector position;
    private PVector forceVector;
    private Float forceMag;
    private Float scale;
    private PVector end;
    private Integer color;

    public FieldVector(PVector position, PVector forceVector) {
        this.position = position.copy();
        this.forceVector = forceVector.copy().copy();
        this.forceMag = this.forceVector.mag();
        this.scale = this.forceMag / 10.0F;
        this.end = PVector.sub(position, forceVector);
        this.color = 0xFFFFFFFF;
    }

    // Draw the field vector if its magnitude exceeds a threshold.
    public void display(PApplet app) {
        if (forceMag > 0.1) {
            // Draw arrow stem
            app.pushMatrix();
            app.stroke(color);
            app.strokeWeight(this.scale / 2);
            app.line(position.x, position.y, end.x, end.y);

            // Draw arrow head
            app.pushMatrix();
            app.translate(this.end.x, this.end.y);
            app.rotate(forceVector.mult(-1).heading());
            app.fill(color);
            app.noStroke();
            app.triangle(0, 0, -4 * scale, -2 * scale, -4 * scale, 2 * scale);
            app.popMatrix();
            app.popMatrix();
        }
    }
}
