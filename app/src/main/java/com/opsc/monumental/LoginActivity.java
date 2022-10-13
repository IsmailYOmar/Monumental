package com.opsc.monumental;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailAddress, password;
    private Button loginButton, redirectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                //userLogin();
                break;
        }
    }

}