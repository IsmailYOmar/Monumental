package com.opsc.monumental;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    //creating relevant variables
    private static Retrofit retrofit = null;
    public static final String BASE_URL = "https://maps.googleapis.com";

    //method to set and return retrofit
    public static Retrofit getRetrofitClient() {
        //if statement that runs if retrofit has not been set with a base URL
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
