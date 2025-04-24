package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.example.model.CommonMath;
import org.example.model.PointCharge;
import org.junit.jupiter.api.Test;

import processing.core.PVector;

public class CommonMathTest {

    // Net Force Calculation:
    // For a point charge at (200,200) with charge +5, evaluate netForceAtPoint at (300,200)
    // Expected: diff = (200,200)-(300,200) = (-100,0), r=100, forceMag = COULOMBS_CONSTANT*5/100^2 = 4495 (approx)
    // Therefore, expected force vector is approximately (-4495, 0).
    @Test
    public void testNetForceCalculation() {
        PointCharge charge = new PointCharge(new PVector(200, 200), 5);
        ArrayList<PointCharge> charges = new ArrayList<>();
        charges.add(charge);

        PVector testPoint = new PVector(300, 200);
        PVector netForce = CommonMath.netForceAtPoint(testPoint, charges);

        float expectedForceX = -4495.0f; // approximate value
        float expectedForceY = 0.0f;
        float tolerance = 1e-1f;

        assertEquals(expectedForceX, netForce.x, tolerance, "Net force x-component should be approximately -4495");
        assertEquals(expectedForceY, netForce.y, tolerance, "Net force y-component should be approximately 0");
    }

    // Inside Charge Check:
    // For a charge at (200,200) with CHARGE_RADIUS = CHARGE_DIAMETER/2 = 20,
    // a point (210,200) (distance 10) should be inside.
    @Test
    public void testMouseIsInsideCharge() {
        PointCharge charge = new PointCharge(new PVector(200, 200), 5);
        PVector insidePoint = new PVector(210, 200);
        assertTrue(CommonMath.mouseIsInsideCharge(insidePoint, charge), "Point should be inside the charge's radius.");
    }

    // Voltage Calculation:
    // For a charge at (200,200) with charge +5, evaluating voltageAtPoint at (300,200) where r = 100:
    // Expected voltage = COULOMBS_CONSTANT*5/100. With COULOMBS_CONSTANT=8990000, expected ≈ 449500.
    @Test
    public void testVoltageCalculation() {
        PointCharge charge = new PointCharge(new PVector(200, 200), 5);
        ArrayList<PointCharge> charges = new ArrayList<>();
        charges.add(charge);

        PVector testPoint = new PVector(300, 200);
        float voltage = CommonMath.voltageAtPoint(testPoint, charges);

        float expectedVoltage = 449500.0f; // approximate value
        float tolerance = 1e-1f;
        assertEquals(expectedVoltage, voltage, tolerance, "Voltage should be approximately 449500");
    }

    // Vector Creation:
    // Assert that createVector() returns a PVector with x == 0 and y == 0.
    @Test
    public void testCreateVector() {
        PVector vec = CommonMath.createVector();
        assertEquals(0.0f, vec.x, 1e-6, "Vector x should be 0");
        assertEquals(0.0f, vec.y, 1e-6, "Vector y should be 0");
    }
}
