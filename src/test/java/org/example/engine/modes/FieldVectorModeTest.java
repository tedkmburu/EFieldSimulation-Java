package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import org.example.factories.ProcessingFieldElementFactory;
import org.example.model.config.ConfigManager;
import org.example.model.PointCharge;
import processing.core.PApplet;
import processing.core.PVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FieldVectorModeTest {

    private PApplet pap;
    private SimulationModel sim;
    private ControlPanel controlPanel;
    private FieldVectorMode mode;
    private Field fieldVectorsField;
    private ConfigManager cm;

    @BeforeEach
    void setUp() throws Exception {
        cm = ConfigManager.getInstance();

        // Initialize headless Processing sketch
        pap = new PApplet();
        PApplet.runSketch(new String[]{"FieldVectorModeTest"}, pap);
        pap.noLoop();
        pap.width = 640;
        pap.height = 480;

        // Create ControlPanel without UI
        controlPanel = new ControlPanel(pap, null, true);
        // Create SimulationModel
        sim = new SimulationModel(pap, controlPanel);
        controlPanel.setSimulation(sim);
        controlPanel.addListener(sim);

        // Access private fieldVectors
        fieldVectorsField = SimulationModel.class.getDeclaredField("fieldVectors");
        fieldVectorsField.setAccessible(true);

        // Create mode under test
        mode = new FieldVectorMode(sim, controlPanel);
    }

    @Test
    void testUpdateDoesNothingWhenDisabled() throws Exception {
        // Ensure field vectors mode is off (default)
        mode.update();

        @SuppressWarnings("unchecked")
        List<?> fieldVectors = (List<?>) fieldVectorsField.get(sim);
        assertTrue(fieldVectors.isEmpty(), "No field vectors should be generated when mode is disabled");
    }

    @Test
    void testUpdateGeneratesFieldVectorsWhenEnabled() throws Exception {
        // Enable field vectors mode via reflection
        Field showFieldVectorsField = ControlPanel.class.getDeclaredField("showFieldVectors");
        showFieldVectorsField.setAccessible(true);
        showFieldVectorsField.setBoolean(controlPanel, true);
        assertTrue(controlPanel.showFieldVectorsMode(), "Field vectors mode should be enabled");

        // Add a point charge at (200,200)
        sim.addPointCharge(new PVector(200, 200), 1f);

        mode.update();

        @SuppressWarnings("unchecked")
        List<?> fieldVectors = (List<?>) fieldVectorsField.get(sim);
        Float grid = cm.getGridSize();

        // Recompute expected count, skipping any grid-point within chargeDiameter of a charge
        Integer expectedCount = 0;
        float radius = cm.getChargeDiameter();
        PVector chargePos = sim.getPointCharges().get(0).getPosition();
        for (Float y = 0F; y < pap.height; y += grid) {
            for (Float x = 0F; x < pap.width; x += grid) {
                if (PVector.dist(new PVector(x, y), chargePos) >= radius) {
                    expectedCount++;
                }
            }
        }

        assertEquals(expectedCount, fieldVectors.size(),
                "Field vectors count should equal number of grid cells outside the charge-diameter");
    }


    @Test
    void testDisplayDoesNotThrowWhenEnabled() throws Exception {
        // Enable and generate
        Field showFieldVectorsField = ControlPanel.class.getDeclaredField("showFieldVectors");
        showFieldVectorsField.setAccessible(true);
        showFieldVectorsField.setBoolean(controlPanel, true);
        sim.addPointCharge(new PVector(150, 150), 1f);
        mode.update();

        assertDoesNotThrow(() -> mode.display(pap));
    }

    @Test
    void testDisplayDoesNothingWhenDisabled() throws Exception {
        // Generate first
        Field showFieldVectorsField = ControlPanel.class.getDeclaredField("showFieldVectors");
        showFieldVectorsField.setAccessible(true);
        showFieldVectorsField.setBoolean(controlPanel, true);
        sim.addPointCharge(new PVector(250, 250), 1f);
        mode.update();
        @SuppressWarnings("unchecked")
        List<?> before = (List<?>) fieldVectorsField.get(sim);

        // Disable
        showFieldVectorsField.setBoolean(controlPanel, false);
        assertFalse(controlPanel.showFieldVectorsMode(), "Field vectors mode should be disabled");

        assertDoesNotThrow(() -> mode.display(pap));
        @SuppressWarnings("unchecked")
        List<?> after = (List<?>) fieldVectorsField.get(sim);
        assertEquals(before.size(), after.size(),
                "Display should not modify the field vectors list when disabled");
    }
}
