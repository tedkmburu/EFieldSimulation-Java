package org.example.model;

import org.example.model.config.ConfigManager;
import org.example.view.style.Style;
import org.example.view.style.StyleFactory;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;


public class PointCharge extends Charge {
    public Boolean selected = false;
    public Boolean dragging = false;
    private Integer diameter;

    public PointCharge(PVector position, Float charge) {
        super(position, charge);

        this.diameter = ConfigManager.getInstance().getChargeDiameter();
    }

    private static final Style POS_STYLE = StyleFactory.getStyle(0xFFD2292D, 0xFFFFFFFF, 1f, true);
    private static final Style NEG_STYLE = StyleFactory.getStyle(0xFF1761B0, 0xFFFFFFFF, 1f, true);
    private static final Style NEU_STYLE = StyleFactory.getStyle(0xFF555555, 0xFFFFFFFF, 1f, true);
    private static final Style SELECTED_BORDER = StyleFactory.getStyle(0, 0xFFFFFFFF, 2f, false);
    private static final Style NORMAL_BORDER = StyleFactory.getStyle(0, 0x80000000, 1f, false);

    @Override
    public void display(PApplet app) {
        // fill
        Style fillStyle = charge > 0 ? POS_STYLE : charge < 0 ? NEG_STYLE : NEU_STYLE;
        fillStyle.apply(app);

        // border
        Style border = selected ? SELECTED_BORDER : NORMAL_BORDER;
        border.apply(app);
        app.ellipse(position.x, position.y, this.diameter, this.diameter);

        // white text for the number
        app.pushStyle();
        app.fill(255);
        app.textAlign(PConstants.CENTER, PConstants.CENTER);
        app.text(charge.intValue(), position.x, position.y);                   // draw the number
        app.popStyle();
    }

    public void select() {
        this.selected = true;
    }

    public void increaseCharge() { this.charge += ConfigManager.getInstance().getChargeIncrementDelta(); }

    public void decreaseCharge() {
        this.charge -= ConfigManager.getInstance().getChargeIncrementDelta();
    }
}
