package org.example.model;

import org.example.model.config.ConfigManager;
import processing.core.PVector;

public class PresetConfigurator {

    public static final float SINGLE_CHARGE_VALUE        = 5f;
    public static final float DIPOLE_CHARGE_MAGNITUDE    = 5f;
    public static final int   DIPOLE_SPACING_MULTIPLIER  = 10; // × GRID_SIZE
    public static final int   ROW_COUNT                  = 4;
    public static final int   ROW_SPACING_MULTIPLIER     = 3;  // × GRID_SIZE
    public static final int   DIPOLE_ROW_COUNT           = 4;
    public static final int   DIPOLE_ROW_SPACING_MULTIPLIER = ROW_SPACING_MULTIPLIER;
    public static final int   DIPOLE_ROW_VERTICAL_FACTOR   = 1; // × spacing
    public static final int   RANDOM_CHARGE_COUNT        = 10;
    public static final int   TEST_CHARGE_MAP_STEP       = 2;  // add charge every 2 intersecting grid lines

    // helper: center of the simulation
    private static PVector center(SimulationModel sim) {
        return new PVector((sim.getWidth() - (ConfigManager.getInstance().getSidePanelWidth() + ConfigManager.getInstance().getSidePanelPadding()))/2f, sim.getHeight()/2f);
    }

    // helper: round to nearest GRID_SIZE multiple
    private static PVector snapToGrid(PVector p) {
        float g = ConfigManager.getInstance().getGridSize();
        float x = Math.round(p.x / g) * g;
        float y = Math.round(p.y / g) * g;
        return new PVector(x, y);
    }

    public static void setSingleConfiguration(SimulationModel simulation) {

        simulation.clearEquipotentialLines();
        simulation.removeAllPointCharges();

        // compute and snap center
        PVector c = snapToGrid(center(simulation));
        simulation.addPointCharge(c, SINGLE_CHARGE_VALUE);
    }

    public static void setDipoleConfiguration(SimulationModel simulation) {
        simulation.clearEquipotentialLines();
        simulation.removeAllPointCharges();

        // pick a spacing in grid‐units (e.g. 4 cells apart)
        float spacing = DIPOLE_SPACING_MULTIPLIER * ConfigManager.getInstance().getGridSize();
        PVector mid = center(simulation);

        // left and right positions, then snap each
        PVector left  = snapToGrid(new PVector(mid.x - spacing/2, mid.y));
        PVector right = snapToGrid(new PVector(mid.x + spacing/2, mid.y));

        simulation.addPointCharge(left,  +DIPOLE_CHARGE_MAGNITUDE);
        simulation.addPointCharge(right, -DIPOLE_CHARGE_MAGNITUDE);
    }

    public static void setRowConfiguration(SimulationModel simulation) {
        simulation.clearEquipotentialLines();
        simulation.removeAllPointCharges();

        int num = ROW_COUNT;
        float spacing = ConfigManager.getInstance().getGridSize() * ROW_SPACING_MULTIPLIER;
        PVector mid = center(simulation);
        // start so that the whole row is centered
        float totalWidth = spacing * (num - 1);
        float startX = mid.x - totalWidth/2;

        for (int i = 0; i < num; i++) {
            PVector p = new PVector(startX + i*spacing, mid.y);
            simulation.addPointCharge(snapToGrid(p), 5f);
        }
    }

    public static void setDipoleRowConfiguration(SimulationModel simulation) {
        simulation.removeAllPointCharges();
        simulation.clearEquipotentialLines();

        int    numDipoles    = DIPOLE_ROW_COUNT;
        float  spacing       = ConfigManager.getInstance().getGridSize() * DIPOLE_ROW_SPACING_MULTIPLIER;
        PVector mid          = center(simulation);
        float  totalWidth    = spacing * (numDipoles - 1);
        float  startX        = mid.x - totalWidth / 2;
        float  verticalShift = spacing * DIPOLE_ROW_VERTICAL_FACTOR;           // vertical separation between + and –

        for (int i = 0; i < numDipoles; i++) {
            float x = startX + i * spacing;
            // positive pole (above center)
            PVector pos = snapToGrid(new PVector(x, mid.y - verticalShift/2));
            simulation.addPointCharge(pos, +DIPOLE_CHARGE_MAGNITUDE);
            // negative pole (below center)
            PVector neg = snapToGrid(new PVector(x, mid.y + verticalShift/2));
            simulation.addPointCharge(neg, -DIPOLE_CHARGE_MAGNITUDE);
        }
    }

    public static void setRandomConfiguration(SimulationModel simulation) {
        simulation.removeAllPointCharges();
        simulation.clearEquipotentialLines();

        int numCharges = RANDOM_CHARGE_COUNT;
        for (int i = 0; i < numCharges; i++) {
            float x = (float) (Math.random() * (simulation.getWidth() - ConfigManager.getInstance().getSidePanelWidth()));
            float y = (float) (Math.random() * simulation.getHeight());
            // Random charge: either 5 or -5
            float charge = (Math.random() < 0.5) ? ConfigManager.getInstance().getPointChargeMaxValue() : ConfigManager.getInstance().getPointChargeMinValue();
            simulation.addPointCharge(new PVector(x, y), charge);
        }
    }

    public static void createTestChargeMap(SimulationModel simulation) {
        clearTestCharges(simulation);
        simulation.clearEquipotentialLines();

        Integer GRID_SIZE = ConfigManager.getInstance().getGridSize();
        Integer cols = (int) ((simulation.getWidth() - ConfigManager.getInstance().getGridSize()) / GRID_SIZE);
        Integer rows = (int) (simulation.getHeight() / GRID_SIZE);

        // Fill the array with a single color (e.g., blue)
        for (int x = 0; x < cols; x+=TEST_CHARGE_MAP_STEP) {
            for (int y = 0; y < rows; y+=TEST_CHARGE_MAP_STEP) {
                PVector pos = new PVector(x * GRID_SIZE, y * GRID_SIZE);
                simulation.addTestCharge(pos);
            }
        }
    }

    public static void clearTestCharges(SimulationModel simulation) {
        simulation.clearTestCharges();
    }
}
