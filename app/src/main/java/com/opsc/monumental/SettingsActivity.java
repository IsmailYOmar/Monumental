package com.opsc.monumental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //creating relevant variables
    TextView username;
    Button signOut, saveBtn, cancelBtn;
    Spinner landmarkTypes;
    Switch toggleMetric;
    Switch toggleImperial;

    String newSystem;

    FirebaseAuth mAuth;
    FirebaseAuth mAuth2;
    FirebaseAuth mAuth3;
    DatabaseReference ref;
    DatabaseReference ref2;
    DatabaseReference ref3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //initializing relevant variables
        mAuth = FirebaseAuth.getInstance();

        mAuth2 = FirebaseAuth.getInstance();

        mAuth3 = FirebaseAuth.getInstance();

        username = (TextView) findViewById(R.id.username);

        String userID = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("Users");

        //creating a listener to check for changes to user settings
        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //assigning user value retreived from User class
                User fullName = snapshot.getValue(User.class);

                if(fullName != null) {
                    String fn = fullName.firstName;
                    String ln = fullName.lastName;

                    //setting
                    username.setText(fn + " " + ln);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //style and give data to drop down menu
        landmarkTypes = (Spinner) findViewById(R.id.landmarkSelect);

        ArrayAdapter<CharSequence> ar = ArrayAdapter.createFromResource(
                this,
                R.array.Landmark_Types,
                android.R.layout.simple_spinner_item
        );

        ar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        landmarkTypes.setAdapter(ar);

        //get users landmark preference
        ref2 = FirebaseDatabase.getInstance().getReference("Settings");
        ref2.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Setting set = snapshot.getValue(Setting.class);

                if(set != null) {
                    String preference = set.selectedPreference;
                    int position = ar.getPosition(preference);
                    landmarkTypes.setSelection(position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        landmarkTypes.setOnItemSelectedListener(this);

        toggleMetric = (Switch) findViewById(R.id.toggleMetric);

        toggleImperial = (Switch) findViewById(R.id.toggleImperial);

        //changes all details displayed to either the metric system or imperial according to users preference
        toggleMetric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleMetric.isChecked()){
                    newSystem = "Metric";
                    toggleImperial.setChecked(false);
                }else if (!toggleMetric.isChecked()) {
                    newSystem = "Imperial";
                    toggleImperial.setChecked(true);
                }

            }
        });
        //changes all details displayed to either the metric system or imperial according to users preference

        toggleImperial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleImperial.isChecked()){
                    newSystem = "Imperial";
                    toggleMetric.setChecked(false);
                }else if (!toggleImperial.isChecked()) {
                    newSystem = "Metric";
                    toggleMetric.setChecked(true);
                }

            }
        });

        //get users landmark preferences
        ref2 = FirebaseDatabase.getInstance().getReference("Settings");

        //when page is initially loaded this method will set users saved setting preferences
        ref2.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Setting set = snapshot.getValue(Setting.class);

                if(set != null) {
                    String system = set.selectedSystem;

                    if(system.equals("Metric")) {
                        toggleMetric.setChecked(true);
                        toggleImperial.setChecked(false);
                    }
                    else if(system.equals("Imperial")) {
                        toggleMetric.setChecked(false);
                        toggleImperial.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveBtn = (Button) findViewById(R.id.save);

        //button click which will save all users setting preferences to the database
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPreference = landmarkTypes.getSelectedItem().toString();
                String userID4 = mAuth3.getCurrentUser().getUid();
                ref3 = FirebaseDatabase.getInstance().getReference("Settings").child(userID4);
                HashMap hashMap = new HashMap();
                hashMap.put("selectedPreference", newPreference);
                hashMap.put("selectedSystem", newSystem);

                ref3.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Settings updated.", Toast.LENGTH_LONG).show();
                            startActivity( new Intent(SettingsActivity.this, MapsActivity.class));
                        }
                        else {
                            Toast.makeText(SettingsActivity.this, "Update failed. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        cancelBtn = (Button) findViewById(R.id.cancel);

        //button on click to close settings page without saving any changes

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(SettingsActivity.this, MapsActivity.class));
            }
        });

        signOut = (Button) findViewById(R.id.signOut);

        //button on click to sign user out

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String select = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}