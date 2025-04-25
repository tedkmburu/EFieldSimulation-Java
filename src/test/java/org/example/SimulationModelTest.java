package org.example;

import org.example.model.SimulationModel;
import org.example.model.PointCharge;
import org.example.model.TestCharge;
import org.example.model.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationModelTest {

    private SimulationModel sim;
    private PApplet pap;
    private ConfigManager cm;

    @BeforeEach
    void setUp() {
        cm = ConfigManager.getInstance();

        // Initialize a headless Processing sketch so that createGraphics() won't NPE
        pap = new PApplet();
        PApplet.runSketch(new String[]{"SimulationModelTest"}, pap);
        pap.noLoop();
        pap.width = 640;
        pap.height = 480;

        sim = new SimulationModel(pap, null);
    }

    @Test
    void testGetWidthHeight() {
        assertEquals(pap.width, sim.getWidth(),
                "SimulationModel width should match the PApplet width");
        assertEquals(pap.height, sim.getHeight(),
                "SimulationModel height should match the PApplet height");
    }

    @Test
    void testAddAndRemovePointCharges() {
        assertTrue(sim.getPointCharges().isEmpty(),
                "Initially, there should be no point charges");

        PVector pos = new PVector(100, 200);
        sim.addPointCharge(pos);

        List<PointCharge> pcs = sim.getPointCharges();
        assertEquals(1, pcs.size(),
                "After adding a point charge, size should be 1");

        PointCharge pc = pcs.get(0);
        assertEquals(pos.x, pc.getPosition().x, 1e-6f,
                "PointCharge x-position should match input");
        assertEquals(pos.y, pc.getPosition().y, 1e-6f,
                "PointCharge y-position should match input");
        assertTrue(pc.selected,
                "Newly added PointCharge should be selected by default");

        sim.removeAllPointCharges();
        assertTrue(sim.getPointCharges().isEmpty(),
                "After removeAllPointCharges, list should be empty");
    }

    @Test
    void testResetChargeStates() {
        PVector pos = new PVector(150, 150);
        sim.addPointCharge(pos);

        PointCharge pc = sim.getPointCharges().get(0);
        // simulate prior state
        pc.selected = true;
        pc.dragging = true;

        sim.resetChargeStates();

        assertFalse(pc.selected,
                "After resetChargeStates, selected should be false");
        assertFalse(pc.dragging,
                "After resetChargeStates, dragging should be false");
    }

    @Test
    void testSelectChargeAtPosition() {
        PVector pos = new PVector(200, 200);
        sim.addPointCharge(pos);
        // clear the default selection from addPointCharge
        sim.resetChargeStates();

        boolean found = sim.selectChargeAtPosition(pos);
        assertTrue(found,
                "selectChargeAtPosition at the charge location should return true");
        assertTrue(sim.getPointCharges().get(0).selected,
                "Charge should be marked selected after selectChargeAtPosition");

        boolean notFound = sim.selectChargeAtPosition(new PVector(0, 0));
        assertFalse(notFound,
                "selectChargeAtPosition away from any charge should return false");
    }

    @Test
    void testAddAndClearTestCharges() {
        assertTrue(sim.getTestCharges().isEmpty(),
                "Initially, there should be no test charges");

        PVector pos = new PVector(50, 50);
        sim.addTestCharge(pos);

        List<TestCharge> tcs = sim.getTestCharges();
        assertEquals(1, tcs.size(),
                "After addTestCharge, size should be 1");

        TestCharge tc = tcs.get(0);
        assertEquals(pos.x, tc.getPosition().x, 1e-6f,
                "TestCharge x-position should match input");
        assertEquals(pos.y, tc.getPosition().y, 1e-6f,
                "TestCharge y-position should match input");
        assertEquals(cm.getTestChargeCharge(), tc.getCharge(), 1e-9f,
                "TestCharge charge should match default from ConfigManager");

        sim.clearTestCharges();
        assertTrue(sim.getTestCharges().isEmpty(),
                "After clearTestCharges, list should be empty");
    }

    @Test
    void testMoveTestCharges() {
        PVector pos = new PVector(300, 300);
        sim.addTestCharge(pos);

        TestCharge tc = sim.getTestCharges().getFirst();
        PVector original = tc.getPosition().copy();

        // No point charges => net force zero => position unchanged
        sim.moveTestCharges();

        PVector moved = tc.getPosition();
        assertEquals(original.x, moved.x, 1e-6f,
                "Without point charges, TestCharge x should not change");
        assertEquals(original.y, moved.y, 1e-6f,
                "Without point charges, TestCharge y should not change");
    }
}
