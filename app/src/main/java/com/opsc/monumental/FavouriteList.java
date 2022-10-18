package com.opsc.monumental;

public class FavouriteList {
    //model to store and get location detail of favorite landmark

    String locationName;
    String locationAddress;
    String phoneNumber;
    String url;

    public FavouriteList() {

    }

    public FavouriteList(String locationName, String locationAddress, String phoneNumber, String url) {
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.phoneNumber = phoneNumber;
        this.url = url;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUrl() {
        return url;
    }
}
