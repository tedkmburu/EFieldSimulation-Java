package org.example.view.ui;

import processing.core.PVector;

public interface ControlPanelListener {
    void onFieldLinesToggled(boolean on);
    void onFieldVectorsToggled(boolean on);
    void onEquipotentialToggled(boolean on);
    void onVoltageToggled(boolean on);
    void onGridToggled(boolean on);
    void onSnapToGridToggled(boolean on);
    void onTestChargeModeToggled(boolean on);
    void onSinglePreset();
    void onDipolePreset();
    void onRowPreset();
    void onDipoleRowPreset();
    void onRandomPreset();
    void onRemoveAllCharges();
    void onCreateTestChargeMap();
    void onClearTestCharges();
//    void onAddPointCharge(PVector position);
    // …add others as needed
}
