package org.example.model;

import org.example.model.config.ConfigManager;
import processing.core.PVector;

public class PresetConfigurator {

    public static final Float SINGLE_CHARGE_VALUE        = 5f;
    public static final Float DIPOLE_CHARGE_MAGNITUDE    = 5f;
    public static final Integer   DIPOLE_SPACING_MULTIPLIER  = 10; // × GRID_SIZE
    public static final Integer   ROW_COUNT                  = 4;
    public static final Integer   ROW_SPACING_MULTIPLIER     = 3;  // × GRID_SIZE
    public static final Integer   DIPOLE_ROW_COUNT           = 4;
    public static final Integer   DIPOLE_ROW_SPACING_MULTIPLIER = ROW_SPACING_MULTIPLIER;
    public static final Integer   DIPOLE_ROW_VERTICAL_FACTOR   = 1; // × spacing
    public static final Integer   RANDOM_CHARGE_COUNT        = 10;
    public static final Integer   TEST_CHARGE_MAP_STEP       = 2;  // add charge every 2 intersecting grid lines

    // helper: center of the simulation
    private static PVector center(SimulationModel sim) {
        return new PVector((sim.getWidth() - (ConfigManager.getInstance().getSidePanelWidth() + ConfigManager.getInstance().getSidePanelPadding()))/2f, sim.getHeight()/2f);
    }

    // helper: round to nearest GRID_SIZE multiple
    private static PVector snapToGrid(PVector p) {
        Float g = ConfigManager.getInstance().getGridSize();
        Float x = Math.round(p.x / g) * g;
        Float y = Math.round(p.y / g) * g;
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
        Float spacing = (float) (DIPOLE_SPACING_MULTIPLIER * ConfigManager.getInstance().getGridSize());
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

        Integer num = ROW_COUNT;
        Float spacing = (ConfigManager.getInstance().getGridSize() * ROW_SPACING_MULTIPLIER);
        PVector mid = center(simulation);
        // start so that the whole row is centered
        Float totalWidth = spacing * (num - 1);
        Float startX = mid.x - totalWidth/2;

        for (Integer i = 0; i < num; i++) {
            PVector p = new PVector(startX + i*spacing, mid.y);
            simulation.addPointCharge(snapToGrid(p), 5f);
        }
    }

    public static void setDipoleRowConfiguration(SimulationModel simulation) {
        simulation.removeAllPointCharges();
        simulation.clearEquipotentialLines();

        Integer    numDipoles    = DIPOLE_ROW_COUNT;
        Float  spacing       = (ConfigManager.getInstance().getGridSize() * DIPOLE_ROW_SPACING_MULTIPLIER);
        PVector mid          = center(simulation);
        Float  totalWidth    = spacing * (numDipoles - 1);
        Float  startX        = mid.x - totalWidth / 2;
        Float  verticalShift = spacing * DIPOLE_ROW_VERTICAL_FACTOR;           // vertical separation between + and –

        for (Integer i = 0; i < numDipoles; i++) {
            Float x = startX + i * spacing;
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

        Integer numCharges = RANDOM_CHARGE_COUNT;
        for (Integer i = 0; i < numCharges; i++) {
            Float x = (float) (Math.random() * (simulation.getWidth() - ConfigManager.getInstance().getSidePanelWidth()));
            Float y = (float) (Math.random() * simulation.getHeight());
            // Random charge: either 5 or -5
            Float charge = (Math.random() < 0.5) ? ConfigManager.getInstance().getPointChargeMaxValue() : ConfigManager.getInstance().getPointChargeMinValue();
            simulation.addPointCharge(new PVector(x, y), charge);
        }
    }

    public static void createTestChargeMap(SimulationModel simulation) {
        clearTestCharges(simulation);
        simulation.clearEquipotentialLines();

        Float GRID_SIZE = ConfigManager.getInstance().getGridSize();
        Integer cols = (int) ((simulation.getWidth() - ConfigManager.getInstance().getGridSize()) / GRID_SIZE);
        Integer rows = (int) (simulation.getHeight() / GRID_SIZE);

        // Fill the array with a single color (e.g., blue)
        for (Integer x = 0; x < cols; x+=TEST_CHARGE_MAP_STEP) {
            for (Integer y = 0; y < rows; y+=TEST_CHARGE_MAP_STEP) {
                PVector pos = new PVector(x * GRID_SIZE, y * GRID_SIZE);
                simulation.addTestCharge(pos);
            }
        }
    }

    public static void clearTestCharges(SimulationModel simulation) {
        simulation.clearTestCharges();
    }
}
