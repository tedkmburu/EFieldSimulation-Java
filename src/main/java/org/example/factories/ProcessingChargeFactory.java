package org.example.factories;

import org.example.model.PointCharge;
import org.example.model.TestCharge;
import processing.core.PVector;

public class ProcessingChargeFactory implements ChargeFactory {
    @Override
    public PointCharge createPointCharge(PVector position, float charge) {
        return new PointCharge(position.copy(), charge);
    }

    @Override
    public TestCharge createTestCharge(PVector position, float charge) {
        return new TestCharge(position.copy(), charge);
    }
}