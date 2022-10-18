package com.opsc.monumental;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opsc.monumental.databinding.ActivityMapsBinding;

import java.util.Arrays;
import java.util.List;

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
    private String API = "&key=AIzaSyDZ2EP0ZUjVnNSp846Vifwsm2qBwYUjvU8";

    private ActivityMapsBinding binding;
    Button collections,settings1,settings2,directions,restaurant,bank,park,mall,gps,pins;
    LinearLayout list;
    androidx.appcompat.widget.SearchView searchView;
    BottomSheetBehavior bottomSheetBehavior;
    private static final String TAG = "MapsActivity";
    private String user_pref ;
    private LocationManager locationManager;
    private Location userCurrentLocation;
    private LocationListener locationListener;

    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private static final int DEFAULT_ZOOM = 16;
    private static final int Request_code = 101;
    DatabaseReference ref;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        String userID = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("Settings");
        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Setting set = snapshot.getValue(Setting.class);

                if(set != null) {
                    user_pref= set.selectedPreference;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        collections = (Button) findViewById(R.id.Collections);

        collections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(MapsActivity.this, CollectionsActivity.class));
            }
        });

        directions = findViewById(R.id.directions);
        settings1= (Button) findViewById(R.id.settings1);
        restaurant = (Button) findViewById(R.id.restaurants);
        mall = (Button) findViewById(R.id.malls);
        park = (Button) findViewById(R.id.parks);
        bank = (Button) findViewById(R.id.banks);

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

        gps= (Button) findViewById(R.id.gps);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDeviceLocation();
            }
        });
        pins= (Button) findViewById(R.id.pins);
        pins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
                stringBuilder.append("&radius=1000");

                //user settings

                //
                //
                // "&type="+ user_pref

                stringBuilder.append("&type="+ user_pref);
                stringBuilder.append("&sensor=true");
                stringBuilder.append(API);

                String url = stringBuilder.toString();
                Object dataFetch[]= new Object[2];
                dataFetch[0] = mMap;
                dataFetch[1] = url;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataFetch);
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(result.getLatLng(),19));
                    String location = result.getName().toString();
                    Marker mMarker = mMap.addMarker(new MarkerOptions().position(result.getLatLng()).title(location));
                    //code landmark details here

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @SuppressLint("PotentialBehaviorOverride")
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {

                            marker.hideInfoWindow();

                            myDialog.setContentView(R.layout.landmark_details);
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(myDialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.gravity = Gravity.BOTTOM;
                            myDialog.getWindow().setAttributes(lp);
                            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            myDialog.show();

                            Button favBtn,directionsBtn;
                            String favID = "";
                            String locationID = place.getId();
                            Button btnClose;
                            TextView field_NAME, field_ADDRESS,field_PHONE_NUMBER,field_RATING,
                                    field_WEBSITE_URI,field_BUSINESS_STATUS,field_RATING_total;

                            favBtn = (Button) myDialog.findViewById(R.id.favouriteBtn);
                            directionsBtn = (Button) myDialog.findViewById(R.id.directionsBtn);
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
                                    Favourite add = new Favourite(mAuth.getCurrentUser().getUid(),locationID);
                                    FirebaseDatabase.getInstance().getReference("Favourites").child(favID).push()
                                            .setValue(add).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                            directionsBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String placeId = place.getId();
                                    Double lat = result.getLatLng().latitude;
                                    Double lng = result.getLatLng().longitude;

                                    replaceFragment( new Fragment());


                                    Intent intent = new Intent(MapsActivity.this, NavigationActivity.class);
                                    intent.putExtra("placeId", placeId);
                                    intent.putExtra("lat", marker.getPosition().latitude);
                                    intent.putExtra("lng", marker.getPosition().longitude);

                                    startActivity(intent);
                                }
                            });

                            btnClose.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    myDialog.dismiss();
                                }
                            });
                            return true;
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

        directions = findViewById(R.id.directions);

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

            Button btnClose;
            btnClose = (Button) myDialog.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                    mMap.clear();
                }
            });
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Prompt the user for permission.
        mMap = map;


        updateLocationUI();
        getDeviceLocation();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint("PotentialBehaviorOverride")
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                List<Place.Field> placeFields2 = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.USER_RATINGS_TOTAL,
                        Place.Field.LAT_LNG, Place.Field.ADDRESS,Place.Field.PHONE_NUMBER,Place.Field.RATING,Place.Field.WEBSITE_URI,
                        Place.Field.OPENING_HOURS,Place.Field.BUSINESS_STATUS,Place.Field.TYPES,Place.Field.UTC_OFFSET);

                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(marker.getTitle(), placeFields2);
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place result2 = response.getPlace();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(result2.getLatLng(),19));
                    String location = result2.getName();
                    mMap.addMarker(new MarkerOptions().position(result2.getLatLng()).title(location));
                    //code landmark details here


                myDialog.setContentView(R.layout.landmark_details);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(myDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM;
                myDialog.getWindow().setAttributes(lp);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();

                Button favBtn,directionsBtn;;
                String favID = "";
                String locationID = result2.getId();
                Button btnClose;
                TextView field_NAME, field_ADDRESS,field_PHONE_NUMBER,field_RATING,
                        field_WEBSITE_URI,field_BUSINESS_STATUS,field_RATING_total;

                favBtn = (Button) myDialog.findViewById(R.id.favouriteBtn);
                btnClose = (Button) myDialog.findViewById(R.id.btnClose);
                directionsBtn = (Button) myDialog.findViewById(R.id.directionsBtn);

                field_NAME = (TextView) myDialog.findViewById(R.id.field_NAME);
                field_PHONE_NUMBER = (TextView) myDialog.findViewById(R.id.field_PHONE_NUMBER);
                field_ADDRESS= (TextView) myDialog.findViewById(R.id.field_ADDRESS);
                field_RATING = (TextView) myDialog.findViewById(R.id.field_RATING);
                field_RATING_total = (TextView) myDialog.findViewById(R.id.field_RATING_total);
                field_WEBSITE_URI = (TextView) myDialog.findViewById(R.id.field_WEBSITE_URI);
                field_BUSINESS_STATUS = (TextView) myDialog.findViewById(R.id.field_BUSINESS_STATUS);

                field_NAME.setText(result2.getName());
                field_PHONE_NUMBER.setText(result2.getPhoneNumber());
                field_ADDRESS.setText(result2.getAddress());
                String rating = String.valueOf(result2.getRating());
                String rating_total =  String.valueOf(result2.getUserRatingsTotal());
                field_RATING.setText(rating);
                field_RATING_total.setText("(" +rating_total +")");
                field_WEBSITE_URI.setText(String.valueOf(result2.getWebsiteUri()));


                if(result2.isOpen() == null ){
                    field_BUSINESS_STATUS.setText("");
                }else if (result2.isOpen() == false){
                    field_BUSINESS_STATUS.setText("Closed");
                }else{
                    field_BUSINESS_STATUS.setText("Open");
                }

                favBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Favourite add = new Favourite(mAuth.getCurrentUser().getUid(),locationID);
                        FirebaseDatabase.getInstance().getReference("Favourites").child(favID).push()
                                .setValue(add).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    directionsBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String placeId = locationID;
                            Double lat = result2.getLatLng().latitude;
                            Double lng = result2.getLatLng().longitude;

                            replaceFragment( new Fragment());


                            Intent intent = new Intent(MapsActivity.this, NavigationActivity.class);
                            intent.putExtra("placeId", placeId);
                            intent.putExtra("lat", marker.getPosition().latitude);
                            intent.putExtra("lng", marker.getPosition().longitude);

                            startActivity(intent);
                        }
                    });

                btnClose.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        myDialog.dismiss();
                    }
                });
                });
                return true;
            }
        });

/*
        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest)
            {

                List<Place.Field> placeFields1 = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.USER_RATINGS_TOTAL,
                        Place.Field.LAT_LNG, Place.Field.ADDRESS,Place.Field.PHONE_NUMBER,Place.Field.RATING,Place.Field.WEBSITE_URI,
                        Place.Field.OPENING_HOURS,Place.Field.BUSINESS_STATUS,Place.Field.TYPES,Place.Field.UTC_OFFSET);

                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(pointOfInterest.placeId, placeFields1);
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place result3 = response.getPlace();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(result3.getLatLng(),DEFAULT_ZOOM));
                    String location = result3.getName().toString();
                    mMap.addMarker(new MarkerOptions().position(result3.getLatLng()).title(location));
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
                            String locationID = result3.getId();
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

                            field_NAME.setText(result3.getName());
                            field_PHONE_NUMBER.setText(result3.getPhoneNumber());
                            field_ADDRESS.setText(result3.getAddress());
                            String rating = String.valueOf(result3.getRating());
                            String rating_total =  String.valueOf(result3.getUserRatingsTotal());
                            field_RATING.setText(rating);
                            field_RATING_total.setText("(" +rating_total +")");
                            field_WEBSITE_URI.setText(String.valueOf(result3.getWebsiteUri()));


                            if(result3.isOpen() == null ){
                                field_BUSINESS_STATUS.setText("");
                            }else if (result3.isOpen() == false){
                                field_BUSINESS_STATUS.setText("Closed");
                            }else{
                                field_BUSINESS_STATUS.setText("Open");
                            }

                            favBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Favourite add = new Favourite(mAuth.getCurrentUser().getUid(),locationID);
                                    FirebaseDatabase.getInstance().getReference("Favourites").child(favID).push()
                                            .setValue(add).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                    mMap.clear();
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
        });
*/

        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=bank");
                stringBuilder.append("&sensor=true");
                stringBuilder.append(API);

                String url = stringBuilder.toString();
                Object dataFetch[]= new Object[2];
                dataFetch[0] = mMap;
                dataFetch[1] = url;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataFetch);

            }
        });

        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=restaurant");
                stringBuilder.append("&sensor=true");
                stringBuilder.append(API);

                String url = stringBuilder.toString();
                Object dataFetch[]= new Object[2];
                dataFetch[0] = mMap;
                dataFetch[1] = url;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataFetch);

            }
        });

        mall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=shopping_mall");
                stringBuilder.append("&sensor=true");
                stringBuilder.append(API);

                String url = stringBuilder.toString();
                Object dataFetch[]= new Object[2];
                dataFetch[0] = mMap;
                dataFetch[1] = url;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataFetch);

            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=park");
                stringBuilder.append("&sensor=true");
                stringBuilder.append(API);

                String url = stringBuilder.toString();
                Object dataFetch[]= new Object[2];
                dataFetch[0] = mMap;
                dataFetch[1] = url;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataFetch);



            }
        });

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
            ActivityCompat.requestPermissions(MapsActivity.this,
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
                                mMap.setMyLocationEnabled(true);
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
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getDeviceLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}