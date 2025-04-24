package org.example.view.style;

import processing.core.PApplet;

public class Style {
    private final int fillColor;
    private final int strokeColor;
    private final float strokeWeight;
    private final boolean useFill; // true ⇒ fill, false ⇒ noFill

    Style(int fillColor, int strokeColor, float strokeWeight, boolean useFill) {
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

    // equals/hashCode based on all fields…
}
