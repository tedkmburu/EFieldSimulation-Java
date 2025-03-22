package org.example.model;

import org.example.Simulation;
import processing.core.PVector;
import processing.core.PApplet;

import java.util.ArrayList;

import static org.example.CommonMath.*;
import static org.example.Constants.*;
import static org.example.Simulation.*;

public class VoltageGradient {
    int[][] grid;
    int voltageFidelity = GRID_SIZE / 5;
    int cols;
    int rows;

    public VoltageGradient(Simulation simulation) {
        cols = (int) ((simulation.getWidth() - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING) / voltageFidelity);
        rows = (int) (simulation.getHeight() / voltageFidelity);
    }

    public void display(PApplet app) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                app.fill(grid[x][y]); // Set color
                app.noStroke();
                app.rect(x * voltageFidelity, y * voltageFidelity, voltageFidelity, voltageFidelity); // Draw 1-pixel-wide columns
            }
        }
    }

    public void updateVoltageGradient(PApplet app, ArrayList<PointCharge> pointCharges) {

        // Initialize the 2D array

        grid = new int[cols][rows];

        // Fill the array with a single color (e.g., blue)
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {

                PVector cellPosition = new PVector(x * voltageFidelity, y * voltageFidelity);
                float voltageAtCell = voltageAtPoint(cellPosition, pointCharges);

                float intensity = Math.round(app.map(Math.abs(voltageAtCell), 0, 1000000, 0, 200));

                float red = 0.0F;
                float blue = 0.0F;
                float alpha = intensity * 5.0F;

                if (Math.abs(voltageAtCell) < 10) {
                    red = 0.0F;
                    blue = 0.0F;
                    alpha = 0.0F;
                } else if (voltageAtCell > 0) {
                    red = intensity;
                } else if (voltageAtCell < 0) {
                    blue = intensity;
                } else {
                    alpha = 0.0F;
                }

                grid[x][y] = app.color(red, 0, blue, alpha);

            }
        }
    }


}
