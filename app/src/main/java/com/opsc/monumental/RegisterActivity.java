package com.opsc.monumental;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerButton, redirectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        redirectButton = (Button) findViewById(R.id.redirectButton);
        redirectButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        // Establishes a switch case to allow the function of 2 buttons
        switch (v.getId()) {
            // Initializes a case for the register button and its method
            case R.id.registerButton:
                //registerUser();
                break;

            // Initializes a case the button that will link to the login page
            case R.id.redirectButton:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;
        }
    }
}