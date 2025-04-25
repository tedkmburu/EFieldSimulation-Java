package org.example.config;

import org.example.model.config.ConfigManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigManagerTest {

    private final ConfigManager cm = ConfigManager.getInstance();

    @Test
    void testCoulombsConstant() {
        assertEquals(8_990_000.0f, cm.getCoulombsConstant(), 1e-3f,
                "Coulombs constant should match default");
    }

    @Test
    void testTestChargeCharge() {
        assertEquals(0.000005f, cm.getTestChargeCharge(), 1e-9f,
                "Test charge default charge should be 0.000005");
    }

    @Test
    void testPointChargeMaxValue() {
        assertEquals(5, cm.getPointChargeMaxValue(),
                "Point charge max value should be 5");
    }

    @Test
    void testPointChargeMinValue() {
        assertEquals(-5, cm.getPointChargeMinValue(),
                "Point charge min value should be -5");
    }

    @Test
    void testChargeIncrementDelta() {
        assertEquals(1.0f, cm.getChargeIncrementDelta(), 1e-6f,
                "Charge increment delta should be 1.0");
    }

    @Test
    void testGridSize() {
        assertEquals(25, cm.getGridSize(),
                "Grid size should be 25");
    }

    @Test
    void testSidePanelWidth() {
        assertEquals(200, cm.getSidePanelWidth(),
                "Side panel width should be 200");
    }

    @Test
    void testSidePanelPadding() {
        assertEquals(50, cm.getSidePanelPadding(),
                "Side panel padding should be 50");
    }

    @Test
    void testChargeDiameter() {
        assertEquals(40, cm.getChargeDiameter(),
                "Charge diameter should be 40");
    }

    @Test
    void testChargeRadius() {
        assertEquals(20.0f, cm.getChargeRadius(), 1e-6f,
                "Charge radius should be half the diameter (20.0)");
    }

    @Test
    void testTestChargeDiameter() {
        assertEquals(10, cm.getTestChargeDiameter(),
                "Test charge diameter should be 10");
    }

    @Test
    void testTestChargeRadius() {
        assertEquals(5.0f, cm.getTestChargeRadius(), 1e-6f,
                "Test charge radius should be half the diameter (5.0)");
    }

    @Test
    void testPositiveChargeColor() {
        assertEquals(0xFFD2292D, cm.getPositiveChargeColor(),
                "Positive charge color should match 0xFFD2292D");
    }

    @Test
    void testNegativeChargeColor() {
        assertEquals(0xFF1761B0, cm.getNegativeChargeColor(),
                "Negative charge color should match 0xFF1761B0");
    }

    @Test
    void testNeutralChargeColor() {
        assertEquals(0xFF555555, cm.getNeutralChargeColor(),
                "Neutral charge color should match 0xFF555555");
    }

    @Test
    void testEquiLinesAccuracy() {
        assertEquals(0.125f, cm.getEquiLinesAccuracy(), 1e-6f,
                "Equipotential line accuracy should be 0.125");
    }

    @Test
    void testEquiLinesLimit() {
        assertEquals(4500, cm.getEquiLinesLimit(),
                "Equipotential line limit should be 4500");
    }

    @Test
    void testFieldLinesPerCoulomb() {
        assertEquals(4, cm.getFieldLinesPerCoulomb(),
                "Field lines per coulomb should be 4");
    }

    @Test
    void testFieldVectorScale() {
        assertEquals(500.0f, cm.getFieldVectorScale(), 1e-6f,
                "Field vector scale should be 500.0");
    }
}
