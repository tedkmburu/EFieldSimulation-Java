package org.example.model;

import org.example.model.config.ConfigManager;
import processing.core.PVector;


import java.util.ArrayList;


public class CommonMath {

    // Calculate the net electric force at a given point
    public static PVector netForceAtPoint(PVector pos, ArrayList<PointCharge> pointCharges) {
        Float minDistance = 0.5f; // to prevent dividing by 0

        PVector netForce = createVector();
        for (PointCharge pointCharge : pointCharges) {
            PVector diff = PVector.sub(pointCharge.getPosition(), pos);
            Float r = diff.mag();
            if (r < minDistance) r = minDistance;
            Float forceMag = ConfigManager.getInstance().getCoulombsConstant() * pointCharge.getCharge() / (r * r);
            diff.normalize();
            diff.mult(forceMag);
            netForce.add(diff);
        }
        return netForce;
    }

    public static Float voltageAtPoint(PVector point, ArrayList<PointCharge> pointCharges) {
        Float voltage = 0.0f;
        for (PointCharge pointCharge : pointCharges) {
            Float r = PVector.dist(point, pointCharge.getPosition());
            if (r < 0.5f) { // avoid division by zero
                r = 0.5f;
            }
            // V = K * q / r
            voltage += (ConfigManager.getInstance().getCoulombsConstant() * pointCharge.getCharge() / r);
        }
        return voltage;
    }

    public static PVector createVector() {
        return new PVector(0, 0);
    }

    // method to check if a point is inside a PointCharge's circle.
    public static boolean mouseIsInsideCharge(PVector point, PointCharge charge) {
        return PVector.dist(point, charge.getPosition()) < ConfigManager.getInstance().getChargeRadius();
    }
}
