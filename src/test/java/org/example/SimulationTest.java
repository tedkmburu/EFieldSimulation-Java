package org.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.example.model.SimulationModel;
import org.example.model.PointCharge;
import org.example.model.TestCharge;
import org.example.view.ui.ControlPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PVector;

public class SimulationTest {
    private PApplet app;

    @BeforeEach
    public void setup() {
        // Create a minimal PApplet instance with essential fields set.
        app = new PApplet();
        app.width = 800;
        app.height = 600;
        app.displayWidth = 800;
        app.displayHeight = 600;
        app.mouseX = 100;
        app.mouseY = 100;
    }

    /**
     * Creates a ControlPanel instance that skips UI initialization.
     * We override the mode getters to force the simulation into desired states.
     */
    private ControlPanel createControlPanel(PApplet app,
                                            boolean fieldVectors,
                                            boolean testCharge,
                                            boolean fieldLines,
                                            boolean voltage,
                                            boolean grid) {
        return new ControlPanel(app, null, true) {
            @Override public boolean showFieldVectorsMode() { return fieldVectors; }
            @Override public boolean testChargeMode() { return testCharge; }
            @Override public boolean showFieldLinesMode() { return fieldLines; }
            @Override public boolean showVoltageMode() { return voltage; }
            @Override public boolean showGridMode() { return grid; }
        };
    }

    // --- Test: Constructor Initialization ---
    @Test
    public void testConstructorInitialization() {
        ControlPanel cp = createControlPanel(app, false, false, false, false, false);
        SimulationModel simulation = new SimulationModel(app, cp);
        ArrayList<PointCharge> pointCharges = simulation.getPointCharges();
        assertTrue(pointCharges.size() == 2, "Should have exactly 2 point charges on initialization");
        boolean hasPositive = false;
        boolean hasNegative = false;
        for (PointCharge charge : pointCharges) {
            if (charge.getCharge() > 0) {
                hasPositive = true;
            } else if (charge.getCharge() < 0) {
                hasNegative = true;
            }
        }
        assertTrue(hasPositive, "Should contain a positive charge");
        assertTrue(hasNegative, "Should contain a negative charge");
    }

    // --- Test: Update – Field Vectors Mode ---
    @Test
    public void testUpdateFieldVectorsMode() throws Exception {
        ControlPanel cp = createControlPanel(app, true, false, false, false, false);
        SimulationModel simulation = new SimulationModel(app, cp);
        // Use reflection to access the private field "fieldVectors"
        Field fieldVectorsField = SimulationModel.class.getDeclaredField("fieldVectors");
        fieldVectorsField.setAccessible(true);
        ArrayList<?> fieldVectorsBefore = (ArrayList<?>) fieldVectorsField.get(simulation);
        int sizeBefore = fieldVectorsBefore.size();
        simulation.update();
        ArrayList<?> fieldVectorsAfter = (ArrayList<?>) fieldVectorsField.get(simulation);
        assertTrue(fieldVectorsAfter.size() > sizeBefore, "Field vectors should be populated when showFieldVectorsMode is true.");
    }

    @Test
    public void testUpdateTestChargeMode() {
        ControlPanel cp = createControlPanel(app, false, true, false, false, false);
        SimulationModel simulation = new SimulationModel(app, cp);
        // Add a test charge at a known position.
        PVector initialPos = new PVector(100, 100);
        simulation.addTestCharge(initialPos);
        TestCharge testCharge = simulation.getTestCharges().get(0);
        PVector beforeUpdate = testCharge.getPosition().copy();
        simulation.update();
        PVector afterUpdate = testCharge.getPosition();
        assertTrue(!beforeUpdate.equals(afterUpdate), "Test charge position should change after update in test charge mode.");
    }

    // --- Test: Update – Field Lines and Voltage Modes ---
    @Test
    public void testUpdateFieldLinesAndVoltageModes() throws Exception {
        ControlPanel cp = createControlPanel(app, false, false, true, true, false);
        SimulationModel simulation = new SimulationModel(app, cp);
        simulation.update();
        // Verify fieldLines is populated.
        Field fieldLinesField = SimulationModel.class.getDeclaredField("fieldLines");
        fieldLinesField.setAccessible(true);
        ArrayList<?> fieldLines = (ArrayList<?>) fieldLinesField.get(simulation);
        assertTrue(fieldLines.size() > 0, "Field lines should be created when showFieldLinesMode is active.");

        // Verify the voltage grid is updated.
        Field voltageGradientField = SimulationModel.class.getDeclaredField("voltageGradient");
        voltageGradientField.setAccessible(true);
        Object voltageGradientObj = voltageGradientField.get(simulation);
        Field gridField = voltageGradientObj.getClass().getDeclaredField("grid");
        gridField.setAccessible(true);
        int[][] grid = (int[][]) gridField.get(voltageGradientObj);
        boolean gridPopulated = false;
        for (int[] row : grid) {
            for (int color : row) {
                if (color != 0) {
                    gridPopulated = true;
                    break;
                }
            }
            if (gridPopulated) break;
        }
        assertTrue(gridPopulated, "Voltage grid should be updated when showVoltageMode is active.");
    }

    // --- Test: Charge Selection ---
    @Test
    public void testChargeSelection() {
        ControlPanel cp = createControlPanel(app, false, false, false, false, false);
        SimulationModel simulation = new SimulationModel(app, cp);
        // Use a point inside the first charge's circle (default first charge at (200,200)).
        PVector pos = new PVector(200, 200);
        boolean result = simulation.selectChargeAtPosition(pos);
        assertTrue(result, "selectChargeAtPosition should return true when a charge is selected.");
        boolean selectedFlag = false;
        for (PointCharge pc : simulation.getPointCharges()) {
            if (PVector.dist(pc.getPosition(), pos) < Constants.CHARGE_RADIUS && pc.selected) {
                selectedFlag = true;
                break;
            }
        }
        assertTrue(selectedFlag, "Charge within the radius should have its selected flag set to true.");
    }

    // --- Test: Adding Charges ---
    @Test
    public void testAddingCharges() {
        ControlPanel cp = createControlPanel(app, false, false, false, false, false);
        SimulationModel simulation = new SimulationModel(app, cp);
        int initialTestChargeCount = simulation.getTestCharges().size();
        PVector testPos = new PVector(50, 50);
        simulation.addTestCharge(testPos);
        assertTrue(simulation.getTestCharges().size() == initialTestChargeCount + 1, "Test charge should be added.");

        int initialPointChargeCount = simulation.getPointCharges().size();
        PVector pointPos = new PVector(300, 300);
        simulation.addPointCharge(pointPos, 1f);
        assertTrue(simulation.getPointCharges().size() == initialPointChargeCount + 1, "Point charge should be added.");
        PointCharge newCharge = simulation.getPointCharges().get(simulation.getPointCharges().size() - 1);
        assertTrue(newCharge.selected, "Newly added point charge should be selected.");
    }

    // --- Test: Reset States ---
    @Test
    public void testResetStates() {
        ControlPanel cp = createControlPanel(app, false, false, false, false, false);
        SimulationModel simulation = new SimulationModel(app, cp);
        // Manually set flags to true for each point charge.
        for (PointCharge pc : simulation.getPointCharges()) {
            pc.selected = true;
            pc.dragging = true;
        }
        simulation.resetChargeStates();
        for (PointCharge pc : simulation.getPointCharges()) {
            assertTrue(!pc.selected && !pc.dragging, "After reset, no charge should be selected or dragging.");
        }
    }
}
