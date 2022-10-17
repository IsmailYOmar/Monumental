package com.opsc.monumental;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.opsc.monumental.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    FirebaseAuth mAuth;

    Dialog myDialog;
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    // The entry point to the Places API.
    private PlacesClient placesClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;


    private ActivityMapsBinding binding;
    Button Collections,settings1,settings2;
    MaterialButton directions;
    LinearLayout list;
    androidx.appcompat.widget.SearchView searchView;
    BottomSheetBehavior bottomSheetBehavior;
    private static final String TAG = "MapsActivity";
    private LocationManager locationManager;
    private Location userCurrentLocation;
    private LocationListener locationListener;

    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private static final int DEFAULT_ZOOM = 16;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        View bottomSheet = findViewById(R.id.bottom_sheet1);
        //searchView= findViewById(R.id.search_bar3);
        settings1= (Button) findViewById(R.id.settings1);
        settings2= (Button) findViewById(R.id.settings2);
        directions = findViewById(R.id.directions);
        settings1= (Button) findViewById(R.id.settings1);

        settings1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, SettingsActivity.class));
            }
        });

        settings2= (Button) findViewById(R.id.settings2);

        settings2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, SettingsActivity.class));
            }
        });

        list= findViewById(R.id.list);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(480);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    settings1.setVisibility(View.VISIBLE);
                    settings2.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    settings1.setVisibility(View.GONE);
                    settings2.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    settings1.setVisibility(View.GONE);
                    settings2.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        myDialog = new Dialog(this);

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.USER_RATINGS_TOTAL,
                Place.Field.LAT_LNG, Place.Field.ADDRESS,Place.Field.PHONE_NUMBER,Place.Field.RATING,Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS,Place.Field.BUSINESS_STATUS,Place.Field.TYPES,Place.Field.UTC_OFFSET);


        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.search_bar3);


        autocompleteFragment.setHint("Where to?");
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(place.getId(), placeFields);

                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place result = response.getPlace();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(result.getLatLng(),DEFAULT_ZOOM));
                    String location = result.getName().toString();
                    mMap.addMarker(new MarkerOptions().position(result.getLatLng()).title(location));
                    //code landmark details here

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            myDialog.setContentView(R.layout.landmark_details);
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(myDialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.gravity = Gravity.BOTTOM;
                            myDialog.getWindow().setAttributes(lp);
                            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            myDialog.show();

                            Button favBtn;
                            String favID = "";
                            String locationID = place.getId();
                            Button btnClose;
                            TextView field_NAME, field_ADDRESS,field_PHONE_NUMBER,field_RATING,
                                    field_WEBSITE_URI,field_BUSINESS_STATUS,field_RATING_total;

                            favBtn = (Button) myDialog.findViewById(R.id.favouriteBtn);
                            btnClose = (Button) myDialog.findViewById(R.id.btnClose);


                            field_NAME = (TextView) myDialog.findViewById(R.id.field_NAME);
                            field_PHONE_NUMBER = (TextView) myDialog.findViewById(R.id.field_PHONE_NUMBER);
                            field_ADDRESS= (TextView) myDialog.findViewById(R.id.field_ADDRESS);
                            field_RATING = (TextView) myDialog.findViewById(R.id.field_RATING);
                            field_RATING_total = (TextView) myDialog.findViewById(R.id.field_RATING_total);
                            field_WEBSITE_URI = (TextView) myDialog.findViewById(R.id.field_WEBSITE_URI);
                            field_BUSINESS_STATUS = (TextView) myDialog.findViewById(R.id.field_BUSINESS_STATUS);

                            field_NAME.setText(result.getName());
                            field_PHONE_NUMBER.setText(result.getPhoneNumber());
                            field_ADDRESS.setText(result.getAddress());
                            String rating = String.valueOf(result.getRating());
                            String rating_total =  String.valueOf(result.getUserRatingsTotal());
                            field_RATING.setText(rating);
                            field_RATING_total.setText("(" +rating_total +")");
                            field_WEBSITE_URI.setText(String.valueOf(result.getWebsiteUri()));


                            if(result.isOpen() == null ){
                                field_BUSINESS_STATUS.setText("");
                            }else if (result.isOpen() == false){
                                field_BUSINESS_STATUS.setText("Closed");
                            }else{
                                field_BUSINESS_STATUS.setText("Open");
                            }

                            favBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Favourite add = new Favourite(locationID);
                                    FirebaseDatabase.getInstance().getReference("Favourites").child(favID).push().child("UserID: " + mAuth.getCurrentUser().getUid()).setValue(add).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(MapsActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(MapsActivity.this, "Unsuccessful.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });

                            btnClose.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    myDialog.dismiss();
                                }
                            });
                            return false;
                        }


                    });

                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        // TODO: Handle error with given status code.
                    }
                });
                //LatLng latLng = new LatLng(place.getLatitude(),address.getLongitude());
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        directions.setOnClickListener(v -> {
            myDialog.setContentView(R.layout.activity_directions);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(myDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            myDialog.getWindow().setAttributes(lp);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Prompt the user for permission.
        mMap = map;
        updateLocationUI();

        getDeviceLocation();
        //map.setOnMyLocationButtonClickListener(this);
       // map.setOnMyLocationClickListener(this);

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


    public void hideSoftKeyBoard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            updateLocationUI();

            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted ) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
                updateLocationUI();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}