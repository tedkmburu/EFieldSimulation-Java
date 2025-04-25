package org.example.factories;

import org.example.model.PointCharge;
import org.example.model.TestCharge;
import processing.core.PVector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChargeFactoryTest {

    private final ChargeFactory factory = new ProcessingChargeFactory();

    @Test
    void testCreatePointCharge() {
        PVector original = new PVector(1.5f, 2.5f);
        float chargeVal = 4.2f;

        PointCharge pc = factory.createPointCharge(original, chargeVal);
        assertNotNull(pc, "createPointCharge should not return null");
        assertEquals(chargeVal, pc.getCharge(), 1e-6f, "PointCharge should have correct charge");
        assertNotSame(original, pc.getPosition(), "Position vector should be a copy, not the same instance");
        assertEquals(original.x, pc.getPosition().x, 1e-6f, "PointCharge x-position should match input");
        assertEquals(original.y, pc.getPosition().y, 1e-6f, "PointCharge y-position should match input");

        // Mutate original and verify independence
        original.set(9.9f, 8.8f);
        assertNotEquals(original.x, pc.getPosition().x, "PointCharge position should not change when original is mutated");
    }

    @Test
    void testCreateTestCharge() {
        PVector original = new PVector(-3.0f, 0.5f);
        float chargeVal = -1.2f;

        TestCharge tc = factory.createTestCharge(original, chargeVal);
        assertNotNull(tc, "createTestCharge should not return null");
        assertEquals(chargeVal, tc.getCharge(), 1e-6f, "TestCharge should have correct charge");
        assertNotSame(original, tc.getPosition(), "Position vector should be a copy, not the same instance");
        assertEquals(original.x, tc.getPosition().x, 1e-6f, "TestCharge x-position should match input");
        assertEquals(original.y, tc.getPosition().y, 1e-6f, "TestCharge y-position should match input");

        // Mutate original and verify independence
        original.set(7.7f, -4.4f);
        assertNotEquals(original.y, tc.getPosition().y, "TestCharge position should not change when original is mutated");
    }
}
