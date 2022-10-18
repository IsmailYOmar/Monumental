package com.opsc.monumental.Model.DirectionPlaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionResponseModel {

    //title: Current Location and Nearby Places On Map in Android Studio |Java| Android Studio Tutorial
    //author: Learn With Deeksha
    //Date: Feb 22, 2022
    //url: https://www.youtube.com/watch?v=e_YLWSNMfZg

    @SerializedName("routes")
    @Expose
    List<DirectionRouteModel> directionRouteModels;


    public List<DirectionRouteModel> getDirectionRouteModels() {
        return directionRouteModels;
    }

    public void setDirectionRouteModels(List<DirectionRouteModel> directionRouteModels) {
        this.directionRouteModels = directionRouteModels;
    }
}
