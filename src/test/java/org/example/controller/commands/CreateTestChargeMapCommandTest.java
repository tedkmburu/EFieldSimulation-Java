package org.example.controller.commands;

import org.example.model.SimulationModel;
import org.example.view.ui.ControlPanel;
import org.example.model.config.ConfigManager;
import org.example.model.PresetConfigurator;
import processing.core.PApplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateTestChargeMapCommandTest {

    private PApplet pap;
    private SimulationModel sim;
    private ControlPanel controlPanel;
    private CreateTestChargeMapCommand cmd;
    private ConfigManager cm;

    @BeforeEach
    void setUp() {
        cm = ConfigManager.getInstance();
        pap = new PApplet();
        PApplet.runSketch(new String[]{"CreateTestChargeMapCommandTest"}, pap);
        pap.noLoop();
        pap.width = 640;
        pap.height = 480;

        controlPanel = new ControlPanel(pap, null, true);
        controlPanel.setSimulation(sim);

        sim = new SimulationModel(pap, controlPanel);
        // start with no test charges
        sim.clearTestCharges();

        cmd = new CreateTestChargeMapCommand(sim, controlPanel);
    }

    @Test
    void testExecuteGeneratesCorrectNumberOfTestCharges() {
        cmd.execute();
        List<?> tcs = sim.getTestCharges();

        Float grid = cm.getGridSize();
        Integer cols = (int) ((pap.width - grid) / grid);
        Integer rows = (int) (pap.height / grid);
        Integer step = PresetConfigurator.TEST_CHARGE_MAP_STEP;

        Integer countX = 0;
        for (Integer x = 0; x < cols; x += step) countX++;
        Integer countY = 0;
        for (Integer y = 0; y < rows; y += step) countY++;
        Integer expectedTotal = countX * countY;

        assertEquals(expectedTotal, tcs.size(),
                "execute() should generate the expected number of test charges");
    }

    @Test
    void testUndoClearsTestCharges() {
        // populate test charges
        cmd.execute();
        assertFalse(sim.getTestCharges().isEmpty(), "After execute, test charges should exist");

        cmd.undo();
        assertTrue(sim.getTestCharges().isEmpty(),
                "undo() should clear all test charges");
    }

    @Test
    void testPrevModeDoesNotAlterControlPanelFlag() {
        // initial flag
        Boolean initial = controlPanel.testChargeMode();
        cmd.execute();
        assertEquals(initial, controlPanel.testChargeMode(),
                "execute() should not change controlPanel.testChargeMode flag");

        cmd.undo();
        assertEquals(initial, controlPanel.testChargeMode(),
                "undo() should not change controlPanel.testChargeMode flag");
    }
}