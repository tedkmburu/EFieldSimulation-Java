package org.example.model;

public class FieldLineConfig {
    private final int    maxLoops;
    private final float  stepSize;
    private final int    arrowInterval;

    public FieldLineConfig(int maxLoops, float stepSize, int arrowInterval) {
        this.maxLoops      = maxLoops;
        this.stepSize      = stepSize;
        this.arrowInterval = arrowInterval;
    }

    public int getMaxLoops()          { return maxLoops; }
    public float getStepSize()        { return stepSize; }
    public int getArrowInterval()     { return arrowInterval; }
}