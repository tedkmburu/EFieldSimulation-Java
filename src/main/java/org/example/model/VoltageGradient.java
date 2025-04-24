package org.example.model;

import org.example.model.config.ConfigManager;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.core.PApplet;

import java.util.ArrayList;

import static processing.core.PApplet.map;

public class VoltageGradient {
    int[][] grid;
    int voltageFidelity = ConfigManager.getInstance().getGridSize() / 3;
    int cols;
    int rows;
    public final PGraphics pg;

    public VoltageGradient(SimulationModel simulation) {
        cols = (int) ((simulation.getWidth() - ConfigManager.getInstance().getSidePanelWidth() - ConfigManager.getInstance().getSidePanelPadding()) / voltageFidelity);
        rows = (int) (simulation.getHeight() / voltageFidelity);

        pg = simulation.parent.createGraphics((int) simulation.getWidth(), (int) simulation.getHeight());
    }

    public void display(PApplet app) {
        // draw the voltage‐color buffer at the origin
        app.pushStyle();        // preserve any styles the caller has set
        app.image(pg, 0, 0);    // blit the PGraphics to the screen
        app.popStyle();
    }

    public void updateVoltageGradient(ArrayList<PointCharge> pointCharges) {
        pg.beginDraw();
        pg.clear();
        pg.noStroke();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                float fx = x * voltageFidelity, fy = y * voltageFidelity;
                float v    = CommonMath.voltageAtPoint(new PVector(fx, fy), pointCharges);

                // compute color
                int c;
                float absV = Math.abs(v);
                float intensity = Math.round(
                        map(absV, 0, 1000000, 0, 200)
                );
                float red=0, blue=0, alpha=0;
                if (Math.abs(v) >= 10f && intensity*5 >= 50f) {
                    if (v>0) red = intensity;
                    else    blue = intensity;
                    alpha = intensity*5;
                }
                c = pg.color(red, 0, blue, alpha);

                // draw the cell onto the buffer
                pg.fill(c);
                pg.rect(fx, fy, voltageFidelity, voltageFidelity);
            }
        }

//        // now apply a Gaussian blur of radius 4 pixels:
//        pg.filter(PApplet.BLUR, 2);

        pg.endDraw();
    }
}
