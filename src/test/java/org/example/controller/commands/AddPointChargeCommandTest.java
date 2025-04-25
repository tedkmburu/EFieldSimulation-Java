package org.example.controller.commands;

import org.example.model.SimulationModel;
import processing.core.PApplet;
import processing.core.PVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddPointChargeCommandTest {

    private PApplet pap;
    private SimulationModel sim;

    @BeforeEach
    void setUp() {
        // Initialize headless Processing sketch
        pap = new PApplet();
        PApplet.runSketch(new String[]{"AddPointChargeCommandTest"}, pap);
        pap.noLoop();
        pap.width = 400;
        pap.height = 300;

        // Create SimulationModel without UI
        sim = new SimulationModel(pap, null);
        // Ensure starting state
        sim.removeAllPointCharges();
    }

    @Test
    void testExecuteAddsPointCharge() {
        assertTrue(sim.getPointCharges().isEmpty(), "Initially, no point charges");

        PVector pos = new PVector(10f, 20f);
        AddPointChargeCommand cmd = new AddPointChargeCommand(sim, pos);
        cmd.execute();

        assertEquals(1, sim.getPointCharges().size(), "After execute, one point charge should be added");
        var pc = sim.getPointCharges().get(0);
        assertEquals(10f, pc.getPosition().x, 1e-6f, "X position should match the command position");
        assertEquals(20f, pc.getPosition().y, 1e-6f, "Y position should match the command position");
        assertTrue(pc.selected, "New point charge should be selected by default");
    }

    @Test
    void testUndoRemovesLastAdded() {
        PVector pos1 = new PVector(1f, 2f);
        PVector pos2 = new PVector(3f, 4f);

        AddPointChargeCommand cmd1 = new AddPointChargeCommand(sim, pos1);
        AddPointChargeCommand cmd2 = new AddPointChargeCommand(sim, pos2);

        cmd1.execute();
        cmd2.execute();
        assertEquals(2, sim.getPointCharges().size(), "Two charges after two executes");

        cmd2.undo();
        assertEquals(1, sim.getPointCharges().size(), "Undo should remove the last added charge");
        var remaining = sim.getPointCharges().get(0);
        assertEquals(1f, remaining.getPosition().x, 1e-6f, "Remaining charge should be the first one");
        assertEquals(2f, remaining.getPosition().y, 1e-6f, "Remaining charge should be the first one");

        cmd1.undo();
        assertTrue(sim.getPointCharges().isEmpty(), "All charges should be removed after undoing both");
    }

    @Test
    void testConstructorCopiesPosition() {
        PVector original = new PVector(5f, 6f);
        AddPointChargeCommand cmd = new AddPointChargeCommand(sim, original);

        // Mutate original
        original.set(50f, 60f);

        cmd.execute();
        var pc = sim.getPointCharges().get(0);
        assertEquals(5f, pc.getPosition().x, 1e-6f, "X should match original before mutation");
        assertEquals(6f, pc.getPosition().y, 1e-6f, "Y should match original before mutation");
    }
}
