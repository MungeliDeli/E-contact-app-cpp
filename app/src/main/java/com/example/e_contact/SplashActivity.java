package com.example.e_contact;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 500; // 3 seconds

    private static final String PREFS_NAME = "RegistrationPrefs";
    private static final String IS_REGISTERED_KEY = "isRegistered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Hide the action bar for the splash screen.
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_splash);

        // Delay for SPLASH_DURATION milliseconds and then start the appropriate activity
        new Handler().postDelayed(() -> {

            // Check if the user is already registered using SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean isRegistered = prefs.getBoolean(IS_REGISTERED_KEY, false);

            Intent intent;
            if (isRegistered) {
                //if user is registered , skip registration page
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish(); // Close the splash screen activity
        }, SPLASH_DURATION);
    }
}



