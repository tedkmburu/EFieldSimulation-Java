package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.example.model.CommonMath;
import org.example.model.PointCharge;
import org.junit.jupiter.api.Test;

import processing.core.PVector;

public class CommonMathTest {

    /**
     * Net Force Calculation:
     * For a point charge at (200,200) with charge +5, evaluating netForceAtPoint at (300,200):
     *   diff = (200,200) - (300,200) = (-100, 0)
     *   r = 100
     *   forceMag = K * q / r^2 = 8990000 * 5 / (100*100) ≈ 4495
     * Expect vector ≈ (-4495, 0).
     */
    @Test
    public void testNetForceCalculation() {
        PointCharge charge = new PointCharge(new PVector(200, 200), 5);
        ArrayList<PointCharge> charges = new ArrayList<>();
        charges.add(charge);

        PVector testPoint = new PVector(300, 200);
        PVector netForce = CommonMath.netForceAtPoint(testPoint, charges);

        float expectedX = -4495.0f;
        float expectedY = 0.0f;
        float tol       = 1e-1f;

        assertEquals(expectedX, netForce.x, tol,
                "Net force x-component should be approximately -4495");
        assertEquals(expectedY, netForce.y, tol,
                "Net force y-component should be approximately 0");
    }

    /**
     * Inside-Charge Check:
     * A point 10px away from a charge at (200,200) should lie within its radius (20px).
     */
    @Test
    public void testMouseIsInsideCharge() {
        PointCharge charge = new PointCharge(new PVector(200, 200), 5);
        PVector inside = new PVector(210, 200);
        assertTrue(CommonMath.mouseIsInsideCharge(inside, charge),
                "Point should be inside the charge's radius.");
    }

    /**
     * Voltage Calculation:
     * For same single charge, V = K * q / r = 8990000 * 5 / 100 ≈ 449500.
     */
    @Test
    public void testVoltageCalculation() {
        PointCharge charge = new PointCharge(new PVector(200, 200), 5);
        ArrayList<PointCharge> charges = new ArrayList<>();
        charges.add(charge);

        PVector testPoint = new PVector(300, 200);
        float voltage = CommonMath.voltageAtPoint(testPoint, charges);

        float expectedV = 449500.0f;
        float tol       = 1e-1f;
        assertEquals(expectedV, voltage, tol,
                "Voltage should be approximately 449500");
    }

    /**
     * Vector Creation:
     * createVector() must return (0,0).
     */
    @Test
    public void testCreateVector() {
        PVector vec = CommonMath.createVector();
        assertEquals(0.0f, vec.x, 1e-6, "Vector x should be 0");
        assertEquals(0.0f, vec.y, 1e-6, "Vector y should be 0");
    }
}
