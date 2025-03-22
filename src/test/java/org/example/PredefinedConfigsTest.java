package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.example.model.PointCharge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PVector;

public class PredefinedConfigsTest {

    private PApplet app;
    private Simulation simulation;
    private ControlPanel controlPanel;

    @BeforeEach
    public void setup() {
        // Create a minimal PApplet with necessary dimensions.
        app = new PApplet();
        app.width = 800;
        app.height = 600;
        app.displayWidth = 800;
        app.displayHeight = 600;
        // Create a ControlPanel in no-UI mode.
        controlPanel = new ControlPanel(app, null, true) {
            // For these tests, mode getters are not needed.
        };
        simulation = new Simulation(app, controlPanel);
    }

    // Single Configuration:
    // After calling setSingleConfiguration(simulation), assert that simulation’s point charge list
    // has exactly one charge positioned at the center.
    @Test
    public void testSingleConfiguration() {
        PredefinedConfigs.setSingleConfiguration(simulation);
        assertEquals(1, simulation.getPointCharges().size(), "There should be exactly one point charge");

        PointCharge charge = simulation.getPointCharges().get(0);
        float expectedX = app.width / 2f;
        float expectedY = app.height / 2f;
        // Use a small tolerance for floating point comparison.
        assertTrue(Math.abs(charge.getPosition().x - expectedX) < 0.001,
                "Charge x position should be at the center (" + expectedX + ")");
        assertTrue(Math.abs(charge.getPosition().y - expectedY) < 0.001,
                "Charge y position should be at the center (" + expectedY + ")");
    }

    // Dipole Configuration:
    // Verify that setDipoleConfiguration(simulation) clears existing charges and adds exactly two
    // charges with the correct polarity (+5 and -5).
    @Test
    public void testDipoleConfiguration() {
        // First, add a dummy charge to verify removal.
        simulation.addPointCharge(new PVector(0, 0), 0f);
        PredefinedConfigs.setDipoleConfiguration(simulation);
        assertEquals(2, simulation.getPointCharges().size(), "Dipole configuration should result in exactly 2 point charges");

        PointCharge charge1 = simulation.getPointCharges().get(0);
        PointCharge charge2 = simulation.getPointCharges().get(1);

        // Expected positions: (300, height/2) with +5, and (500, height/2) with -5.
        float centerY = app.height / 2f;
        assertTrue(Math.abs(charge1.getPosition().x - 300) < 0.001 &&
                Math.abs(charge1.getPosition().y - centerY) < 0.001 &&
                charge1.getCharge() > 0, "First dipole charge should be at (300, height/2) with positive polarity");

        assertTrue(Math.abs(charge2.getPosition().x - 500) < 0.001 &&
                Math.abs(charge2.getPosition().y - centerY) < 0.001 &&
                charge2.getCharge() < 0, "Second dipole charge should be at (500, height/2) with negative polarity");
    }

    // Row Configuration:
    // Check that setRowConfiguration(simulation) results in exactly four charges arranged with correct spacing.
    @Test
    public void testRowConfiguration() {
        PredefinedConfigs.setRowConfiguration(simulation);
        assertEquals(4, simulation.getPointCharges().size(), "Row configuration should create 4 point charges");

        float spacing = 75f;
        float baseX = app.width / 2f;
        float expectedY = app.height / 2f;

        for (int i = 0; i < 4; i++) {
            PointCharge charge = simulation.getPointCharges().get(i);
            float expectedX = (i + 1) * spacing + baseX;
            assertTrue(Math.abs(charge.getPosition().x - expectedX) < 0.001,
                    "Charge " + (i + 1) + " x position should be " + expectedX);
            assertTrue(Math.abs(charge.getPosition().y - expectedY) < 0.001,
                    "Charge " + (i + 1) + " y position should be " + expectedY);
        }
    }

    // Dipole Row Configuration:
    // Ensure that setDipoleRowConfiguration(simulation) produces the expected number of charges (8 total)
    // with each dipole arranged as expected.
    @Test
    public void testDipoleRowConfiguration() {
        PredefinedConfigs.setDipoleRowConfiguration(simulation);
        assertEquals(8, simulation.getPointCharges().size(), "Dipole row configuration should produce 8 charges (4 dipoles)");

        float spacing = 75f;
        float baseX = app.width / 2f;
        float y = app.height / 2f;
        // Loop through each dipole.
        for (int i = 0; i < 4; i++) {
            // Each dipole: first charge at ( (i+1)*spacing + baseX, y ) with +5;
            // second charge at ( (i+1)*spacing + baseX, y + 100 ) with -5.
            int idx = i * 2;
            PointCharge positiveCharge = simulation.getPointCharges().get(idx);
            PointCharge negativeCharge = simulation.getPointCharges().get(idx + 1);
            float expectedX = (i + 1) * spacing + baseX;

            assertTrue(Math.abs(positiveCharge.getPosition().x - expectedX) < 0.001 &&
                            Math.abs(positiveCharge.getPosition().y - y) < 0.001 &&
                            positiveCharge.getCharge() > 0,
                    "Dipole " + (i + 1) + " positive charge is not positioned correctly.");

            assertTrue(Math.abs(negativeCharge.getPosition().x - expectedX) < 0.001 &&
                            Math.abs(negativeCharge.getPosition().y - (y + 100)) < 0.001 &&
                            negativeCharge.getCharge() < 0,
                    "Dipole " + (i + 1) + " negative charge is not positioned correctly.");
        }
    }

    // Random Configuration:
    // After setRandomConfiguration(simulation), assert that there are 10 charges and that their positions
    // fall within the simulation’s width (minus side panel) and height.
    @Test
    public void testRandomConfiguration() {
        PredefinedConfigs.setRandomConfiguration(simulation);
        assertEquals(10, simulation.getPointCharges().size(), "Random configuration should create 10 point charges");

        for (PointCharge charge : simulation.getPointCharges()) {
            float x = charge.getPosition().x;
            float y = charge.getPosition().y;
            // x should be between 0 and (width - SIDE_PANEL_WIDTH) and y between 0 and height.
            assertTrue(x >= 0 && x <= app.width - Constants.SIDE_PANEL_WIDTH,
                    "Charge x position should be within the valid range");
            assertTrue(y >= 0 && y <= app.height,
                    "Charge y position should be within the valid range");
            // Charge value should be either 5 or -5.
            assertTrue(charge.getCharge() == 5f || charge.getCharge() == -5f,
                    "Charge value should be either +5 or -5");
        }
    }

    // Test Charge Map:
    // Call createTestChargeMap(simulation) and check that the testCharges list is populated
    // according to the grid dimensions computed from GRID_SIZE.
    @Test
    public void testCreateTestChargeMap() {
        PredefinedConfigs.createTestChargeMap(simulation);
        int testChargeCount = simulation.getTestCharges().size();

        // Calculate expected number of test charges.
        int cols = (int) ((app.width - Constants.SIDE_PANEL_WIDTH) / Constants.GRID_SIZE);
        int rows = (int) (app.height / Constants.GRID_SIZE);
        // Charges are added in steps of 2 for both x and y:
        int expectedCols = (cols + 1) / 2; // integer division rounding down is acceptable if cols is even.
        int expectedRows = (rows + 1) / 2;
        int expectedCount = expectedCols * expectedRows;
        assertEquals(expectedCount, testChargeCount,
                "Test charge map should populate " + expectedCount + " test charges, but got " + testChargeCount);
    }

    // Clear Test Charges:
    // Verify that clearTestCharges(simulation) empties the testCharges list.
    @Test
    public void testClearTestCharges() {
        // First, populate some test charges.
        simulation.addTestCharge(new PVector(10, 10));
        simulation.addTestCharge(new PVector(20, 20));
        assertTrue(!simulation.getTestCharges().isEmpty(), "There should be test charges before clearing.");
        PredefinedConfigs.clearTestCharges(simulation);
        assertTrue(simulation.getTestCharges().isEmpty(), "Test charges list should be empty after clearing.");
    }
}
