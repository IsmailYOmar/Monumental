package com.opsc.monumental;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Splash Screen duration
        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);
    }
}