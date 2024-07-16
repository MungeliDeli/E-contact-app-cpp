package com.example.e_contact;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.e_contact.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    // This block loads a native library called 'e_contact' when the application starts.
    static {
        System.loadLibrary("e_contact");
    }

    // This is used to bind the views from the XML layout to Java code.
    private ActivityMainBinding binding;

    // These are constants and variables for SharedPreferences.
    private static final String PREFS_NAME = "RegistrationPrefs";
    private static final String IS_REGISTERED_KEY = "isRegistered";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This line sets up view binding for this activity.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Changing the color of the action bar at the top of the screen.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#32a852")));
        }

        // Initializing the database with the path of the database file.
        String dbPath = getDatabasePath("mydb.db").getAbsolutePath();
        int resultCode = DbHelper.initDB(dbPath);
        if (resultCode != 0) {
            Toast.makeText(this, "Failed to initialize database", Toast.LENGTH_SHORT).show();
        }

        // Creating the necessary tables in the database.
        int rc = DbHelper.createTables();
        if (rc != 0) {
            Toast.makeText(this, "Failed to create tables for database", Toast.LENGTH_SHORT).show();
        }

        // Checking if the user is already registered using SharedPreferences.
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isRegistered = prefs.getBoolean(IS_REGISTERED_KEY, false);

        // If the user is already registered, go directly to the home page.
        if (isRegistered) {
            goToHomePage();
            return;
        }

        // Setting up the click listener for the save button.
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        // Setting up the click listener for the cancel button.
        findViewById(R.id.cancelbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Closes the current activity.
            }
        });
    }

    // Method to save data from the form into the database.
    private void saveData() {
        // Getting the data from the form fields.
        TextInputEditText firstNameEdit = findViewById(R.id.firstName);
        TextInputEditText lastNameEdit = findViewById(R.id.lastName);
        TextInputEditText homeAddressEdit = findViewById(R.id.homeAddress);
        TextInputEditText homePhoneEdit = findViewById(R.id.homePhone);
        TextInputEditText emergencyContactEdit = findViewById(R.id.emergencyContact);
        TextInputEditText emergencyContactNumberEdit = findViewById(R.id.emergencyContactNumber);

        // Converting the data to strings.
        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String homeAddress = homeAddressEdit.getText().toString();
        String homePhone = homePhoneEdit.getText().toString();
        String emergencyContact = emergencyContactEdit.getText().toString();
        String emergencyContactNumber = emergencyContactNumberEdit.getText().toString();

        // Input validation to ensure all fields are filled correctly.
        if (firstName.isEmpty() || lastName.isEmpty() || homeAddress.isEmpty() || homePhone.length() != 10 || emergencyContact.isEmpty() || emergencyContactNumber.length() != 10) {
            Toast.makeText(this, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inserting the data into the database.
        int resultCode = DbHelper.insertData(firstName, lastName, homeAddress, homePhone, emergencyContact, emergencyContactNumber);
        if (resultCode == 0) {
            // If data is saved successfully, mark the user as registered in SharedPreferences.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(IS_REGISTERED_KEY, true);
            editor.apply();

            // Show a success message and go to the home page.
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            goToHomePage();
        } else {
            // Show an error message if data saving failed.
            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to navigate to the home page activity.
    private void goToHomePage() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent); // Starts the HomeActivity.
        finish(); // Finishes the current activity.
    }

    /**
     * A native method that is implemented by the 'e_contact' native library,
     * which is packaged with this application.
     */
}
