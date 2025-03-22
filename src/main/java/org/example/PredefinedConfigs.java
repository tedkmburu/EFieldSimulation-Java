package org.example;

import processing.core.PVector;

import static org.example.Constants.GRID_SIZE;
import static org.example.Constants.SIDE_PANEL_WIDTH;

public class PredefinedConfigs {

    /**
     * Sets up a "single charge" configuration.
     * @param simulation The simulation to update.
     */
    public static void setSingleConfiguration(Simulation simulation) {
        simulation.removeAllPointCharges();
        // For example, add a single charge at the center:
        simulation.addPointCharge(new PVector(simulation.getWidth() / 2, simulation.getHeight() / 2), 5f);
    }

    /**
     * Sets up a dipole configuration.
     * @param simulation The simulation to update.
     */
    public static void setDipoleConfiguration(Simulation simulation) {
        simulation.removeAllPointCharges();
        simulation.addPointCharge(new PVector(300, simulation.getHeight() / 2), 5f);
        simulation.addPointCharge(new PVector(500, simulation.getHeight() / 2), -5f);
    }

    /**
     * Sets up a row configuration.
     * @param simulation The simulation to update.
     */
    public static void setRowConfiguration(Simulation simulation) {
        simulation.removeAllPointCharges();
        int numCharges = 4;
        float spacing = 75;
        float y = simulation.getHeight() / 2;
        for (int i = 1; i <= numCharges; i++) {
            simulation.addPointCharge(new PVector((i * spacing) + (simulation.getWidth()/2), y),5f);
        }
    }

    /**
     * Sets up a dipole row configuration.
     * @param simulation The simulation to update.
     */
    public static void setDipoleRowConfiguration(Simulation simulation) {
        simulation.removeAllPointCharges();
        int numDipoles = 4;
        float spacing = 75;
        float y = simulation.getHeight() / 2;
        for (int i = 1; i <= numDipoles; i++) {
            // For each dipole, add two charges close together.
            simulation.addPointCharge(new PVector((i * spacing) + (simulation.getWidth()/2), y), 5f);
            simulation.addPointCharge(new PVector((i * spacing) + (simulation.getWidth()/2), y + 100), -5f);
        }
    }

    /**
     * Sets up a random configuration.
     * @param simulation The simulation to update.
     */
    public static void setRandomConfiguration(Simulation simulation) {
        simulation.removeAllPointCharges();
        int numCharges = 10;
        for (int i = 0; i < numCharges; i++) {
            float x = (float) (Math.random() * (simulation.getWidth() - SIDE_PANEL_WIDTH));
            float y = (float) (Math.random() * simulation.getHeight());
            // Random charge: either 5 or -5
            float charge = (Math.random() < 0.5) ? 5 : -5;
            simulation.addPointCharge(new PVector(x, y), charge);
        }
    }

    /**
     * Creates a test charge map in the simulation.
     * @param simulation The simulation to update.
     */
    public static void createTestChargeMap(Simulation simulation) {
        clearTestCharges(simulation);

        Integer cols = (int) ((simulation.getWidth() - SIDE_PANEL_WIDTH) / GRID_SIZE);
        Integer rows = (int) (simulation.getHeight() / GRID_SIZE);

        // Fill the array with a single color (e.g., blue)
        for (int x = 0; x < cols; x+=2) {
            for (int y = 0; y < rows; y+=2) {
                PVector pos = new PVector(x * GRID_SIZE, y * GRID_SIZE);
                simulation.addTestCharge(pos);
            }
        }
    }

    /**
     * Clears all test charges from the simulation.
     * @param simulation The simulation to update.
     */
    public static void clearTestCharges(Simulation simulation) {
        simulation.clearTestCharges();
    }
}
