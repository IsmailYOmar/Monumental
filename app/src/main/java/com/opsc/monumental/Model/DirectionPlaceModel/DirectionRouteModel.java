package com.opsc.monumental.Model.DirectionPlaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionRouteModel {

    //title: Current Location and Nearby Places On Map in Android Studio |Java| Android Studio Tutorial
    //author: Learn With Deeksha
    //Date: Feb 22, 2022
    //url: https://www.youtube.com/watch?v=e_YLWSNMfZg

    @SerializedName("legs")
    @Expose
    private List<DirectionLegModel> legs;

    @SerializedName("overview_polyline")
    @Expose
    private DirectionPolylineModel polylineModel;

    @SerializedName("summary")
    @Expose
    private String summary;

    public List<DirectionLegModel> getLegs() {
        return legs;
    }

    public void setLegs(List<DirectionLegModel> legs) {
        this.legs = legs;
    }

    public DirectionPolylineModel getPolylineModel() {
        return polylineModel;
    }

    public void setPolylineModel(DirectionPolylineModel polylineModel) {
        this.polylineModel = polylineModel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
