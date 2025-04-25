package org.example.view.style;

import processing.core.PApplet;

public class Style {
    private final Integer fillColor;
    private final Integer strokeColor;
    private final Float strokeWeight;
    private final Boolean useFill; // true ⇒ fill, false ⇒ noFill

    Style(Integer fillColor, Integer strokeColor, Float strokeWeight, Boolean useFill) {
        this.fillColor    = fillColor;
        this.strokeColor  = strokeColor;
        this.strokeWeight = strokeWeight;
        this.useFill      = useFill;
    }

    public void apply(PApplet app) {
        if (useFill) {
            app.fill(fillColor);
        }
        app.stroke(strokeColor);
        app.strokeWeight(strokeWeight);
    }
}
