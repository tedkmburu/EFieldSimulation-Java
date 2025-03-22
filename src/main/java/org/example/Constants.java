package org.example;

public class Constants {
    public static final Float COULOMBS_CONSTANT = 8990000f; // Coulomb's constant (adjusted for simulation scale)
    public static final Float TEST_CHARGE_CHARGE = 0.000005F; //q = 5 micro coulombs;

    public static final Integer POINT_CHARGE_MAX_VALUE = 5;
    public static final Integer POINT_CHARGE_MIN_VALUE = -5;
    public static final Float CHARGE_INCREMENT_DELTA = 1.0f;

    public static final Integer GRID_SIZE = 25;
    public static final Integer SIDE_PANEL_WIDTH = 200;
    public static final Integer SIDE_PANEL_PADDING = 50;

    public static final Integer CHARGE_DIAMETER = 40;
    public static final Float CHARGE_RADIUS = CHARGE_DIAMETER / 2.0f;
    public static final Integer TEST_CHARGE_DIAMETER = 10;
    public static final Float TEST_CHARGE_RADIUS = TEST_CHARGE_DIAMETER / 2.0f;

    public static final Integer POSITIVE_CHARGE_COLOR = 0xFFD2292D; // e.g., red
    public static final Integer NEGATIVE_CHARGE_COLOR = 0xFF1761B0; // e.g., blue
    public static final Integer NEUTRAL_CHARGE_COLOR = 0xFF555555;


    public static final Float EQUI_LINES_ACCURACY = 0.125f;
    public static final Integer EQUI_LINES_LIMIT = 4500;

    public static final Integer FIELD_LINES_PER_COULOMB = 4;

    public static final Float FIELD_VECTOR_SCALE = 500f;

}
