package com.opsc.monumental.Model.GooglePlaceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoModel {

    //title: Current Location and Nearby Places On Map in Android Studio |Java| Android Studio Tutorial
    //author: Learn With Deeksha
    //Date: Feb 22, 2022
    //url: https://www.youtube.com/watch?v=e_YLWSNMfZg

    @SerializedName("height")
    @Expose
    private Integer height;

    @SerializedName("html_attributions")
    @Expose
    private List<String> htmlAttributions = null;

    @SerializedName("photo_reference")
    @Expose
    private String photoReference;

    @SerializedName("width")
    @Expose
    private Integer width;

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }


}
