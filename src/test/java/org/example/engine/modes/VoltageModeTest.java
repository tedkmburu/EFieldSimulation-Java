package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import processing.core.PApplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class VoltageModeTest {

    private PApplet pap;
    private TestableSim sim;
    private ControlPanel controlPanel;
    private VoltageMode mode;

    // Stub to capture calls
    static class TestableSim extends SimulationModel {
        boolean updated = false;
        boolean displayed = false;

        public TestableSim(PApplet pap, ControlPanel cp) {
            super(pap, cp);
        }

        @Override
        public void updateVoltageGradient() {
            updated = true;
        }

        @Override
        public void displayVoltage(PApplet app) {
            displayed = true;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Initialize headless Processing
        pap = new PApplet();
        PApplet.runSketch(new String[]{"VoltageModeTest"}, pap);
        pap.noLoop();
        pap.width = 640;
        pap.height = 480;

        controlPanel = new ControlPanel(pap, null, true);
        sim = new TestableSim(pap, controlPanel);
        controlPanel.setSimulation(sim);
        controlPanel.addListener(sim);

        mode = new VoltageMode(sim, controlPanel);
    }

    @Test
    void testUpdateDoesNothingWhenDisabled() {
        sim.voltageDirty = true;
        // Default showVoltageMode() is false
        mode.update();
        assertFalse(sim.updated, "updateVoltageGradient should not be called when voltage mode is off");
    }

    @Test
    void testUpdateWhenEnabledAndDirty() throws Exception {
        // Enable voltage mode
        Field f = ControlPanel.class.getDeclaredField("showVoltage");
        f.setAccessible(true);
        f.setBoolean(controlPanel, true);

        sim.voltageDirty = true;
        mode.update();
        assertTrue(sim.updated, "updateVoltageGradient should be called when mode on and dirty");
    }

    @Test
    void testUpdateWhenEnabledButNotDirty() throws Exception {
        Field f = ControlPanel.class.getDeclaredField("showVoltage");
        f.setAccessible(true);
        f.setBoolean(controlPanel, true);

        sim.voltageDirty = false;
        mode.update();
        assertFalse(sim.updated, "updateVoltageGradient should not be called when not dirty");
    }

    @Test
    void testDisplayDoesNothingWhenDisabled() {
        mode.display(pap);
        assertFalse(sim.displayed, "displayVoltage should not be called when voltage mode is off");
    }

    @Test
    void testDisplayWhenEnabled() throws Exception {
        Field f = ControlPanel.class.getDeclaredField("showVoltage");
        f.setAccessible(true);
        f.setBoolean(controlPanel, true);

        mode.display(pap);
        assertTrue(sim.displayed, "displayVoltage should be called when voltage mode is on");
    }
}
