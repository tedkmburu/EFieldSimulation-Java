package org.example.model;

import org.example.model.config.ConfigManager;
import org.example.engine.modes.*;
import org.example.factories.ChargeFactory;
import org.example.factories.FieldElementFactory;
import org.example.factories.ProcessingChargeFactory;
import org.example.factories.ProcessingFieldElementFactory;
import org.example.view.ui.ControlPanel;
import org.example.view.ui.ControlPanelListener;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static org.example.model.CommonMath.*;

public class SimulationModel implements ControlPanelListener {
    public PApplet parent;

    private final FieldElementFactory fieldElementFactory;
    private final ChargeFactory chargeFactory;

    private final List<SimulationMode> modes;

    private ArrayList<PointCharge> pointCharges;
    private ArrayList<FieldLine> fieldLines;
    private ArrayList<EquiLine> equiLines;
    private ArrayList<TestCharge> testCharges;
    private ArrayList<FieldVector> fieldVectors;
    private VoltageGradient voltageGradient;

    public ControlPanel controlPanel;

    private PVector mousePosition;
    public Boolean voltageDirty = true;

    public SimulationModel(PApplet parent, ControlPanel controlPanel, FieldElementFactory fieldElementFactory, ChargeFactory chargeFactory) {
        this.parent = parent;
        this.controlPanel = controlPanel;
        this.fieldElementFactory = fieldElementFactory;
        this.chargeFactory = chargeFactory;

        this.pointCharges = new ArrayList<>();

        // this order controls the rendering ordering
        modes = List.of(
            new VoltageMode(this, controlPanel),
            new FieldVectorMode(this, controlPanel),
            new FieldLineMode(this, controlPanel, fieldElementFactory),
            new TestChargeMode(this, controlPanel),
            new EquipotentialMode(this, controlPanel)
        );

        this.equiLines = new ArrayList<>();
        this.fieldLines = new ArrayList<>();
        this.fieldVectors = new ArrayList<>();
        this.testCharges = new ArrayList<>();
        this.voltageGradient = new VoltageGradient(this);
    }

    public SimulationModel(PApplet parent, ControlPanel controlPanel) {
        this(parent, controlPanel, new ProcessingFieldElementFactory(), new ProcessingChargeFactory());
    }

    public void update() {
        mousePosition = new PVector(parent.mouseX, parent.mouseY);
        for (SimulationMode mode : modes) {
            mode.update();
        }
    }

    public void display() {
        for (SimulationMode mode : modes) {
            mode.display(parent);
        }
        displayGrid(parent);
        for (TestCharge testCharge : testCharges) { testCharge.display(parent); }
        for (PointCharge pointCharge : pointCharges) { pointCharge.display(parent); }
        displayFrameRate(parent);
        displaySidePanelBackground(parent);
    }

    public PVector getMousePosition() {
        return mousePosition;
    }

    public void displayEquipotentialLines(PApplet app) {
        for (EquiLine equipotentialLine : equiLines) {
            equipotentialLine.display(parent);
        }
    }

    public void displayFieldVectors(PApplet app) {
        for (FieldVector fieldVector : fieldVectors) {
            fieldVector.display(parent);
        }
    }

    public void displayFieldLines(PApplet app) {
        for (FieldLine fieldLine : fieldLines) {
            fieldLine.display(parent);
        }
    }

    public void updateVoltageGradient() {
        voltageGradient.updateVoltageGradient(pointCharges);
    }

    public void displayVoltage(PApplet app) {
        voltageGradient.display(parent);
    }

    public void displayFrameRate(PApplet app) {
        app.pushMatrix();
        app.fill(255);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text((int) app.frameRate, 10, 10);
        app.popMatrix();
    }

    public void displayGrid(PApplet app) {
        Float GRID_SIZE = ConfigManager.getInstance().getGridSize();
        if (controlPanel.showGridMode()) {
            app.pushMatrix();
            app.stroke(255, 255, 255, 50);
            app.strokeWeight(1);
            for (Integer x = 0; x < (app.displayWidth / GRID_SIZE); x++) {
                Float xPos = x * GRID_SIZE;
                Integer y1 = 0;
                Integer y2 = app.height;
                app.line(xPos, y1, xPos, y2);
            }
            for (Integer y = 0; y < (app.displayHeight / GRID_SIZE); y++) {
                Float yPos = y * GRID_SIZE;
                Integer x1 = 0;
                Integer x2 = app.width;
                app.line(x1, yPos, x2, yPos);
            }
            app.popMatrix();
        }
    }

    public void displaySidePanelBackground(PApplet app) {
        // create white background for side panel
        Integer SIDE_PANEL_WIDTH = ConfigManager.getInstance().getSidePanelWidth();
        Integer SIDE_PANEL_PADDING = ConfigManager.getInstance().getSidePanelPadding();

        app.pushMatrix();
        app.stroke(255, 255, 255, 255);
        app.fill(255, 255, 255, 255);
        app.rect(app.width - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING, 0, SIDE_PANEL_WIDTH + SIDE_PANEL_PADDING, app.height);
        app.popMatrix();
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

    private void getEquiLinePoints(PVector originPoint, PVector leftPoint, PVector rightPoint, int numberOfLoops, ArrayList<PVector> leftPoints, ArrayList<PVector> rightPoints) {
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
        for (Integer i = 0; i < 100; i++) {
            PVector forceVector = netForceAtPoint(leftPoint, pointCharges);
            forceVector.rotate((float)Math.PI / 2);
            forceVector.setMag(ConfigManager.getInstance().getEquiLinesAccuracy());
            leftPoint.add(forceVector);
        }
        leftPoints.add(leftPoint.copy());

        // Update rightPoint similarly.
        for (Integer i = 0; i < 100; i++) {
            PVector forceVector = netForceAtPoint(rightPoint, pointCharges);
            forceVector.mult(-1);
            forceVector.rotate((float)Math.PI / 2);
            forceVector.setMag(ConfigManager.getInstance().getEquiLinesAccuracy());
            rightPoint.add(forceVector);
        }
        rightPoints.add(rightPoint.copy());

        // After a few loops, check for convergence.
        if (numberOfLoops > 10 && leftPoints.size() >= 5) {
            PVector pointToCheck = leftPoints.get(leftPoints.size() - 5);
            for (PVector p : rightPoints) {
                if (PVector.dist(pointToCheck, p) < 20) {
                    numberOfLoops = ConfigManager.getInstance().getEquiLinesLimit();
                    break;
                }
            }
        }

        if (numberOfLoops < ConfigManager.getInstance().getEquiLinesLimit()) {
            getEquiLinePoints(originPoint, leftPoint.copy(), rightPoint.copy(), numberOfLoops + 1, leftPoints, rightPoints);
        } else {
            float distanceBetweenLines = PVector.dist(leftPoints.get(leftPoints.size() - 1), rightPoints.get(rightPoints.size() - 1));
            if (distanceBetweenLines < 10) {
                leftPoints.add(rightPoints.get(rightPoints.size() - 1).copy());
                rightPoints.add(leftPoints.get(leftPoints.size() - 1).copy());
            }
            // Create and store two equipotential lines.
            Float voltage = voltageAtPoint(leftPoints.get(0), pointCharges);
            EquiLine leftEquiLine = fieldElementFactory.createEquiLine(parent, leftPoints, voltage);
            EquiLine rightEquiLine = fieldElementFactory.createEquiLine(parent, rightPoints, voltage);
            equiLines.add(leftEquiLine);
            equiLines.add(rightEquiLine);
        }
    }

    // Computes field vectors at grid points.
    public void createFieldVectors() {
        // Clear any existing field vectors.
        fieldVectors.clear();
        // Loop through a grid over the canvas (use parent.height and parent.width)
        for (Float y = 0.0f; y < parent.height; y += ConfigManager.getInstance().getGridSize()) {
            for (Float x = 0.0f; x < parent.width; x += ConfigManager.getInstance().getGridSize()) {
                PVector arrowLocation = new PVector(x, y);

                // Check if no charge is near this grid point.
                Boolean noChargesNearby = true;
                for (Charge charge : pointCharges) {
                    if (PVector.dist(arrowLocation, charge.getPosition()) < ConfigManager.getInstance().getChargeDiameter()) {
                        noChargesNearby = false;
                        break;
                    }
                }

                if (noChargesNearby) {
                    // Get the net force at this location, then scale it.
                    PVector forceVector = netForceAtPoint(arrowLocation, pointCharges);
                    forceVector.div(ConfigManager.getInstance().getFieldVectorScale()); // remove

                    // Create a new FieldVector and add it to the list.
                    fieldVectors.add(fieldElementFactory.createFieldVector(arrowLocation, forceVector));
                }
            }
        }
    }

    // Draws an unsaved arrow for the field vector at the mouse position.
    public void showForceVectorsOnMouse() {
        PVector force = netForceAtPoint(mousePosition, pointCharges);
        force.div(ConfigManager.getInstance().getFieldVectorScale());

        // check if any charges are near the mouse cursor
        boolean noChargesNearby = true;
        for (Charge charge : pointCharges) {
            if (PVector.dist(mousePosition, charge.getPosition()) < ConfigManager.getInstance().getChargeRadius()) {
                noChargesNearby = false;
                break;
            }
        }

        // only display it if no charges are near the mouse cursor
        if (noChargesNearby) {
            new FieldVector(mousePosition, force).display(parent);
        }
    }

    public void createFieldLines() {
        // Clear any existing field lines.
        fieldLines.clear();

        for (PointCharge charge : pointCharges) {
            if (charge.getCharge() > 0)
            {
                Float radius = ConfigManager.getInstance().getChargeRadius();
                Integer times = (int)Math.abs(charge.getCharge() * ConfigManager.getInstance().getFieldLinesPerCoulomb());
                PVector origin = charge.getPosition().copy();
                PVector point = new PVector(radius, 0);
                for (Integer a = 0; a < times; a++) {
                    PVector startingPoint = PVector.add(origin, point);

                    FieldLine line = new FieldLineBuilder(parent, startingPoint, pointCharges).build();
                    fieldLines.add(line);

                    point.rotate((2 * (float)Math.PI) / times);
                }
            }
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
        testCharges.add(chargeFactory.createTestCharge(pos, ConfigManager.getInstance().getTestChargeCharge()));
    }

    public void addPointCharge(PVector pos) {
        addPointCharge(pos, 0f);
    }

    public void addPointCharge(PVector pos, Float charge) {
        PointCharge pc = chargeFactory.createPointCharge(pos, charge);
        pointCharges.add(pc);
        pc.select();
        voltageDirty = true;
    }

    public void removeAllPointCharges() {
        pointCharges.clear();
        voltageDirty = true;
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

    public boolean isVoltageDirty() {
        return voltageDirty;
    }

    public void moveTestCharges() {
        for (TestCharge testCharge : testCharges) {
            testCharge.move(pointCharges);
        }
    }

    public void displayTestCharges(PApplet app) {
        for (TestCharge testCharge : testCharges) {
            testCharge.display(app);
        }
    }

    @Override
    public void onFieldLinesToggled(Boolean on) {
        if (on) {
            createFieldLines();
        } else {
            fieldLines.clear();
        }
    }

    @Override
    public void onFieldVectorsToggled(Boolean on) {
        if (on) {
            // regenerate field vectors immediately
            createFieldVectors();
        } else {
            // hide them
            fieldVectors.clear();
        }
    }

    @Override
    public void onEquipotentialToggled(Boolean on) {
        if (!on) {
            // clear any existing equipotential lines
            equiLines.clear();
        }
    }

    @Override
    public void onVoltageToggled(Boolean on){
        // mark voltage map dirty so it’ll be recomputed/displayed on next draw
        voltageDirty = true;
    }

    @Override
    public void onGridToggled(Boolean on) {
        // no immediate action needed—displayGrid() reads the ControlPanel flag
    }

    @Override
    public void onSnapToGridToggled(Boolean on) {
        // no action here—InputHandler will pick up the snap setting
    }

    @Override
    public void onTestChargeModeToggled(Boolean on) {
        if (!on) {
            // exiting test-charge mode, clear any stray test charges
            clearTestCharges();
        }
    }

    @Override
    public void onSinglePreset() {
        PresetConfigurator.setSingleConfiguration(this);
    }

    @Override
    public void onDipolePreset() {
        PresetConfigurator.setDipoleConfiguration(this);
    }

    @Override
    public void onRowPreset() {
        PresetConfigurator.setRowConfiguration(this);
    }

    @Override
    public void onDipoleRowPreset() {
        PresetConfigurator.setDipoleRowConfiguration(this);
    }

    @Override
    public void onRandomPreset() {
        PresetConfigurator.setRandomConfiguration(this);
    }

    @Override
    public void onCreateTestChargeMap() {
        PresetConfigurator.createTestChargeMap(this);
    }

    @Override
    public void onClearTestCharges() {
        clearTestCharges();
    }

    @Override
    public void onRemoveAllCharges() {
        removeAllPointCharges();
    }
}
