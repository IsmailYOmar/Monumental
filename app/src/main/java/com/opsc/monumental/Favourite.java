package com.opsc.monumental;

public class Favourite {

    String userID;

    public Favourite(String userID, String locationID) {
        this.userID = userID;
        this.locationID = locationID;
    }

    public String getUserID() {
        return userID;
    }

    String locationID;

    public Favourite() {

    }


    public String getLocationID() {
        return locationID;
    }
}
