package com.opsc.monumental;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.opsc.monumental.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Button Collections,settings1,settings2;
    EditText search_bar3;
    BottomSheetBehavior bottomSheetBehavior;
    private static final String TAG = "MapsActivity";
    private LocationManager locationManager;
    private Location userCurrentLocation;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View bottomSheet = findViewById(R.id.bottom_sheet1);
        search_bar3= findViewById(R.id.search_bar3);
        settings1= findViewById(R.id.settings1);
        settings2= findViewById(R.id.settings2);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(470);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    settings1.setVisibility(View.VISIBLE);
                    settings2.setVisibility(View.GONE);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    settings1.setVisibility(View.GONE);
                    settings2.setVisibility(View.VISIBLE);
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    settings1.setVisibility(View.GONE);
                    settings2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 103);

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            userCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng myLocation = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());

            mMap.setMyLocationEnabled(true);
            float zoomLevel = 24.0f;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16.0f));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel));
            //mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation,zoomLevel));


        }else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            userCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng myLocation = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());

            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16.0f));
            init();
            //map.setOnMyLocationButtonClickListener(this);
           // map.setOnMyLocationClickListener(this);
        }
    }

    public void init()
    {
        search_bar3.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent)
            {
                if(actionID == EditorInfo.IME_ACTION_SEARCH || actionID == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == keyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    geoLocate();
                }
                return false;
            }
        });
        hideSoftKeyBoard();
    }

    public void geoLocate()
    {
        String searchString = search_bar3.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Toast.makeText(this, "Invalid Search", Toast.LENGTH_LONG)
                    .show();
        }
        if(list.size() > 0)
        {
            Address address = list.get(0);

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 16.0f, address.getAddressLine(0));
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions().position(latLng).title(title);

        mMap.addMarker(options);

        hideSoftKeyBoard();
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onResume()
    {//override page animation on page resume
        super.onResume();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed()
    {//close app on back press
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }

    public void hideSoftKeyBoard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}