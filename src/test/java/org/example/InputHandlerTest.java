package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Supplier;

import org.example.controller.InputController;
import org.example.model.SimulationModel;
import org.example.model.PointCharge;
import org.example.view.ui.ControlPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class InputHandlerTest {

    private PApplet app;
    private SimulationModel simulation;
    private ControlPanel controlPanel;
    private InputController inputController;

    @BeforeEach
    public void setup() {
        // Create a minimal PApplet instance with essential fields.
        app = new PApplet();
        app.width = 800;
        app.height = 600;
        app.displayWidth = 800;
        app.displayHeight = 600;
        // Initialize default mouse coordinates.
        app.mouseX = 100;
        app.mouseY = 100;

        // Create a ControlPanel in no-UI mode (skip UI initialization).
        controlPanel = new ControlPanel(app, null, true) {
            @Override public boolean showFieldVectorsMode() { return false; }
            @Override public boolean testChargeMode() { return false; }
            @Override public boolean showEquipotentialLinesMode() { return false; }
        };

        // Create a Simulation instance.
        simulation = new SimulationModel(app, controlPanel);

        // Create the InputHandler.
        inputController = new InputController(app, simulation, controlPanel);
    }

    // 1. Mouse Pressed – Charge Selection:
    // Simulate a mouse press at a location within a charge's radius and assert that it becomes selected.
    @Test
    public void testHandleMousePressedChargeSelection() {
        // Set parent's mouse coordinates to (200,200) which is inside the first charge.
        app.mouseX = 200;
        app.mouseY = 200;
        // (Do not call simulation.update() here because handleMousePressed directly creates a new PVector.)
        // Clear any previously selected flags.
        for (PointCharge pc : simulation.getPointCharges()) {
            pc.selected = false;
        }
        inputController.handleMousePressed();
        boolean foundSelected = false;
        for (PointCharge pc : simulation.getPointCharges()) {
            if (pc.selected) {
                foundSelected = true;
                break;
            }
        }
        assertTrue(foundSelected, "A charge should be selected when mouse is pressed within its radius.");
    }

    // 2. Mouse Dragged – Charge Movement:
    // Simulate dragging a charge and check that its position changes and that equipotential lines are cleared.
    @Test
    public void testHandleMouseDraggedChargeMovement() throws Exception {
        // First, set mouse position to exactly the first charge's position.
        PointCharge charge = simulation.getPointCharges().get(0);
        app.mouseX = (int) charge.getPosition().x;
        app.mouseY = (int) charge.getPosition().y;
        // Call update() so that simulation.mousePosition is set.
        simulation.update();
        inputController.handleMouseDragged();
        assertTrue(charge.dragging, "Charge should be marked as dragging when mouse is over it during drag.");

        // Record old position.
        float oldX = charge.getPosition().x;
        float oldY = charge.getPosition().y;
        // Simulate dragging: update mouse position.
        app.mouseX = (int) (oldX + 50);
        app.mouseY = (int) (oldY + 50);
        simulation.update();
        inputController.handleMouseDragged();
        // Verify that the charge's position has updated.
        assertTrue(charge.getPosition().x > oldX, "Charge x position should increase after dragging.");
        assertTrue(charge.getPosition().y > oldY, "Charge y position should increase after dragging.");

        // Verify that equipotential lines are cleared.
        Field equiLinesField = SimulationModel.class.getDeclaredField("equiLines");
        equiLinesField.setAccessible(true);
        ArrayList<?> equiLines = (ArrayList<?>) equiLinesField.get(simulation);
        assertTrue(equiLines.isEmpty(), "Equipotential lines should be cleared after dragging a charge.");
    }

    // 3. Mouse Moved – Field Vector Display:
    // When showFieldVectorsMode() is true, verify that showForceVectorsOnMouse() is executed.
    @Test
    public void testHandleMouseMovedFieldVectorDisplay() {
        // Subclass Simulation to override showForceVectorsOnMouse().
        class TestSimulation extends SimulationModel {
            public boolean forceVectorDisplayed = false;
            public TestSimulation(PApplet parent, ControlPanel cp) {
                super(parent, cp);
            }
            @Override
            public void showForceVectorsOnMouse() {
                forceVectorDisplayed = true;
            }
        }
        // Use a ControlPanel that returns true for showFieldVectorsMode.
        controlPanel = new ControlPanel(app, null, true) {
            @Override public boolean showFieldVectorsMode() { return true; }
        };
        TestSimulation testSim = new TestSimulation(app, controlPanel);
        inputController = new InputController(app, testSim, controlPanel);
        // Update simulation to set internal mousePosition.
        testSim.update();
        inputController.handleMouseMoved();
        assertTrue(testSim.forceVectorDisplayed, "showForceVectorsOnMouse should be executed when showFieldVectorsMode is true.");
    }

    // 4. Key Pressed – Charge Modification:
    // Simulate key presses to increase, decrease, and remove a selected charge.
    @Test
    public void testHandleKeyPressedChargeModification() {
        // Select the first charge.
        PointCharge charge = simulation.getPointCharges().get(0);
        charge.selected = true;
        // Set the charge value to 0 so that it is below the maximum.
        charge.setCharge(0f);
        float originalCharge = charge.getCharge();
        // Simulate RIGHT arrow press (increase charge).
        app.keyCode = PConstants.RIGHT;
        inputController.handleKeyPressed();
        assertTrue(charge.getCharge() > originalCharge, "Charge should increase after RIGHT arrow key press.");

        // Now simulate LEFT arrow press (decrease charge).
        charge.selected = true;
        originalCharge = charge.getCharge();
        app.keyCode = PConstants.LEFT;
        inputController.handleKeyPressed();
        assertTrue(charge.getCharge() < originalCharge, "Charge should decrease after LEFT arrow key press.");

        // Simulate DELETE key press to remove a charge.
        simulation.addPointCharge(new PVector(400, 400), 0f);
        int countBefore = simulation.getPointCharges().size();
        PointCharge newCharge = simulation.getPointCharges().get(simulation.getPointCharges().size() - 1);
        newCharge.selected = true;
        app.keyCode = PConstants.DELETE;
        inputController.handleKeyPressed();
        int countAfter = simulation.getPointCharges().size();
        assertTrue(countAfter == countBefore - 1, "Charge should be removed after DELETE key press.");
    }

    // 5. Mouse Clicked – Mode-Dependent Behavior:
    // Depending on active mode, assert that clicking creates an equipotential line, adds a test charge, or adds a new point charge.
    @Test
    public void testHandleMouseClickedModeDependentBehavior() throws Exception {
        // Helper to access equipotential lines count.
        Supplier<Integer> getEquipotentialCount = () -> {
            try {
                Field equiLinesField = SimulationModel.class.getDeclaredField("equiLines");
                equiLinesField.setAccessible(true);
                ArrayList<?> equiLines = (ArrayList<?>) equiLinesField.get(simulation);
                return equiLines.size();
            } catch (Exception e) {
                return -1;
            }
        };

        // a) Equipotential line mode active.
        controlPanel = new ControlPanel(app, null, true) {
            @Override public boolean showEquipotentialLinesMode() { return true; }
            @Override public boolean testChargeMode() { return false; }
        };
        simulation = new SimulationModel(app, controlPanel);
        inputController = new InputController(app, simulation, controlPanel);
        // Set mouse coordinates and update simulation.
        app.mouseX = 250;
        app.mouseY = 250;
        simulation.update();
        int initialEquiCount = getEquipotentialCount.get();
        inputController.handleMouseClicked();
        int afterEquiCount = getEquipotentialCount.get();
        assertTrue(afterEquiCount > initialEquiCount, "An equipotential line should be created when equipotential mode is active.");

        // b) Test charge mode active.
        controlPanel = new ControlPanel(app, null, true) {
            @Override public boolean showEquipotentialLinesMode() { return false; }
            @Override public boolean testChargeMode() { return true; }
        };
        simulation = new SimulationModel(app, controlPanel);
        inputController = new InputController(app, simulation, controlPanel);
        simulation.update();
        int initialTestChargeCount = simulation.getTestCharges().size();
        inputController.handleMouseClicked();
        int afterTestChargeCount = simulation.getTestCharges().size();
        assertEquals(initialTestChargeCount + 1, afterTestChargeCount, "A test charge should be added when test charge mode is active.");

        // c) Default mode (neither equipotential nor test charge mode active).
        controlPanel = new ControlPanel(app, null, true) {
            @Override public boolean showEquipotentialLinesMode() { return false; }
            @Override public boolean testChargeMode() { return false; }
        };
        simulation = new SimulationModel(app, controlPanel);
        inputController = new InputController(app, simulation, controlPanel);
        // Set mouse coordinates away from any existing charge.
        app.mouseX = 50;
        app.mouseY = 50;
        simulation.update();
        int initialPointChargeCount = simulation.getPointCharges().size();
        inputController.handleMouseClicked();
        int afterPointChargeCount = simulation.getPointCharges().size();
        assertEquals(initialPointChargeCount + 1, afterPointChargeCount, "A new point charge should be added when in default mode.");
    }
}
