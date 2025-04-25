package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import processing.core.PApplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class TestChargeModeTest {

    private PApplet pap;
    private TestableSim sim;
    private ControlPanel controlPanel;
    private TestChargeMode mode;

    // A stub SimulationModel to track method calls
    static class TestableSim extends SimulationModel {
        boolean moved = false;
        boolean displayed = false;

        public TestableSim(PApplet pap, ControlPanel cp) {
            super(pap, cp);
        }

        @Override
        public void moveTestCharges() {
            moved = true;
        }

        @Override
        public void displayTestCharges(PApplet app) {
            displayed = true;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Initialize headless Processing
        pap = new PApplet();
        PApplet.runSketch(new String[]{"TestChargeModeTest"}, pap);
        pap.noLoop();
        pap.width = 500;
        pap.height = 400;

        // Setup control panel and model stub
        controlPanel = new ControlPanel(pap, null, true);
        sim = new TestableSim(pap, controlPanel);
        controlPanel.setSimulation(sim);
        controlPanel.addListener(sim);

        mode = new TestChargeMode(sim, controlPanel);
    }

    @Test
    void testUpdateDoesNothingWhenDisabled() {
        sim.moved = false;
        assertFalse(controlPanel.testChargeMode(), "Default should be disabled");

        mode.update();
        assertFalse(sim.moved, "moveTestCharges should not be called when disabled");
    }

    @Test
    void testUpdateCallsMoveWhenEnabled() throws Exception {
        // Enable test-charge mode via reflection
        Field f = ControlPanel.class.getDeclaredField("testChargeMode");
        f.setAccessible(true);
        f.setBoolean(controlPanel, true);

        sim.moved = false;
        assertTrue(controlPanel.testChargeMode());

        mode.update();
        assertTrue(sim.moved, "moveTestCharges should be called when enabled");
    }

    @Test
    void testDisplayDoesNothingWhenDisabled() {
        sim.displayed = false;
        assertFalse(controlPanel.testChargeMode());

        mode.display(pap);
        assertFalse(sim.displayed, "displayTestCharges should not be called when disabled");
    }

    @Test
    void testDisplayCallsDisplayWhenEnabled() throws Exception {
        // Enable test-charge mode via reflection
        Field f = ControlPanel.class.getDeclaredField("testChargeMode");
        f.setAccessible(true);
        f.setBoolean(controlPanel, true);

        sim.displayed = false;
        assertTrue(controlPanel.testChargeMode());

        mode.display(pap);
        assertTrue(sim.displayed, "displayTestCharges should be called when enabled");
    }
}
