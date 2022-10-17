package com.opsc.monumental;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.CamcorderProfile;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.*;

import java.io.IOException;
import java.util.Optional;

public class FetchData extends AsyncTask<Object,String,String> {
    String googleNearbyPlacesData;
    GoogleMap googleMap;
    String url;
    Dialog myDialog;
    FirebaseAuth mAuth;

    @Override
    protected void onPostExecute(String s)
    {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for(int i = 0;i<jsonArray.length();i++){

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONObject getLocation = jsonObject1.getJSONObject("geometry")
                        .getJSONObject("location");

                String lat = getLocation.getString("lat");
                String lng = getLocation.getString("lng");

                JSONObject getName= jsonArray.getJSONObject(i);
                String name = getName.getString("name");

                LatLng latLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name);
                markerOptions.position(latLng);
                googleMap.addMarker(markerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected String doInBackground(Object... objects) {
        try{
            googleMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            DownloadURL downloadURL = new DownloadURL();
            googleNearbyPlacesData = downloadURL.retreiveUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  googleNearbyPlacesData;
    }
}
