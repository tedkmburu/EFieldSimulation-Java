package org.example.view.style;

import java.util.HashMap;
import java.util.Map;

public class StyleFactory {
    private static final Map<StyleKey, Style> cache = new HashMap<>();

    public static Style getStyle(int fillColor,
                                 int strokeColor,
                                 float strokeWeight,
                                 boolean useFill) {
        StyleKey key = new StyleKey(fillColor, strokeColor, strokeWeight, useFill);
        return cache.computeIfAbsent(key,
                k -> new Style(fillColor, strokeColor, strokeWeight, useFill));
    }
}

// A simple key object that implements equals/hashCode on its four fields.
final class StyleKey {
    final int fillColor;
    final int strokeColor;
    final float strokeWeight;
    final boolean useFill;

    // ← you need exactly this
    public StyleKey(int fillColor,
                    int strokeColor,
                    float strokeWeight,
                    boolean useFill) {
        this.fillColor    = fillColor;
        this.strokeColor  = strokeColor;
        this.strokeWeight = strokeWeight;
        this.useFill      = useFill;
    }

    // implement equals() & hashCode() based on all four fields…
}
