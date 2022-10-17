package com.opsc.monumental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView username;
    Button signOut, saveBtn;
    Spinner landmarkTypes;
    Switch toggleMetric;
    Switch toggleImperial;

    FirebaseAuth mAuth;
    FirebaseAuth mAuth2;
    DatabaseReference ref;
    DatabaseReference ref2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        mAuth2 = FirebaseAuth.getInstance();

        username = (TextView) findViewById(R.id.username);

        String userID = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User fullName = snapshot.getValue(User.class);

                if(fullName != null) {
                    String fn = fullName.firstName;
                    String ln = fullName.lastName;

                    username.setText(fn + " " + ln);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        landmarkTypes = (Spinner) findViewById(R.id.landmarkSelect);

        ArrayAdapter<CharSequence> ar = ArrayAdapter.createFromResource(
                this,
                R.array.Landmark_Types,
                android.R.layout.simple_spinner_item
        );

        ar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        landmarkTypes.setAdapter(ar);

        landmarkTypes.setOnItemSelectedListener(this);

        toggleMetric = (Switch) findViewById(R.id.toggleMetric);

        toggleImperial = (Switch) findViewById(R.id.toggleImperial);

        String userID2 = mAuth2.getCurrentUser().getUid();
        ref2 = FirebaseDatabase.getInstance().getReference("Settings");

        ref2.child(userID2).addListenerForSingleValueEvent(new ValueEventListener() {
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

        signOut = (Button) findViewById(R.id.signOut);

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