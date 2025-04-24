package org.example.factories;

import org.example.model.PointCharge;
import org.example.model.TestCharge;
import processing.core.PVector;

public interface ChargeFactory {
    PointCharge createPointCharge(PVector position, float charge);
    TestCharge  createTestCharge(PVector position, float charge);
}