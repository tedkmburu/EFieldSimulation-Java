package org.example.factories;

import org.example.factories.FieldElementFactory;
import org.example.factories.ProcessingFieldElementFactory;
import org.example.model.FieldVector;
import org.example.model.EquiLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PVector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FieldElementFactoryTest {

    private FieldElementFactory factory;
    private PApplet pap;

    @BeforeEach
    void setUp() {
        factory = new ProcessingFieldElementFactory();
        pap = new PApplet();
        PApplet.runSketch(new String[]{"FieldElementFactoryTest"}, pap);
        pap.noLoop();
        pap.width = 300;
        pap.height = 200;
    }

    @Test
    void testCreateFieldVector_CopiesPositionAndForce() throws Exception {
        PVector originalPos = new PVector(5.5f, -3.3f);
        PVector originalForce = new PVector(1.1f, 2.2f);

        FieldVector fv = factory.createFieldVector(originalPos, originalForce);
        assertNotNull(fv, "FieldVector should not be null");

        // Reflectively access private 'position' and 'forceVector'
        Field posField = FieldVector.class.getDeclaredField("position");
        posField.setAccessible(true);
        PVector posCopy = (PVector) posField.get(fv);

        Field forceField = FieldVector.class.getDeclaredField("forceVector");
        forceField.setAccessible(true);
        PVector forceCopy = (PVector) forceField.get(fv);

        // Check copies have same values but are distinct instances
        assertEquals(originalPos.x, posCopy.x, 1e-6f, "Copied position x should match original");
        assertEquals(originalPos.y, posCopy.y, 1e-6f, "Copied position y should match original");
        assertNotSame(originalPos, posCopy, "Position should be a copy, not same instance");

        assertEquals(originalForce.x, forceCopy.x, 1e-6f, "Copied force x should match original");
        assertEquals(originalForce.y, forceCopy.y, 1e-6f, "Copied force y should match original");
        assertNotSame(originalForce, forceCopy, "Force vector should be a copy, not same instance");

        // Display should run without error
        assertDoesNotThrow(() -> fv.display(pap), "display() should not throw");
    }

    @Test
    void testCreateEquiLine_ReferencesSamePointsList() throws Exception {
        ArrayList<PVector> points = new ArrayList<>();
        points.add(new PVector(0, 0));
        points.add(new PVector(10, 10));
        float voltage = 42.0f;

        EquiLine eq = factory.createEquiLine(pap, points, voltage);
        assertNotNull(eq, "EquiLine should not be null");

        Field pointsField = EquiLine.class.getDeclaredField("points");
        pointsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<PVector> refPoints = (List<PVector>) pointsField.get(eq);

        assertSame(points, refPoints, "EquiLine should reference the same points list");

        // Display should run without error
        assertDoesNotThrow(() -> eq.display(pap), "display() should not throw");
    }
}
