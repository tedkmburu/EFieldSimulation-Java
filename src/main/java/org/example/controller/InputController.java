package org.example.controller;

import org.example.model.config.ConfigManager;
import org.example.model.*;
import org.example.view.ui.ControlPanel;
import processing.core.*;

import static org.example.model.CommonMath.*;

import java.util.ArrayList;

public class InputController {
    private static PApplet parent;
    private static SimulationModel simulation;
    private static ControlPanel controlPanel;

    public InputController(PApplet parent, SimulationModel simulation, ControlPanel controlPanel) {
        InputController.parent = parent;
        InputController.simulation = simulation;
        InputController.controlPanel = controlPanel;
    }

    public void handleMouseDragged() {
        // Get the current mouse position
        PVector mousePos = simulation.getMousePosition();
        Integer SIDE_PANEL_WIDTH = ConfigManager.getInstance().getSidePanelWidth();
        Integer SIDE_PANEL_PADDING = ConfigManager.getInstance().getSidePanelPadding();
        Float CHARGE_RADIUS = ConfigManager.getInstance().getChargeRadius();
        if (mousePos.x > simulation.getWidth() - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING - CHARGE_RADIUS) {
            mousePos.x = simulation.getWidth() - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING - CHARGE_RADIUS;
        }
        ArrayList<PointCharge> pointCharges = simulation.getPointCharges();

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
            if (controlPanel.snapToGridMode()) {
                Float g = ConfigManager.getInstance().getGridSize();
                Float snappedX = Math.round(mousePos.x / g) * g;
                Float snappedY = Math.round(mousePos.y / g) * g;
                chargeToMove.setPosition(new PVector(snappedX, snappedY));

            }
            else {
                chargeToMove.setPosition(mousePos);
            }
            simulation.voltageDirty = true;
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

        Integer kc = parent.keyCode;  // Get the key code from the parent PApplet
        // Loop over charges and check for a selected one.
        for (int i = 0; i < pointCharges.size(); i++) {
            PointCharge pointCharge = pointCharges.get(i);
            if (pointCharge.selected) {
                // Increase charge when right arrow is pressed.
                if (kc == PConstants.RIGHT && pointCharge.getCharge() < ConfigManager.getInstance().getPointChargeMaxValue()) {
                    pointCharge.increaseCharge();
                }
                // Decrease charge when left arrow is pressed.
                else if (kc == PConstants.LEFT && pointCharge.getCharge() > ConfigManager.getInstance().getPointChargeMinValue()) {
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
        PVector mousePosition = simulation.getMousePosition();

        // If we're in equipotential line mode, create an equipotential line from the click position.
        if (controlPanel.showEquipotentialLinesMode()) {
            simulation.createEquipotentialLine(mousePosition.copy());
        }

        // If test charge mode is active, add a new TestCharge at the mouse position.
        if (controlPanel.testChargeMode()) {
            simulation.addTestCharge(mousePosition.copy());
            return;
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
            simulation.addPointCharge(mousePosition);
        }

    }
}
