package com.opsc.monumental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerButton, redirectButton;

    private EditText emailAddress, password;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

    public void registerUser() {
        String email = emailAddress.getText().toString();
        String pass = password.getText().toString();

        if(TextUtils.isEmpty(email)) {
            emailAddress.setError("This field is required.");
            emailAddress.requestFocus();
        }
        else if(TextUtils.isEmpty(pass)) {
            password.setError("This field is required.");
            password.requestFocus();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                        startActivity( new Intent(RegisterActivity.this, MapsActivity.class));
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registration Unsuccessful." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}