package org.example.view.ui;

import processing.core.PVector;

public interface ControlPanelListener {
    void onFieldLinesToggled(Boolean on);
    void onFieldVectorsToggled(Boolean on);
    void onEquipotentialToggled(Boolean on);
    void onVoltageToggled(Boolean on);
    void onGridToggled(Boolean on);
    void onSnapToGridToggled(Boolean on);
    void onTestChargeModeToggled(Boolean on);
    void onSinglePreset();
    void onDipolePreset();
    void onRowPreset();
    void onDipoleRowPreset();
    void onRandomPreset();
    void onRemoveAllCharges();
    void onCreateTestChargeMap();
    void onClearTestCharges();
}
