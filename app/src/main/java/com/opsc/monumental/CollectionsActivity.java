package com.opsc.monumental;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    // The entry point to the Places API.
    private PlacesClient placesClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    ListView list;

    ArrayList<FavouriteList> arrayList;

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        list = (ListView) findViewById(R.id.statisticsList);

        arrayList = new ArrayList<FavouriteList>();

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mAuth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference("Favourites");

        String userID = mAuth.getCurrentUser().getUid();

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item2, arrayList);

        ref.child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Favourite fav = snapshot.getValue(Favourite.class);

                String id = fav.locationID;

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                        Place.Field.ADDRESS,Place.Field.PHONE_NUMBER,Place.Field.WEBSITE_URI);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}