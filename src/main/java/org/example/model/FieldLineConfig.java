package org.example.model;

public class FieldLineConfig {
    private final Integer    maxLoops;
    private final Float  stepSize;
    private final Integer    arrowInterval;

    public FieldLineConfig(Integer maxLoops, Float stepSize, Integer arrowInterval) {
        this.maxLoops      = maxLoops;
        this.stepSize      = stepSize;
        this.arrowInterval = arrowInterval;
    }

    public Integer getMaxLoops() { return maxLoops; }
    public Float getStepSize() { return stepSize; }
    public Integer getArrowInterval() { return arrowInterval; }
}