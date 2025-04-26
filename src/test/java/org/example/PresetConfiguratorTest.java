package org.example;

import org.example.model.PresetConfigurator;
import org.example.model.SimulationModel;
import org.example.model.PointCharge;
import org.example.model.TestCharge;
import org.example.model.config.ConfigManager;
import processing.core.PApplet;
import processing.core.PVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PresetConfiguratorTest {

    private ConfigManager cm;
    private SimulationModel sim;
    private PApplet pap;

    @BeforeEach
    void setUp() {
        cm = ConfigManager.getInstance();

        // Initialize a headless Processing sketch so pap.g is non-null
        pap = new PApplet();
        PApplet.runSketch(new String[]{"PresetConfiguratorTest"}, pap);
        pap.noLoop();

        // Force a predictable canvas size
        pap.width = 800;
        pap.height = 600;

        // Now it's safe to construct the SimulationModel
        sim = new SimulationModel(pap, null);
    }

    @Test
    void testSetSingleConfiguration() {
        PresetConfigurator.setSingleConfiguration(sim);
        List<PointCharge> charges = sim.getPointCharges();
        assertEquals(1, charges.size(), "Should have exactly one point charge");

        PointCharge pc = charges.get(0);

        float midX = (pap.width - (cm.getSidePanelWidth() + cm.getSidePanelPadding())) / 2f;
        float midY = pap.height / 2f;
        float expectedX = Math.round(midX / cm.getGridSize()) * cm.getGridSize();
        float expectedY = Math.round(midY / cm.getGridSize()) * cm.getGridSize();

        assertEquals(expectedX, pc.getPosition().x, 1e-3f, "Single charge x-position");
        assertEquals(expectedY, pc.getPosition().y, 1e-3f, "Single charge y-position");
        assertEquals(PresetConfigurator.SINGLE_CHARGE_VALUE, pc.getCharge(), 1e-6f, "Single charge value");
    }

    @Test
    void testSetDipoleConfiguration() {
        PresetConfigurator.setDipoleConfiguration(sim);
        List<PointCharge> charges = sim.getPointCharges();
        assertEquals(2, charges.size(), "Should have exactly two point charges");

        float midX = (pap.width - (cm.getSidePanelWidth() + cm.getSidePanelPadding())) / 2f;
        float midY = pap.height / 2f;
        float spacing = PresetConfigurator.DIPOLE_SPACING_MULTIPLIER * cm.getGridSize();
        float leftX = Math.round((midX - spacing / 2f) / cm.getGridSize()) * cm.getGridSize();
        float rightX = Math.round((midX + spacing / 2f) / cm.getGridSize()) * cm.getGridSize();
        float expectedY = Math.round(midY / cm.getGridSize()) * cm.getGridSize();

        PointCharge left = charges.get(0);
        assertEquals(leftX, left.getPosition().x, 1e-3f, "Dipole left x-position");
        assertEquals(expectedY, left.getPosition().y, 1e-3f, "Dipole left y-position");
        assertEquals(PresetConfigurator.DIPOLE_CHARGE_MAGNITUDE, left.getCharge(), 1e-6f, "Left charge value");

        PointCharge right = charges.get(1);
        assertEquals(rightX, right.getPosition().x, 1e-3f, "Dipole right x-position");
        assertEquals(expectedY, right.getPosition().y, 1e-3f, "Dipole right y-position");
        assertEquals(-PresetConfigurator.DIPOLE_CHARGE_MAGNITUDE, right.getCharge(), 1e-6f, "Right charge value");
    }

    @Test
    void testSetRowConfiguration() {
        PresetConfigurator.setRowConfiguration(sim);
        List<PointCharge> charges = sim.getPointCharges();
        assertEquals(PresetConfigurator.ROW_COUNT, charges.size(), "Should have ROW_COUNT point charges");

        float midX = (pap.width - (cm.getSidePanelWidth() + cm.getSidePanelPadding())) / 2f;
        float midY = pap.height / 2f;
        float spacing = cm.getGridSize() * PresetConfigurator.ROW_SPACING_MULTIPLIER;
        float totalWidth = spacing * (PresetConfigurator.ROW_COUNT - 1);
        float startX = midX - totalWidth / 2f;
        float expectedY = Math.round(midY / cm.getGridSize()) * cm.getGridSize();

        for (int i = 0; i < PresetConfigurator.ROW_COUNT; i++) {
            PointCharge pc = charges.get(i);
            float rawX = startX + i * spacing;
            float expectedX = Math.round(rawX / cm.getGridSize()) * cm.getGridSize();

            assertEquals(expectedX, pc.getPosition().x, 1e-3f, "Row charge " + i + " x-position");
            assertEquals(expectedY, pc.getPosition().y, 1e-3f, "Row charge " + i + " y-position");
            assertEquals(PresetConfigurator.SINGLE_CHARGE_VALUE, pc.getCharge(), 1e-6f, "Row charge " + i + " value");
        }
    }

    @Test
    void testSetDipoleRowConfiguration() {
        PresetConfigurator.setDipoleRowConfiguration(sim);
        List<PointCharge> charges = sim.getPointCharges();
        Integer expectedCount = 2 * PresetConfigurator.DIPOLE_ROW_COUNT;
        assertEquals(expectedCount, charges.size(), "Should have 2×DIPOLE_ROW_COUNT point charges");

        Float midX = (pap.width - (cm.getSidePanelWidth() + cm.getSidePanelPadding())) / 2f;
        Float midY = pap.height / 2f;
        Float spacing = cm.getGridSize() * PresetConfigurator.DIPOLE_ROW_SPACING_MULTIPLIER;
        Float totalWidth = spacing * (PresetConfigurator.DIPOLE_ROW_COUNT - 1);
        Float startX = midX - totalWidth / 2f;
        Float verticalHalf = spacing * PresetConfigurator.DIPOLE_ROW_VERTICAL_FACTOR / 2f;
        Float expectedPosY = Math.round((midY - verticalHalf) / cm.getGridSize()) * cm.getGridSize();
        Float expectedNegY = Math.round((midY + verticalHalf) / cm.getGridSize()) * cm.getGridSize();

        for (Integer i = 0; i < PresetConfigurator.DIPOLE_ROW_COUNT; i++) {
            Integer base = 2 * i;
            PointCharge pos = charges.get(base);
            PointCharge neg = charges.get(base + 1);

            Float rawX = startX + i * spacing;
            Float expectedX = Math.round(rawX / cm.getGridSize()) * cm.getGridSize();

            // Positive pole
            assertEquals(expectedX, pos.getPosition().x, 1e-3f, "Dipole-row pos " + i + " x");
            assertEquals(expectedPosY, pos.getPosition().y, 1e-3f, "Dipole-row pos " + i + " y");
            assertEquals(PresetConfigurator.DIPOLE_CHARGE_MAGNITUDE, pos.getCharge(), 1e-6f, "Dipole-row pos " + i + " charge");

            // Negative pole
            assertEquals(expectedX, neg.getPosition().x, 1e-3f, "Dipole-row neg " + i + " x");
            assertEquals(expectedNegY, neg.getPosition().y, 1e-3f, "Dipole-row neg " + i + " y");
            assertEquals(-PresetConfigurator.DIPOLE_CHARGE_MAGNITUDE, neg.getCharge(), 1e-6f, "Dipole-row neg " + i + " charge");
        }
    }

    @Test
    void testRandomConfiguration() {
        PresetConfigurator.setRandomConfiguration(sim);
        List<PointCharge> charges = sim.getPointCharges();
        assertEquals(PresetConfigurator.RANDOM_CHARGE_COUNT, charges.size(), "Should have RANDOM_CHARGE_COUNT point charges");

        for (PointCharge pc : charges) {
            Float c = pc.getCharge();
            Boolean isMax = c.equals(cm.getPointChargeMaxValue());
            Boolean isMin = c.equals(cm.getPointChargeMinValue());
            assertTrue(isMax || isMin, "Each random charge must be either max or min");
        }
    }

    @Test
    void testCreateAndClearTestChargeMap() {
        // Create the test‐charge map
        PresetConfigurator.createTestChargeMap(sim);
        List<TestCharge> tcs = sim.getTestCharges();

        int cols = (int) ((sim.getWidth() - cm.getGridSize()) / cm.getGridSize());
        int rows = (int) (sim.getHeight() / cm.getGridSize());
        int step = PresetConfigurator.TEST_CHARGE_MAP_STEP;
        int countX = (cols + step - 1) / step;
        int countY = (rows + step - 1) / step;
        int expectedTotal = countX * countY;

        assertEquals(expectedTotal, tcs.size(), "Test-charge map should have the expected count");

        for (TestCharge tc : tcs) {
            assertEquals(cm.getTestChargeCharge(), tc.getCharge(), 1e-9f, "Each test charge should use the default test charge value");
        }

        // Then clear them
        PresetConfigurator.clearTestCharges(sim);
        assertTrue(sim.getTestCharges().isEmpty(), "clearTestCharges should remove all test charges");
    }
}
