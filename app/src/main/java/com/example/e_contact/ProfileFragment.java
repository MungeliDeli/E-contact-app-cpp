package com.example.e_contact;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    // Declare UI elements
    private TextInputEditText editFirstName, editLastName, editAddress, editHomePhone, editEmergencyContactName, editEmergencyContactNumber;
    private TextView titleName;
    private Button updateButton;
    private DbHelper dbHelper; // Database helper to interact with SQLite database

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        titleName = view.findViewById(R.id.User_name);
        editFirstName = view.findViewById(R.id.edit_first_name);
        editLastName = view.findViewById(R.id.edit_Last_name);
        editAddress = view.findViewById(R.id.address);
        editHomePhone = view.findViewById(R.id.home_phone);
        editEmergencyContactName = view.findViewById(R.id.emergency_Contact);
        editEmergencyContactNumber = view.findViewById(R.id.emergency_Contact_number);
        updateButton = view.findViewById(R.id.update_button);

        // Fetch user data and display it
        loadUserData();

        // Set click listener for the update button
        updateButton.setOnClickListener(v -> updateUserData());

        return view;
    }

    // Method to load user data from the database and display it
    private void loadUserData() {
        try {
            UserData userData = dbHelper.getUserData(); // Fetch user data from the database

            if (userData != null) {
                // Display user data in UI elements
                String fname = userData.getFirstName();
                String lname = userData.getLastName();
                String Ename = userData.getEmergencyContactName();
                String address = userData.getAddress();
                titleName.setText(fname.substring(0,1).toUpperCase() + fname.substring(1).toLowerCase() + " " + lname.substring(0,1).toUpperCase() + lname.substring(1).toLowerCase());
                editFirstName.setText(fname.substring(0,1).toUpperCase() + fname.substring(1).toLowerCase());
                editLastName.setText(lname.substring(0,1).toUpperCase() + lname.substring(1).toLowerCase());
                editAddress.setText(address.substring(0,1).toUpperCase() + address.substring(1).toLowerCase());
                editHomePhone.setText(userData.getHomePhone());
                editEmergencyContactName.setText(Ename.substring(0,1).toUpperCase() + Ename.substring(1).toLowerCase());
                editEmergencyContactNumber.setText(userData.getEmergencyContactNumber());
            } else {
                Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle exceptions
            Toast.makeText(getContext(), "Error loading data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Method to update user data in the database
    private void updateUserData() {
        try {
            // Get updated data from UI elements
            String firstName = editFirstName.getText().toString();
            String lastName = editLastName.getText().toString();
            String address = editAddress.getText().toString();
            String homePhone = editHomePhone.getText().toString();
            String emergencyContactName = editEmergencyContactName.getText().toString();
            String emergencyContactNumber = editEmergencyContactNumber.getText().toString();

            // Update user data in the database
            dbHelper.updateUserData(firstName, lastName, address, homePhone, emergencyContactName, emergencyContactNumber);

            // Show success message
            Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle exceptions
            Toast.makeText(getContext(), "Error updating data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
