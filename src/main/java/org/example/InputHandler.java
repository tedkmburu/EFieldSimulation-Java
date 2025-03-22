package org.example;

import org.example.model.*;
import processing.core.*;

import static org.example.CommonMath.*;
import static org.example.Constants.*;
import static org.example.ControlPanel.*;

import java.util.ArrayList;

public class InputHandler {
    private static PApplet parent;
    private static Simulation simulation;
    private static ControlPanel controlPanel;

    public InputHandler(PApplet parent, Simulation simulation, ControlPanel controlPanel) {
        InputHandler.parent = parent;
        InputHandler.simulation = simulation;
        InputHandler.controlPanel = controlPanel;
    }

    public void handleMousePressed() {
        // Get the current mouse position from the parent PApplet.
        PVector mousePos = new PVector(parent.mouseX, parent.mouseY);

//        // If we're in equipotential line mode, create an equipotential line from the click position.
//        if (simulation.showEquipotentialLinesMode()) {
//            simulation.createEquipotentialLine(mousePosition.copy());
//        }
//
//        // If test charge mode is active, add a new TestCharge at the mouse position.
//        if (simulation.isTestChargeMode()) {
//            simulation.addTestCharge(mousePosition.copy());
//        }
//
//        // Reset dragging/selected flags for all point charges.
//        simulation.resetChargeStates();
//
//        // Check if a point charge was clicked.
//        boolean chargeSelected = simulation.selectChargeAtPosition(mousePos);
//        if (!chargeSelected && !simulation.isTestChargeMode() && !simulation.isShowEquipotentialLines()) {
//            simulation.addPointCharge(mousePos);
//        }
    }

    public void handleMouseDragged() {
        // Get the current mouse position
        PVector mousePos = simulation.getMousePosition();
        if (mousePos.x > simulation.getWidth() - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING - CHARGE_RADIUS) {
            mousePos.x = simulation.getWidth() - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING - CHARGE_RADIUS;
        }
        ArrayList<PointCharge> pointCharges = simulation.getPointCharges();
        ArrayList<TestCharge> testCharges = simulation.getTestCharges();

        // Determine if no charge is currently being dragged.
        boolean noChargeIsBeingDragged = true;
        for (PointCharge pointCharge : pointCharges) {
            if (pointCharge.dragging) {
                noChargeIsBeingDragged = false;
                break;
            }
        }

        // If no charge is being dragged, check if the mouse is over any charge.
        if (noChargeIsBeingDragged) {
            for (PointCharge pointCharge : pointCharges) {
                if (mouseIsInsideCharge(mousePos, pointCharge)) {
                    pointCharge.dragging = true;
                } else {
                    pointCharge.dragging = false;
                }
            }
        }

        // Find the first charge that is marked as dragging.
        PointCharge chargeToMove = null;
        for (PointCharge pointCharge : pointCharges) {
            if (pointCharge.dragging) {
                chargeToMove = pointCharge;
                break;
            }
        }

        // If a charge is being dragged, update its position.
        if (chargeToMove != null) {
            chargeToMove.setPosition(mousePos);
            // Clear equipotential lines if a charge is being dragged.
            simulation.clearEquipotentialLines();
        }
    }

    public void handleMouseMoved() {
        if (controlPanel.showFieldVectorsMode()) {
            simulation.showForceVectorsOnMouse();
        }
    }

    public void handleKeyPressed() {
        ArrayList<PointCharge> pointCharges = simulation.getPointCharges();
        ArrayList<TestCharge> testCharges = simulation.getTestCharges();

        int kc = parent.keyCode;  // Get the key code from the parent PApplet
        // Loop over charges and check for a selected one.
        for (int i = 0; i < pointCharges.size(); i++) {
            PointCharge pointCharge = pointCharges.get(i);
            if (pointCharge.selected) {
                // Increase charge when right arrow is pressed.
                if (kc == PConstants.RIGHT && pointCharge.getCharge() < POINT_CHARGE_MAX_VALUE) {
                    pointCharge.increaseCharge();
                }
                // Decrease charge when left arrow is pressed.
                else if (kc == PConstants.LEFT && pointCharge.getCharge() > POINT_CHARGE_MIN_VALUE) {
                    pointCharge.decreaseCharge();
                }
                // Remove the charge if DELETE (or BACKSPACE) is pressed.
                else if (kc == PConstants.DELETE || kc == PConstants.BACKSPACE) {
                    pointCharges.remove(i);
                    break; // exit the loop since the list has been modified
                }
            }
        }
    }

    public void handleMouseClicked() {
        ArrayList<PointCharge> pointCharges = simulation.getPointCharges();
        ArrayList<TestCharge> testCharges = simulation.getTestCharges();
        PVector mousePosition = simulation.getMousePosition();

        // Get the current mouse position from the parent PApplet.
//        PVector mousePos = new PVector(parent.mouseX, parent.mouseY);

        // If we're in equipotential line mode, create an equipotential line from the click position.
        if (controlPanel.showEquipotentialLinesMode()) {
            simulation.createEquipotentialLine(mousePosition.copy());
        }

        // If test charge mode is active, add a new TestCharge at the mouse position.
        if (controlPanel.testChargeMode()) {
            // Make sure you have a testCharges ArrayList and a constant for the test charge's charge.
            testCharges.add(new TestCharge(mousePosition.copy(), Constants.TEST_CHARGE_CHARGE));
        }

        // For every charge, reset dragging and selected flags.
        for (PointCharge pointCharge : pointCharges) {
            pointCharge.dragging = false;
            pointCharge.selected = false;
        }

        boolean chargeSelected = false;
        // Find if the mouse click is inside any charge's circle.
        for (PointCharge pointCharge : pointCharges) {
            if (mouseIsInsideCharge(simulation.getMousePosition(), pointCharge)) {
                pointCharge.selected = true;
                chargeSelected = true;
                break;  // Stop after finding the first one.
            }
        }

        if (!chargeSelected && !controlPanel.testChargeMode() && !controlPanel.showEquipotentialLinesMode()) {
            // add a point charge on mouse click
            pointCharges.add(new PointCharge(simulation.getMousePosition(), 0));
            pointCharges.getLast().select();
        }

    }
}
