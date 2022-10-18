package com.opsc.monumental;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadURL {

    //code attribution
    //title: Current Location and Nearby Places On Map in Android Studio |Java| Android Studio Tutorial
    //author: Learn With Deeksha
    //Date: Feb 22, 2022
    //url: https://www.youtube.com/watch?v=e_YLWSNMfZg

    //get data and return data from url
    //
    //title: Near Me | Firebase, Google Map, Place & Direction API's | Intro & Setup Project Android Java Part(1)
    //author: Muhammad Adnan
    //Date: Mar 6, 2021
    //url" https://www.youtube.com/playlist?list=PLpQFhyCcxiCqDFYQabluYIYsNSsCFMNFk & https://github.com/adnan0786/NearMeJavaApplication

    public String retreiveUrl(String url) throws IOException{
        String urlData = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try
        {
            URL getUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) getUrl.openConnection();
            httpURLConnection.connect();

            inputStream=httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";

            while((line = bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }

            urlData = sb.toString();
            bufferedReader.close();
        }
        catch(Exception e){
            Log.d("Exception", e.toString());
        } finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
      return urlData;
    }

}
