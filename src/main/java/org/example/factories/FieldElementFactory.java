package org.example.factories;

import org.example.model.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public interface FieldElementFactory {
    FieldVector createFieldVector(PVector position, PVector forceVector);
    EquiLine   createEquiLine(PApplet app, ArrayList<PVector> points, float voltage);
//    FieldLine  createFieldLine(PApplet app, PVector start, ArrayList<PointCharge> charges);
}
