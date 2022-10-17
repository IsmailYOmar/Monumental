package com.opsc.monumental;

public class Setting {

    String selectedPreference;
    String selectedSystem;

    public Setting() {

    }

    public Setting(String selectedPreference, String selectedSystem) {
        this.selectedPreference = selectedPreference;
        this.selectedSystem = selectedSystem;
    }

    public String getSelectedPreference() {
        return selectedPreference;
    }

    public String getSelectedSystem() {
        return selectedSystem;
    }
}
