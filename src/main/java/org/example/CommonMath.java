package org.example;

import org.example.model.Charge;
import org.example.model.PointCharge;
import processing.core.PVector;

import static org.example.Simulation.*;

import java.util.ArrayList;
import java.util.List;

import static org.example.Constants.COULOMBS_CONSTANT;

public class CommonMath {

    // Calculate the net electric force at a given point
    public static PVector netForceAtPoint(PVector pos, ArrayList<PointCharge> pointCharges) {
        float minDistance = 0.5f; // to prevent dividing by 0

        PVector netForce = createVector();
        for (PointCharge pointCharge : pointCharges) {
            PVector diff = PVector.sub(pointCharge.getPosition(), pos);
            float r = diff.mag();
            if (r < minDistance) r = minDistance;
            float forceMag = COULOMBS_CONSTANT * pointCharge.getCharge() / (r * r);
            diff.normalize();
            diff.mult(forceMag);
            netForce.add(diff);
        }
        return netForce;
    }

    // method to check if a point is inside a PointCharge's circle.
    public static boolean mouseIsInsideCharge(PVector point, PointCharge charge) {
        return PVector.dist(point, charge.getPosition()) < Constants.CHARGE_RADIUS;
    }

    public static float voltageAtPoint(PVector point, ArrayList<PointCharge> pointCharges) {

        float voltage = 0;
        for (PointCharge pointCharge : pointCharges) {
            float r = PVector.dist(point, pointCharge.getPosition());
            if (r < 0.5f) { // avoid division by zero
                r = 0.5f;
            }
            // Voltage: V = K * q / r (adjust units as needed)
            voltage += COULOMBS_CONSTANT * pointCharge.getCharge() / r;
        }
        return voltage;
    }

    public static PVector createVector() {
        return new PVector(0, 0);
    }
}
