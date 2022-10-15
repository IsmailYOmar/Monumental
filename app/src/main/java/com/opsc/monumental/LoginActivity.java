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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailAddress, password;
    private Button loginButton, redirectButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // If statement that keeps the logged in user logged in
        if (user != null)
        {
            // User is signed in
            Intent i = new Intent(LoginActivity.this, MapsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else
        {
            // User is signed out
            //Log.d(, "onAuthStateChanged:signed_out");
        }

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        redirectButton = (Button) findViewById(R.id.redirectButton);
        redirectButton.setOnClickListener(this);

        emailAddress = (EditText) findViewById(R.id.emailAddress);
        password = (EditText) findViewById(R.id.password);
    }

    @Override
    public void onClick(View v) {
        // Establishes a switch case to allow the function of 2 buttons
        switch (v.getId()) {
            // Initializes a case the button that will link to the register page
            case R.id.redirectButton:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            // Initializes a case for the login button and its method
            case R.id.loginButton:
                startActivity(new Intent(this, MapsActivity.class));
                userLogin();
                break;
        }
    }

    public void userLogin() {
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
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        //Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                        startActivity( new Intent(LoginActivity.this, MapsActivity.class));
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Login Unsuccessful." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}