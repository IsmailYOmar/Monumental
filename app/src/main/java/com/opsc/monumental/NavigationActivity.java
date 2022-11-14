package com.opsc.monumental;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.opsc.monumental.Model.DirectionPlaceModel.DirectionLegModel;
import com.opsc.monumental.Model.DirectionPlaceModel.DirectionResponseModel;
import com.opsc.monumental.Model.DirectionPlaceModel.DirectionRouteModel;
import com.opsc.monumental.Model.DirectionPlaceModel.DirectionStepModel;
import com.opsc.monumental.databinding.ActivityNavigationBinding;
import com.opsc.monumental.databinding.BottomSheetBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {

    //draw path to destination and display distance and eta
    //
    //title: Current Location and Nearby Places On Map in Android Studio |Java| Android Studio Tutorial
    //author: Learn With Deeksha
    //Date: Feb 22, 2022
    //url: https://www.youtube.com/watch?v=e_YLWSNMfZg

    //title: Google api documentation
    //author: Google
    //url: https://developers.google.com/maps/documentation/android-sdk/get-api-key
    //url: https://developers.google.com/maps/documentation/directions


    //creating relevant variables
        FirebaseAuth mAuth;
        private ActivityNavigationBinding binding;
        private GoogleMap mGoogleMap;
        private boolean  isTrafficEnable;
        private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
        private BottomSheetBinding bottomSheetLayoutBinding;
        private RetrofitAPI retrofitAPI;
        private Location currentLocation;
        private Double endLat, endLng;
        private String placeId;
        private int currentMode;
        private DirectionStepAdapter adapter;

        private PlacesClient placesClient;
        // The entry point to the Fused Location Provider.
        private FusedLocationProviderClient fusedLocationProviderClient;

        private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
        private boolean locationPermissionGranted;
        private static final int DEFAULT_ZOOM = 16;
        // The geographical location where the device is currently located. That is, the last-known
        // location retrieved by the Fused Location Provider.
        private Location lastKnownLocation;
        DatabaseReference ref;
        String units ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        endLat = getIntent().getDoubleExtra("lat", 0.0);
        endLng = getIntent().getDoubleExtra("lng", 0.0);
        placeId = getIntent().getStringExtra("placeId");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();

        //get user defined units
        ref = FirebaseDatabase.getInstance().getReference("Settings");
        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Setting set = snapshot.getValue(Setting.class);

                if(set != null) {
                    units= set.selectedSystem;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);

        bottomSheetLayoutBinding = binding.bottomSheet;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.getRoot());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        adapter = new DirectionStepAdapter();

        bottomSheetLayoutBinding.stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomSheetLayoutBinding.stepRecyclerView.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.directionMap);

        mapFragment.getMapAsync(this);

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        binding.enableTraffic.setOnClickListener(view -> {
            if (isTrafficEnable) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                    getDeviceLocation();
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(true);
                    getDeviceLocation();
                    isTrafficEnable = true;

                }
            }
        });

        binding.travelMode.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    switch (checkedId) {
                        case R.id.btnChipDriving:
                            getDirection("driving", units);
                            break;
                        case R.id.btnChipWalking:
                            getDirection("walking", units);
                            break;
                        case R.id.btnChipBike:
                            getDirection("bicycling", units);
                            break;
                        case R.id.btnChipTrain:
                            getDirection("transit", units);
                            break;
                    }
                }
            }
        });

    }

    private void getDirection(String mode, String units) {

        //create get request to directions api
        if (locationPermissionGranted) {
            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude() +
                    "&destination=" + endLat + "," + endLng +
                    "&mode=" + mode +
                    "&units="+ units.toLowerCase() +
                    "&key="+ BuildConfig.NEARBY_API_KEY;

            //past json data to fragments
            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                @Override
                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
                    Gson gson = new Gson();
                    String res = gson.toJson(response.body());
                    Log.d("TAG", "onResponse: " + res);

                    if (response.errorBody() == null) {
                        if (response.body() != null) {
                            clearUI();

                            if (response.body().getDirectionRouteModels().size() > 0) {
                                DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);

                                //getSupportActionBar().setTitle(routeModel.getSummary());

                                DirectionLegModel legModel = routeModel.getLegs().get(0);
                                binding.txtStartLocation.setText(legModel.getStartAddress());
                                binding.txtEndLocation.setText(legModel.getEndAddress());

                                bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());
                                bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());


                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng()))
                                        .title("End Location"));

                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng()))
                                        .title("Start Location"));

                                adapter.setDirectionStepModels(legModel.getSteps());


                                List<LatLng> stepList = new ArrayList<>();

                                PolylineOptions options = new PolylineOptions()
                                        .width(25)
                                        .color(Color.BLUE)
                                        .geodesic(true)
                                        .clickable(true)
                                        .visible(true);

                                List<PatternItem> pattern;
                                if (mode.equals("walking")) {
                                    pattern = Arrays.asList(
                                            new Dot(), new Gap(10));

                                    options.jointType(JointType.ROUND);
                                } else {
                                    pattern = Arrays.asList(
                                            new Dash(30));
                                }

                                options.pattern(pattern);

                                for (DirectionStepModel stepModel : legModel.getSteps()) {
                                    List<com.google.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
                                    for (com.google.maps.model.LatLng latLng : decodedLatLng) {
                                        stepList.add(new LatLng(latLng.lat, latLng.lng));
                                    }
                                }

                                options.addAll(stepList);

                                Polyline polyline = mGoogleMap.addPolyline(options);

                                LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
                                LatLng endLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());


                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 14));

                            } else {
                                Toast.makeText(NavigationActivity.this, "No route found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(NavigationActivity.this, "No route found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("TAG", "onResponse: " + response);
                    }

                }

                @Override
                public void onFailure(Call<DirectionResponseModel> call, Throwable t) {

                }
            });
        }

    }

    private void clearUI() {

        mGoogleMap.clear();
        binding.txtStartLocation.setText("");
        binding.txtEndLocation.setText("");
        bottomSheetLayoutBinding.txtSheetDistance.setText("");
        bottomSheetLayoutBinding.txtSheetTime.setText("");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mGoogleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        //check permission and move map camera to current location

        updateLocationUI();
        getDeviceLocation();


    }


    //check location permission and move map camera to current location
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
        } else {
            ActivityCompat.requestPermissions(NavigationActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mGoogleMap.setMyLocationEnabled(true);
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                getDirection("driving" , units);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mGoogleMap.animateCamera(CameraUpdateFactory
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
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted ) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                getDeviceLocation();
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();
    }

    //decode data from List<com.google.maps.model.LatLng>
    private List<com.google.maps.model.LatLng> decode(String points) {

        int len = points.length();

        final List<com.google.maps.model.LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new com.google.maps.model.LatLng(lat * 1e-5, lng * 1e-5));
        }

        //return path to destination
        return path;

    }
}