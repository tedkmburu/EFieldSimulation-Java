package org.example;

import org.example.model.*;
import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PConstants;
import org.example.CommonMath.*;

import java.util.ArrayList;

import static org.example.CommonMath.*;
import static org.example.Constants.*;

public class Simulation {
    private PApplet parent;

    private ArrayList<PointCharge> pointCharges;
    private ArrayList<FieldLine> fieldLines;
    private ArrayList<EquiLine> equiLines;
    private ArrayList<TestCharge> testCharges;
    private ArrayList<FieldVector> fieldVectors;
    private VoltageGradient voltageGradient;

    private ControlPanel controlPanel;

    private PVector mousePosition;

    public Simulation(PApplet parent, ControlPanel controlPanel) {
        this.parent = parent;
        this.controlPanel = controlPanel;

        this.pointCharges = new ArrayList<>();
        pointCharges.add(new PointCharge(new PVector(200, 200), 5));
        pointCharges.add(new PointCharge(new PVector(600, 200), -5));

        this.equiLines = new ArrayList<>();
        this.fieldLines = new ArrayList<>();
        this.fieldVectors = new ArrayList<>();
        this.testCharges = new ArrayList<>();
        this.voltageGradient = new VoltageGradient(this);
    }

    public void update() {
        mousePosition = new PVector(parent.mouseX, parent.mouseY);
        if (controlPanel.showFieldVectorsMode()) { createFieldVectors(); }

        if (controlPanel.testChargeMode()) {
            for (TestCharge testCharge : testCharges) {
                testCharge.move(pointCharges);
            }
        }

        if (controlPanel.showFieldLinesMode()) { createFieldLines(); }

        if (controlPanel.showVoltageMode()) {
            this.voltageGradient.updateVoltageGradient(parent, pointCharges);
        }
    }

    public void display() {
        displayGrid(parent);

        if (controlPanel.showVoltageMode()) { voltageGradient.display(parent); }
        if (controlPanel.showFieldLinesMode()) { displayFieldLines(); }
        if (controlPanel.showFieldVectorsMode()) { displayFieldVectors(); }
        if (controlPanel.showEquipotentialLinesMode()) { displayEquipotentialLines(); }

        for (TestCharge testCharge : testCharges) { testCharge.display(parent); }
        for (PointCharge pointCharge : pointCharges) { pointCharge.display(parent); }

        displayFrameRate(parent);
    }

    public PVector getMousePosition() {
        return mousePosition;
    }

    private void displayEquipotentialLines() {
        for (EquiLine equipotentialLine : equiLines) {
            equipotentialLine.display(parent);
        }
    }

    private void displayFieldVectors() {
        for (FieldVector fieldVector : fieldVectors) {
            fieldVector.display(parent);
        }
    }

    private void displayFieldLines() {
        for (FieldLine fieldLine : fieldLines) {
            fieldLine.display(parent);
        }
    }

    public void displayFrameRate(PApplet app) {
        app.pushMatrix();
        app.fill(255);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text((int) app.frameRate, 10, 10);
        app.popMatrix();
    }

    public void displayGrid(PApplet app) {
        if (controlPanel.showGridMode()) {
            app.pushMatrix();
            app.stroke(255, 255, 255, 50);
            app.strokeWeight(1);
            for (int x = 0; x < (app.displayWidth / GRID_SIZE); x++) {
                Integer xPos = x * GRID_SIZE;
                Integer y1 = 0;
                Integer y2 = app.height;
                app.line(xPos, y1, xPos, y2);
            }
            for (int y = 0; y < (app.displayHeight / GRID_SIZE); y++) {
                Integer yPos = y * GRID_SIZE;
                Integer x1 = 0;
                Integer x2 = app.width;
                app.line(x1, yPos, x2, yPos);
            }

            app.stroke(255, 255, 255, 255);
            app.fill(255, 255, 255, 255);
            app.rect(app.width - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING, 0, SIDE_PANEL_WIDTH + SIDE_PANEL_PADDING, app.height);
            app.popMatrix();
        }
    }

    public ArrayList<PointCharge> getPointCharges() {
        return pointCharges;
    }

    public ArrayList<TestCharge> getTestCharges() {
        return testCharges;
    }

    public void clearEquipotentialLines() {
        this.equiLines = new ArrayList<>();
    }

    public void createEquipotentialLine(PVector position) {
        if (!pointCharges.isEmpty()) {
            ArrayList<PVector> leftPoints = new ArrayList<PVector>();
            ArrayList<PVector> rightPoints = new ArrayList<PVector>();
            // Start the recursion with copies of the origin.
            getEquiLinePoints(position.copy(), position.copy(), position.copy(), 0, leftPoints, rightPoints);
        }
    }

    private void getEquiLinePoints(PVector originPoint, PVector leftPoint, PVector rightPoint, int numberOfLoops,
                                   ArrayList<PVector> leftPoints, ArrayList<PVector> rightPoints) {
        // On first call, initialize arrays if empty.
        if (leftPoints.isEmpty() || rightPoints.isEmpty()) {
            leftPoints.add(originPoint.copy());
            leftPoints.add(originPoint.copy());
            rightPoints.add(originPoint.copy());
            rightPoints.add(originPoint.copy());
            leftPoint = originPoint.copy();
            rightPoint = originPoint.copy();
            numberOfLoops = 0;
        }

        // Update leftPoint by iterating 100 times.
        for (int i = 0; i < 100; i++) {
            PVector forceVector = netForceAtPoint(leftPoint, pointCharges);
            forceVector.rotate((float)Math.PI / 2);
            forceVector.setMag(Constants.EQUI_LINES_ACCURACY);
            leftPoint.add(forceVector);
        }
        leftPoints.add(leftPoint.copy());

        // Update rightPoint similarly.
        for (int i = 0; i < 100; i++) {
            PVector forceVector = netForceAtPoint(rightPoint, pointCharges);
            forceVector.mult(-1);
            forceVector.rotate((float)Math.PI / 2);
            forceVector.setMag(Constants.EQUI_LINES_ACCURACY);
            rightPoint.add(forceVector);
        }
        rightPoints.add(rightPoint.copy());

        // After a few loops, check for convergence.
        if (numberOfLoops > 10 && leftPoints.size() >= 5) {
            PVector pointToCheck = leftPoints.get(leftPoints.size() - 5);
            for (PVector p : rightPoints) {
                if (PVector.dist(pointToCheck, p) < 20) {
                    numberOfLoops = Constants.EQUI_LINES_LIMIT;
                    break;
                }
            }
        }

        if (numberOfLoops < Constants.EQUI_LINES_LIMIT) {
            getEquiLinePoints(originPoint, leftPoint.copy(), rightPoint.copy(), numberOfLoops + 1, leftPoints, rightPoints);
        } else {
            float distanceBetweenLines = PVector.dist(leftPoints.get(leftPoints.size() - 1), rightPoints.get(rightPoints.size() - 1));
            if (distanceBetweenLines < 10) {
                leftPoints.add(rightPoints.get(rightPoints.size() - 1).copy());
                rightPoints.add(leftPoints.get(leftPoints.size() - 1).copy());
            }
            // Create and store two equipotential lines.
            float voltage = voltageAtPoint(leftPoints.get(0), pointCharges);
            org.example.model.EquiLine leftEquiLine = new org.example.model.EquiLine(parent, leftPoints, voltage);
            org.example.model.EquiLine rightEquiLine = new org.example.model.EquiLine(parent, rightPoints, voltage);
            equiLines.add(leftEquiLine);
            equiLines.add(rightEquiLine);
        }
    }

    // Computes field vectors at grid points.
    public void createFieldVectors() {
        // Clear any existing field vectors.
        fieldVectors.clear();
        // Loop through a grid over the canvas (use parent.height and parent.width)
        for (int y = 0; y < parent.height; y += Constants.GRID_SIZE) {
            for (int x = 0; x < parent.width; x += Constants.GRID_SIZE) {
                PVector arrowLocation = new PVector(x, y);

                // Check if no charge is near this grid point.
                boolean noChargesNearby = true;
                for (Charge charge : pointCharges) {
                    if (PVector.dist(arrowLocation, charge.getPosition()) < Constants.CHARGE_DIAMETER) {
                        noChargesNearby = false;
                        break;
                    }
                }

                if (noChargesNearby) {
                    // Get the net force at this location, then scale it.
                    PVector forceVector = netForceAtPoint(arrowLocation, pointCharges);
                    forceVector.div(Constants.FIELD_VECTOR_SCALE); // remove

                    // Create a new FieldVector and add it to the list.
                    fieldVectors.add(new FieldVector(arrowLocation, forceVector));
                }
            }
        }
    }

    // Draws an unsaved arrow for the field vector at the mouse position.
    public void showForceVectorsOnMouse() {
//        PVector mousePos = new PVector(parent.mouseX, parent.mouseY);
        PVector force = netForceAtPoint(mousePosition, pointCharges);
        force.div(Constants.FIELD_VECTOR_SCALE);

        // check if any charges are near the mouse cursor
        boolean noChargesNearby = true;
        for (Charge charge : pointCharges) {
            if (PVector.dist(mousePosition, charge.getPosition()) < Constants.CHARGE_RADIUS) {
                noChargesNearby = false;
                break;
            }
        }

        // only display it if no charges are near the mouse cursor
        if (noChargesNearby) {
            new FieldVector(mousePosition, force).display(parent);
        }
    }

    // Draws an arrow from start to end.
    public void createArrow(PVector start, PVector end, float angle, int arrowColor, float scale) {
        parent.pushMatrix();
        parent.stroke(arrowColor);
        parent.strokeWeight(scale * 4);
        parent.noFill();
        parent.line(start.x, start.y, end.x, end.y);
        parent.translate(end.x, end.y);
        parent.rotate(angle);
        parent.fill(arrowColor);
        parent.triangle(0, 0, -10 * scale, -5 * scale, -10 * scale, 5 * scale);
        parent.popMatrix();
    }

    public void createFieldLines() {
        // Clear any existing field lines.
        fieldLines.clear();
        for (PointCharge charge : pointCharges) {
            float radius = Constants.CHARGE_RADIUS / 2.0f;
            int times = (int)Math.abs(charge.getCharge() * Constants.FIELD_LINES_PER_COULOMB);
            PVector origin = charge.getPosition().copy();
            PVector point = new PVector(radius, radius);
            for (int a = 0; a < times; a++) {
                ArrayList<PVector> linePoints = new ArrayList<>();
                getFieldLinePoints(PVector.add(origin, point), 0, linePoints);
                if (!linePoints.isEmpty()) {
                    fieldLines.add(new org.example.model.FieldLine(linePoints));
                }
                point.rotate((2 * (float)Math.PI) / times);
            }
        }
    }

    private void getFieldLinePoints(PVector startingPosition, int numberOfLoops, ArrayList<PVector> linePoints) {
        if (linePoints == null) {
            linePoints = new ArrayList<>();
            numberOfLoops = 0;
        }
        linePoints.add(startingPosition.copy());
        float vectorMag = Constants.CHARGE_RADIUS;
        if (numberOfLoops < 10) {
            vectorMag = Constants.CHARGE_RADIUS / 4.0f;
        }
        PVector forceVector = netForceAtPoint(startingPosition, pointCharges);
        forceVector.setMag(vectorMag);
        PVector nextPosition = PVector.add(startingPosition, forceVector);

        // If the starting position is not too close to any charge, and we haven't looped too many times…
        boolean nearCharge = false;
        for (Charge c : pointCharges) {
            if (PVector.dist(startingPosition, c.getPosition()) < Constants.CHARGE_RADIUS) {
                nearCharge = true;
                break;
            }
        }

        if (!nearCharge && numberOfLoops < 110) {
            getFieldLinePoints(nextPosition, numberOfLoops + 1, linePoints);
        } else {
            linePoints.add(nextPosition.copy());
            // Field line is complete; it will be added in the calling method.
        }
    }

    public void resetChargeStates() {
        for (PointCharge charge : pointCharges) {
            charge.dragging = false;
            charge.selected = false;
        }
    }

    public boolean selectChargeAtPosition(PVector pos) {
        for (PointCharge charge : pointCharges) {
            if (mouseIsInsideCharge(pos, charge)) {
                charge.selected = true;
                return true;
            }
        }
        return false;
    }

    public void addTestCharge(PVector pos) {
        testCharges.add(new TestCharge(pos.copy(), Constants.TEST_CHARGE_CHARGE));
    }

    public void addPointCharge(PVector pos) {
        PointCharge newCharge = new PointCharge(pos.copy(), 0);
        pointCharges.add(newCharge);
        newCharge.select();
    }

    public void addPointCharge(PVector pos, Float charge) {
        PointCharge newCharge = new PointCharge(pos.copy(), charge);
        pointCharges.add(newCharge);
        newCharge.select();
    }

    public void handleMouseDraggedLogic(PVector mousePos) {
        // (existing logic for handling dragging)
        // For example: update the position of the dragged charge and clear equipotential lines
    }

//    public boolean isTestChargeMode() {
//        return testChargeMode;
//    }
//
//    public boolean isShowEquipotentialLines() {
//        return showEquipotentialLines;
//    }
//
//    public boolean isShowFieldVectors() {
//        return showFieldVectors;
//    }

    public void handleKeyPressedLogic(int keyCode) {
    }

    public void removeAllPointCharges() {
        pointCharges.clear();
    }

    public float getWidth() {
        return parent.width;
    }

    public float getHeight() {
        return parent.height;
    }

    public void clearTestCharges() {
        testCharges.clear();
    }
}
