package com.opsc.monumental;

public class Setting {

    //creating relevant variables
    String selectedPreference;
    String selectedSystem;

    public Setting() {

    }
//setting method to take in and set settings preferences
    public Setting(String selectedPreference, String selectedSystem) {
        this.selectedPreference = selectedPreference;
        this.selectedSystem = selectedSystem;
    }
    //setting gettters

    public String getSelectedPreference() {
        return selectedPreference;
    }

    public String getSelectedSystem() {
        return selectedSystem;
    }
}
