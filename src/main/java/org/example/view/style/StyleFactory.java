package org.example.view.style;

import java.util.HashMap;
import java.util.Map;

public class StyleFactory {
    private static final Map<StyleKey, Style> cache = new HashMap<>();

    public static Style getStyle(Integer fillColor,
                                 Integer strokeColor,
                                 Float strokeWeight,
                                 Boolean useFill) {
        StyleKey key = new StyleKey(fillColor, strokeColor, strokeWeight, useFill);
        return cache.computeIfAbsent(key,
                k -> new Style(fillColor, strokeColor, strokeWeight, useFill));
    }
}


final class StyleKey {
    final Integer fillColor;
    final Integer strokeColor;
    final Float strokeWeight;
    final Boolean useFill;

    // ← you need exactly this
    public StyleKey(Integer fillColor,
                    Integer strokeColor,
                    Float strokeWeight,
                    Boolean useFill) {
        this.fillColor    = fillColor;
        this.strokeColor  = strokeColor;
        this.strokeWeight = strokeWeight;
        this.useFill      = useFill;
    }

}
