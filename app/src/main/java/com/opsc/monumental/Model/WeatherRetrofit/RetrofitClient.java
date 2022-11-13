package com.opsc.monumental.Model.WeatherRetrofit;


import retrofit2.Retrofit;

public class RetrofitClient
{
    private  static Retrofit retrofit;

    public static Retrofit getRetrofit()
    {
        if (retrofit== null)
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .build();
        return retrofit;
    }
}
