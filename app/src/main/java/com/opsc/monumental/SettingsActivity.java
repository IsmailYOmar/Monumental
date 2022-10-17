package com.opsc.monumental;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    Button signOut;
    Spinner landmarkTypes;
    Switch toggleMetric;
    Switch toggleImperial;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        landmarkTypes = (Spinner) findViewById(R.id.landmarkSelect);
        ArrayAdapter<CharSequence> ar = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.Landmark_Types, R.layout.activity_settings);

        toggleMetric = (Switch) findViewById(R.id.toggleMetric);
        toggleImperial = (Switch) findViewById(R.id.toggleImperial);

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
}