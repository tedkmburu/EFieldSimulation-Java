package org.example.controller.commands;

import org.example.view.ui.ControlPanel;
import org.example.view.ui.ControlPanelListener;
import processing.core.PApplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToggleFieldLinesCommandTest {

    private PApplet pap;
    private ControlPanel panel;
    private ToggleFieldLinesCommand cmd;
    private TestListener listener;

    static class TestListener implements ControlPanelListener {
        Boolean lastValue;

        @Override public void onFieldLinesToggled(boolean on) { lastValue = on; }
        @Override public void onFieldVectorsToggled(boolean on) {}
        @Override public void onEquipotentialToggled(boolean on) {}
        @Override public void onVoltageToggled(boolean on) {}
        @Override public void onGridToggled(boolean on) {}
        @Override public void onSnapToGridToggled(boolean on) {}
        @Override public void onTestChargeModeToggled(boolean on) {}
        @Override public void onSinglePreset() {}
        @Override public void onDipolePreset() {}
        @Override public void onRowPreset() {}
        @Override public void onDipoleRowPreset() {}
        @Override public void onRandomPreset() {}
        @Override public void onRemoveAllCharges() {}
        @Override public void onCreateTestChargeMap() {}
        @Override public void onClearTestCharges() {}
    }

    @BeforeEach
    void setUp() {
        // Initialize a headless Processing sketch
        pap = new PApplet();
        PApplet.runSketch(new String[]{"ToggleFieldLinesCommandTest"}, pap);
        pap.noLoop();

        // ControlPanel in skip-UI mode
        panel = new ControlPanel(pap, null, true);
        listener = new TestListener();
        panel.addListener(listener);

        cmd = new ToggleFieldLinesCommand(null, panel);
    }

    @Test
    void testExecuteTogglesOff() {
        // Default is on
        assertTrue(panel.showFieldLinesMode(), "Field lines should be on by default");
        assertNull(listener.lastValue, "Listener should not have been called yet");

        cmd.execute();

        assertFalse(panel.showFieldLinesMode(), "Field lines should be toggled off");
        assertEquals(Boolean.FALSE, listener.lastValue, "Listener should receive false");
    }

    @Test
    void testUndoRestoresOn() {
        // First toggle off
        cmd.execute();
        listener.lastValue = null;

        // Now undo
        cmd.undo();

        assertTrue(panel.showFieldLinesMode(), "Field lines should be restored on");
        assertEquals(Boolean.TRUE, listener.lastValue, "Listener should receive true");
    }
}
