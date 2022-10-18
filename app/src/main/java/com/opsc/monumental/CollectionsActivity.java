package com.opsc.monumental;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    String userId;

    // The entry point to the Places API.
    private PlacesClient placesClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    ListView list;
    Dialog myDialog;
    ArrayList<FavouriteList> arrList = new ArrayList<>();
    FavouriteAdapter arrAd;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        list = (ListView) findViewById(R.id.statisticsList);
        myDialog = new Dialog(this);

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mAuth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference("Favourites");

        //get user id
        String userID = mAuth.getCurrentUser().getUid();

        //Place.Field List to store user data
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS,Place.Field.PHONE_NUMBER,Place.Field.WEBSITE_URI);

        //set custom array Adapter to list
        arrAd = new FavouriteAdapter(CollectionsActivity.this, R.layout.list_item2, R.id.field_NAME , arrList);
        list.setAdapter(arrAd);

        //get list items
        ref.orderByChild(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String id = snapshot.getValue(Favourite.class).getLocationID();

                if (id != null) {
                    final FetchPlaceRequest request = FetchPlaceRequest.newInstance(id, placeFields);

                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place result = response.getPlace();
                        arrList.add(new FavouriteList(result.getName(), result.getAddress(),
                                result.getPhoneNumber(), ""));

                        arrAd.notifyDataSetChanged();
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            final ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                            final int statusCode = apiException.getStatusCode();
                            // TODO: Handle error with given status code.
                        }
                    });
                    //update list
                    arrAd.notifyDataSetChanged();
                }
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

        //list on long click to be added later

        /*list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                myDialog.setContentView(R.layout.update_delete_window);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();

                FavouriteList favourite = arrList.get(position);

                Button btnUpdate, btnDelete;

                btnDelete = (Button) myDialog.findViewById(R.id.deleteBtn);

                //delete  items selected
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ref = FirebaseDatabase.getInstance().getReference();
                        user = mAuth.getCurrentUser();
                        assert user != null;
                        userId = user.getUid();
                        //delete Wishlist in collection
                        ref.child("Favourite").child(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeValue();

                                list.remove(position);
                                listOfKey.remove(position);
                                arrAd.notifyDataSetChanged();
                                // update list view with new values
                                myDialog.dismiss();

                                LayoutInflater inflater = getLayoutInflater();
                                View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                TextView textView6 = customToastLayout.findViewById(R.id.name);
                                textView6.setText("Collection deleted.");

                                Toast mToast = new Toast(CollectionsActivity.this);
                                mToast.setDuration(Toast.LENGTH_SHORT);
                                mToast.setView(customToastLayout);
                                mToast.show();
                                //Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                myDialog.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                LayoutInflater inflater = getLayoutInflater();
                                View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                TextView textView6 = customToastLayout.findViewById(R.id.name);
                                textView6.setText("Operation failed.");

                                Toast mToast = new Toast(CollectionsActivity.this);
                                mToast.setDuration(Toast.LENGTH_SHORT);
                                mToast.setView(customToastLayout);
                                mToast.show();
                                //Toast.makeText(MyCollectionsActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                                myDialog.dismiss();
                            }
                        });

                    }
                });
                return true;
            }
        });*/


    }
}