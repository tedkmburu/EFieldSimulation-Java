package org.example.model;

import java.util.ArrayList;

import org.example.model.config.ConfigManager;
import processing.core.PApplet;
import processing.core.PVector;


public class FieldLineBuilder {
    private final PApplet app;
    private final PVector start;
    private final ArrayList<PointCharge> pointCharges;

    private int maxLoops = 500;
    private float stepSize = ConfigManager.getInstance().getChargeRadius();
    private int arrowInterval = 5;

    public FieldLineBuilder(PApplet app, PVector start, ArrayList<PointCharge> pointCharges) {
        this.app     = app;
        this.start   = start;
        this.pointCharges = pointCharges;
    }

    public FieldLine build() {
        FieldLineConfig config =
                new FieldLineConfig(maxLoops, stepSize, arrowInterval);
        return new FieldLine(app, start, pointCharges, config);
    }
}
