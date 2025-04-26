package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import org.example.factories.ProcessingFieldElementFactory;
import org.example.model.config.ConfigManager;
import processing.core.PApplet;
import processing.core.PVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FieldLineModeTest {

    private PApplet pap;
    private SimulationModel sim;
    private ControlPanel controlPanel;
    private FieldLineMode mode;
    private Field fieldLinesField;
    private ConfigManager cm;

    @BeforeEach
    void setUp() throws Exception {
        cm = ConfigManager.getInstance();

        // Initialize headless Processing sketch
        pap = new PApplet();
        PApplet.runSketch(new String[]{"FieldLineModeTest"}, pap);
        pap.noLoop();
        pap.width = 640;
        pap.height = 480;

        // Create ControlPanel without UI
        controlPanel = new ControlPanel(pap, null, true);
        // Create SimulationModel
        sim = new SimulationModel(pap, controlPanel);
        controlPanel.setSimulation(sim);
        controlPanel.addListener(sim);

        // Access private fieldLines
        fieldLinesField = SimulationModel.class.getDeclaredField("fieldLines");
        fieldLinesField.setAccessible(true);

        // Create mode under test
        mode = new FieldLineMode(sim, controlPanel, new ProcessingFieldElementFactory());
    }

    @Test
    void testUpdateDoesNothingWhenDisabled() throws Exception {
        // Ensure field lines mode is off
        controlPanel.toggleFieldLines(false);

        // Add a point charge (would generate lines if enabled)
        sim.addPointCharge(new PVector(100, 100), 1f);

        // Call update
        mode.update();

        // Inspect fieldLines list
        List<?> fieldLines = (List<?>) fieldLinesField.get(sim);
        assertTrue(fieldLines.isEmpty(), "No field lines should be generated when mode is disabled");
    }

    @Test
    void testUpdateGeneratesFieldLinesWhenEnabled() throws Exception {
        // Ensure field lines mode is on
        controlPanel.toggleFieldLines(true);

        // Add a positive point charge of magnitude 1
        sim.addPointCharge(new PVector(200, 200), 1f);

        // Call update
        mode.update();

        // Inspect generated field lines
        List<?> fieldLines = (List<?>) fieldLinesField.get(sim);
        Integer expected = cm.getFieldLinesPerCoulomb() * 1;
        assertEquals(expected, fieldLines.size(),
                "Field lines count should equal fieldLinesPerCoulomb × charge magnitude");
    }

    @Test
    void testDisplayDoesNotThrowWhenEnabled() {
        // After generating field lines
        controlPanel.toggleFieldLines(true);
        sim.addPointCharge(new PVector(150, 150), 1f);
        mode.update();

        // Calling display should not throw
        assertDoesNotThrow(() -> mode.display(pap));
    }

    @Test
    void testDisplayDoesNothingWhenDisabled() throws Exception {
        // Generate some field lines first
        controlPanel.toggleFieldLines(true);
        sim.addPointCharge(new PVector(250, 250), 1f);
        mode.update();
        List<?> before = (List<?>) fieldLinesField.get(sim);

        // Now disable
        controlPanel.toggleFieldLines(false);
        // Calling display should not throw or modify lines
        assertDoesNotThrow(() -> mode.display(pap));
        List<?> after = (List<?>) fieldLinesField.get(sim);
        assertEquals(before.size(), after.size(),
                "Display should not modify the field lines list when disabled");
    }
}
