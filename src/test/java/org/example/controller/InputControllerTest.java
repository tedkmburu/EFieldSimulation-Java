package org.example.controller;

import org.example.model.SimulationModel;
import org.example.model.PointCharge;
import org.example.model.TestCharge;
import org.example.model.config.ConfigManager;
import org.example.controller.InputController;
import org.example.view.ui.ControlPanel;
import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InputControllerTest {

    private PApplet pap;
    private SimulationModel sim;
    private ControlPanel controlPanel;
    private InputController inputController;
    private ConfigManager cm;

    @BeforeEach
    void setUp() throws Exception {
        cm = ConfigManager.getInstance();

        // Initialize headless Processing sketch
        pap = new PApplet();
        PApplet.runSketch(new String[]{"InputControllerTest"}, pap);
        pap.noLoop();
        pap.width = 640;
        pap.height = 480;

        // Create ControlPanel without UI
        controlPanel = new ControlPanel(pap, null, true);
        // Create SimulationModel with panel
        sim = new SimulationModel(pap, controlPanel);
        controlPanel.setSimulation(sim);
        controlPanel.addListener(sim);

        // Create InputController
        inputController = new InputController(pap, sim, controlPanel);
    }

    @Test
    void testHandleMouseDragged_StartsDraggingAndMovesCharge() {
        // Add a point charge at (100,100)
        PVector initial = new PVector(100, 100);
        sim.addPointCharge(initial);

        // Set mouse position inside charge radius
        pap.mouseX = 105;
        pap.mouseY = 102;
        // Update simulation mousePosition
        sim.update();

        // Call drag handler
        inputController.handleMouseDragged();

        PointCharge pc = sim.getPointCharges().get(0);
        assertTrue(pc.dragging, "PointCharge should be marked dragging");
        assertEquals(105f, pc.getPosition().x, 1e-6f, "X position should update to mouseX");
        assertEquals(102f, pc.getPosition().y, 1e-6f, "Y position should update to mouseY");
    }

    @Test
    void testHandleKeyPressed_IncreaseDecreaseAndDelete() {
        PVector pos = new PVector(150, 200);
        sim.addPointCharge(pos);
        PointCharge pc = sim.getPointCharges().get(0);

        // Ensure selected for charge change
        pc.selected = true;

        // Increase charge
        pap.keyCode = PConstants.RIGHT;
        inputController.handleKeyPressed();
        assertEquals(cm.getChargeIncrementDelta(), pc.getCharge(), 1e-6f, "Charge should increase by delta");

        // Decrease charge
        pap.keyCode = PConstants.LEFT;
        inputController.handleKeyPressed();
        assertEquals(0f, pc.getCharge(), 1e-6f, "Charge should decrease back to zero");

        // Delete charge
        pap.keyCode = PConstants.DELETE;
        inputController.handleKeyPressed();
        assertTrue(sim.getPointCharges().isEmpty(), "PointCharges list should be empty after delete key");
    }

    @Test
    void testHandleMouseClicked_AddsPointCharge_WhenNotInTestChargeMode() {
        assertTrue(sim.getPointCharges().isEmpty());

        pap.mouseX = 50;
        pap.mouseY = 60;
        sim.update();

        inputController.handleMouseClicked();

        List<PointCharge> pcs = sim.getPointCharges();
        assertEquals(1, pcs.size(), "Should add one PointCharge");
        PointCharge pc = pcs.get(0);
        assertEquals(50f, pc.getPosition().x, 1e-6f);
        assertEquals(60f, pc.getPosition().y, 1e-6f);
    }

    @Test
    void testHandleMouseClicked_AddsTestCharge_WhenInTestChargeMode() throws Exception {
        // Enable testChargeMode via reflection
        Field f = ControlPanel.class.getDeclaredField("testChargeMode");
        f.setAccessible(true);
        f.set(controlPanel, true);

        assertTrue(controlPanel.testChargeMode(), "TestChargeMode should be enabled");
        assertTrue(sim.getTestCharges().isEmpty());

        pap.mouseX = 70;
        pap.mouseY = 80;
        sim.update();

        inputController.handleMouseClicked();

        List<TestCharge> tcs = sim.getTestCharges();
        assertEquals(1, tcs.size(), "Should add one TestCharge");
        TestCharge tc = tcs.get(0);
        assertEquals(70f, tc.getPosition().x, 1e-6f);
        assertEquals(80f, tc.getPosition().y, 1e-6f);
    }
}
