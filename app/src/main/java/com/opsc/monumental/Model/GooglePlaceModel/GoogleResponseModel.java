package com.opsc.monumental.Model.GooglePlaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.opsc.monumental.GooglePlaceModel;

import java.util.List;

public class GoogleResponseModel {

    @SerializedName("results")
    @Expose
    private List<GooglePlaceModel> googlePlaceModelList;

    @SerializedName("error_message")
    @Expose
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<GooglePlaceModel> getGooglePlaceModelList() {
        return googlePlaceModelList;
    }

    public void setGooglePlaceModelList(List<GooglePlaceModel> googlePlaceModelList) {
        this.googlePlaceModelList = googlePlaceModelList;
    }
}
