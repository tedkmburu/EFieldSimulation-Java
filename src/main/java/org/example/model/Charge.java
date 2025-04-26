package org.example.model;

import processing.core.PApplet;
import processing.core.PVector;

abstract public class Charge {
    protected PVector position;
    protected Float charge;

    public Charge(PVector position, Float charge) {
        this.position = position;
        this.charge = charge;
    }

    public PVector getPosition() {
        return position;
    }

    public Float getCharge() {
        return charge;
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    public void setCharge(Float newCharge) {
        this.charge = newCharge;
    }

    // Basic display method; override in subclasses
    public void display(PApplet app) {
        app.fill(200);
        app.ellipse(position.x, position.y, 20, 20);
    }
}
