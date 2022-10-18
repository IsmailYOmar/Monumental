package com.opsc.monumental;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //creating relevant variables
    private Button registerButton, redirectButton;

    private EditText firstName, lastName, emailAddress, password;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing relevant UI variables on create
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        password = (EditText) findViewById(R.id.password);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        redirectButton = (Button) findViewById(R.id.redirectButton);
        redirectButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v){
        // Establishes a switch case to allow the function of 2 buttons
        switch (v.getId()) {
            // Initializes a case for the register button and its method
            case R.id.registerButton:
                registerUser();
                break;

            // Initializes a case the button that will link to the login page
            case R.id.redirectButton:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;
        }
    }

    //method that runs when user registers account
    public void registerUser() {
        //creating and populating string variables with users entered details
        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();
        String email = emailAddress.getText().toString();
        String pass = password.getText().toString();
        //setting users default setting preferences
        String defaultSystem = "Metric";
        String defaultPreference = "shopping_mall";

        //if statement which checks that users entered details are not blank
        if(TextUtils.isEmpty(fn)) {
            firstName.setError("This field is required.");
            firstName.requestFocus();
        }
        else if(TextUtils.isEmpty(ln)) {
            lastName.setError("This field is required.");
            lastName.requestFocus();
        }
        else if(TextUtils.isEmpty(email)) {
            emailAddress.setError("This field is required.");
            emailAddress.requestFocus();
        }
        else if(TextUtils.isEmpty(pass)) {
            password.setError("This field is required.");
            password.requestFocus();
        }
        //assuming user filled in all required details, the users relevant details are saved into the database
        else {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        User data = new User(fn, ln, email);
                        Setting defaultSettings = new Setting(defaultPreference, defaultSystem);
                        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).setValue(data);
                        FirebaseDatabase.getInstance().getReference("Settings").child(mAuth.getCurrentUser().getUid()).setValue(defaultSettings);
                        Toast.makeText(RegisterActivity.this, "Registration Successful.", Toast.LENGTH_LONG).show();
                        startActivity( new Intent(RegisterActivity.this, MapsActivity.class));
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registration Unsuccessful." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}