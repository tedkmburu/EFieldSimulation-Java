package org.example.model.config;

import java.util.Properties;

public final class ConfigManager {
    private static volatile ConfigManager instance;
    private final Properties props = new Properties();

    private ConfigManager() {
        props.setProperty("coulombs.constant",        "8990000");
        props.setProperty("test.charge.charge",       "0.000005");
        props.setProperty("point.charge.max.value",   "5");
        props.setProperty("point.charge.min.value",   "-5");
        props.setProperty("charge.increment.delta",   "1.0");
        props.setProperty("grid.size",                "25");
        props.setProperty("side.panel.width",         "200");
        props.setProperty("side.panel.padding",       "50");
        props.setProperty("charge.diameter",          "40");
        props.setProperty("charge.radius",            String.valueOf(40 / 2.0f));
        props.setProperty("test.charge.diameter",     "10");
        props.setProperty("test.charge.radius",       String.valueOf(10 / 2.0f));
        props.setProperty("positive.charge.color",    "0xFFD2292D");
        props.setProperty("negative.charge.color",    "0xFF1761B0");
        props.setProperty("neutral.charge.color",     "0xFF555555");
        props.setProperty("equi.lines.accuracy",      "0.125");
        props.setProperty("equi.lines.limit",         "4500");
        props.setProperty("field.lines.per.coulomb",  "4");
        props.setProperty("field.vector.scale",       "500");
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    // ——— Typed accessors ———

    public float getCoulombsConstant() {
        return Float.parseFloat(props.getProperty("coulombs.constant"));
    }

    public float getTestChargeCharge() {
        return Float.parseFloat(props.getProperty("test.charge.charge"));
    }

    public int getPointChargeMaxValue() {
        return Integer.parseInt(props.getProperty("point.charge.max.value"));
    }

    public int getPointChargeMinValue() {
        return Integer.parseInt(props.getProperty("point.charge.min.value"));
    }

    public float getChargeIncrementDelta() {
        return Float.parseFloat(props.getProperty("charge.increment.delta"));
    }

    public int getGridSize() {
        return Integer.parseInt(props.getProperty("grid.size"));
    }

    public int getSidePanelWidth() {
        return Integer.parseInt(props.getProperty("side.panel.width"));
    }

    public int getSidePanelPadding() {
        return Integer.parseInt(props.getProperty("side.panel.padding"));
    }

    public int getChargeDiameter() {
        return Integer.parseInt(props.getProperty("charge.diameter"));
    }

    public float getChargeRadius() {
        return Float.parseFloat(props.getProperty("charge.radius"));
    }

    public int getTestChargeDiameter() {
        return Integer.parseInt(props.getProperty("test.charge.diameter"));
    }

    public float getTestChargeRadius() {
        return Float.parseFloat(props.getProperty("test.charge.radius"));
    }

    public int getPositiveChargeColor() {
        return Long.decode(props.getProperty("positive.charge.color")).intValue();
    }

    public int getNegativeChargeColor() {
        return Long.decode(props.getProperty("negative.charge.color")).intValue();
    }

    public int getNeutralChargeColor() {
        return Long.decode(props.getProperty("neutral.charge.color")).intValue();
    }

    public float getEquiLinesAccuracy() {
        return Float.parseFloat(props.getProperty("equi.lines.accuracy"));
    }

    public int getEquiLinesLimit() {
        return Integer.parseInt(props.getProperty("equi.lines.limit"));
    }

    public int getFieldLinesPerCoulomb() {
        return Integer.parseInt(props.getProperty("field.lines.per.coulomb"));
    }

    public float getFieldVectorScale() {
        return Float.parseFloat(props.getProperty("field.vector.scale"));
    }
}
