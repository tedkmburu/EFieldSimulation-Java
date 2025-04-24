package org.example.model;

import org.example.model.config.ConfigManager;
import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;

import static org.example.model.CommonMath.*;

public class FieldLine {
    private PApplet app;
    private ArrayList<PVector> points;
    private ArrayList<FieldLineArrow> fieldLineArrows;
    private PVector startingPoint;

    private final int maxLoops;
    private final float stepSize;
    private final int arrowInterval;

    public FieldLine(PApplet app, PVector startingPoint, ArrayList<PointCharge> pointCharges, FieldLineConfig config) {
        this.app = app;
        this.startingPoint = startingPoint;
        this.points = new ArrayList<>();
        this.points.add(this.startingPoint);
        this.fieldLineArrows = new ArrayList<>();

        this.maxLoops      = config.getMaxLoops();
        this.stepSize      = config.getStepSize();
        this.arrowInterval = config.getArrowInterval();

        generateFieldLineRecursive(pointCharges, 0);
        placeArrows(pointCharges);
    }

    private void generateFieldLineRecursive(ArrayList<PointCharge> pointCharges, int loopCount) {
        // 1) stop if we’ve done enough loops
        if (loopCount >= maxLoops) return;

        // 2) compute the next point
        PVector current = points.getLast();
        PVector next    = getNextFieldLinePoint(current, pointCharges, stepSize);

        // 3) check: did this segment land inside any charge?
        for (PointCharge pointCharge : pointCharges) {
            if (PVector.dist(next, pointCharge.getPosition()) < ConfigManager.getInstance().getChargeRadius() && pointCharge.getCharge() != 0.0f) {
                // snap to the center (or just add next) and bail out
                points.add(pointCharge.getPosition().copy());
                points.add(pointCharge.getPosition().copy());
                return;
            }
        }

        // 4) otherwise, keep going
        points.add(next);
        generateFieldLineRecursive(pointCharges, loopCount + 1);
    }

    private void placeArrows(ArrayList<PointCharge> pointCharges) {
        for (int i = arrowInterval; i < points.size(); i += arrowInterval) {
            PVector pos = points.get(i);
            float dir   = computeDirectionAt(pos, pointCharges);
            FieldLineArrow arrow = new FieldLineArrow(pos, dir);
            fieldLineArrows.add(arrow);
        }
    }

    private float computeDirectionAt(PVector pos, ArrayList<PointCharge> pointCharges) {
        PVector fieldVec = netForceAtPoint(pos, pointCharges).mult(-1);
        return fieldVec.heading();
    }

    public PVector getNextFieldLinePoint(PVector currentPoint, ArrayList<PointCharge> pointCharges, float stepSize) {
        // Calculate the net force, scale it, and add it to the current point.
        PVector netForceAtCurrentPoint = netForceAtPoint(currentPoint, pointCharges);

        // if the segment is outside the screen, make it bigger to save computation
        if (currentPoint.x < (-3 * ConfigManager.getInstance().getChargeRadius()) ||
            currentPoint.y < (-3 * ConfigManager.getInstance().getChargeRadius()) ||
            currentPoint.x > (app.width + (-3 * ConfigManager.getInstance().getChargeRadius())) ||
            currentPoint.y > (app.height + (-3 * ConfigManager.getInstance().getChargeRadius()))) {
            netForceAtCurrentPoint.setMag(-3 * ConfigManager.getInstance().getChargeRadius());
        }
        else {
            netForceAtCurrentPoint.setMag(-stepSize);
        }
        return currentPoint.copy().add(netForceAtCurrentPoint);
    }

    // Draw the field line as a curve
    public void display(PApplet app) {
        app.pushMatrix();
        app.noFill();
        app.stroke(255);
        app.strokeWeight(3);
        app.beginShape();
        // Use the first point with vertex(), then curveVertex() for smooth curves.
        if (!points.isEmpty()) {
            app.vertex(points.getFirst().x, points.getFirst().y);
            for (PVector p : points) {
                app.curveVertex(p.x, p.y);
            }
        }
        app.endShape();
        app.popMatrix();

        // display the field line arrows
        for (FieldLineArrow fieldLineArrow : fieldLineArrows) {
            fieldLineArrow.display(app);
        }
    }

    public ArrayList<PVector> getPoints() {
        return points;
    }
}
