package org.example.factories;

import org.example.model.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class ProcessingFieldElementFactory implements FieldElementFactory {
    @Override
    public FieldVector createFieldVector(PVector position, PVector forceVector) {
        return new FieldVector(position, forceVector);
    }

    @Override
    public EquiLine createEquiLine(PApplet app, ArrayList<PVector> points, Float voltage) {
        return new EquiLine(app, points, voltage);
    }
}
