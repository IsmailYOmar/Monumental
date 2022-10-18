package com.opsc.monumental;

import com.opsc.monumental.Model.DirectionPlaceModel.DirectionResponseModel;
import com.opsc.monumental.Model.GooglePlaceModel.GoogleResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
//creating an interface to get relevant APIs
public interface RetrofitAPI {

    @GET
    Call<GoogleResponseModel> getNearByPlaces(@Url String url);

    @GET
    Call<DirectionResponseModel> getDirection(@Url String url);
}