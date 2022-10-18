package com.opsc.monumental;

import android.net.Uri;

public class FavouriteList {

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
