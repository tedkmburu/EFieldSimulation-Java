package org.example.engine.modes;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EquipotentialModeTest {

    static class TestableSim extends SimulationModel {
        boolean displayed = false;

        public TestableSim(PApplet pap, ControlPanel cp) {
            super(pap, cp);
        }

        @Override
        public void displayEquipotentialLines(PApplet app) {
            displayed = true;
        }
    }

    private PApplet pap;
    private TestableSim sim;
    private ControlPanel controlPanel;
    private EquipotentialMode mode;
    private Field equiLinesField;

    @BeforeEach
    void setUp() throws Exception {
        // Set up a headless Processing sketch
        pap = new PApplet();
        PApplet.runSketch(new String[]{"EquipotentialModeTest"}, pap);
        pap.noLoop();
        pap.width = 640;
        pap.height = 480;

        // Prepare ControlPanel (skip actual UI rendering)
        controlPanel = new ControlPanel(pap, null, true);
        sim = new TestableSim(pap, controlPanel);
        controlPanel.setSimulation(sim);
        controlPanel.addListener(sim);

        // Instantiate the mode under test
        mode = new EquipotentialMode(sim, controlPanel);

        // Reflectively access SimulationModel.equiLines to verify update() side-effects
        equiLinesField = SimulationModel.class.getDeclaredField("equiLines");
        equiLinesField.setAccessible(true);
    }

    @Test
    void testUpdateDoesNotModifyEquipotentialLines() throws Exception {
        // Initially, there should be no equipotential lines
        @SuppressWarnings("unchecked")
        List<?> before = (List<?>) equiLinesField.get(sim);
        assertTrue(before.isEmpty(), "Should start with no equipotential lines");

        // Call update (which is a no-op)
        mode.update();

        @SuppressWarnings("unchecked")
        List<?> after = (List<?>) equiLinesField.get(sim);
        assertEquals(before.size(), after.size(),
                "update() should not add or remove any equipotential lines");
    }

    @Test
    void testDisplayDoesNothingWhenDisabled() {
        // By default, equipotential mode is off
        assertFalse(controlPanel.showEquipotentialLinesMode(),
                "Equipotential lines mode should be off by default");

        mode.display(pap);
        assertFalse(sim.displayed,
                "displayEquipotentialLines should not be called when disabled");
    }

    @Test
    void testDisplayCallsDisplayWhenEnabled() throws Exception {
        // Enable equipotential mode via reflection on the private flag
        Field f = ControlPanel.class.getDeclaredField("showEquipotentialLines");
        f.setAccessible(true);
        f.setBoolean(controlPanel, true);

        assertTrue(controlPanel.showEquipotentialLinesMode(),
                "Equipotential lines mode should now be enabled");

        mode.display(pap);
        assertTrue(sim.displayed,
                "displayEquipotentialLines should be called when enabled");
    }
}
