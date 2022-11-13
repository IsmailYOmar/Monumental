package com.opsc.monumental.Model.WeatherRetrofit;


import com.opsc.monumental.Model.WeatherModel.WeatherResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface IOpenWeather
{

    @GET
    Call<WeatherResult> getWeatherLatAndLon(@Url String url);
}
