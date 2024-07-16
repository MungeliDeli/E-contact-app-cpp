package com.example.e_contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class EmergencyFragment extends Fragment {

    public EmergencyFragment() {
        // Required empty public constructor
    }

    // Method to create a new instance of the EmergencyFragment
    public static EmergencyFragment newInstance(String param1, String param2) {
        EmergencyFragment fragment = new EmergencyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Method to create and return the view hierarchy associated with the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        // Set click listeners for each card
        view.findViewById(R.id.cardView_hospital).setOnClickListener(v -> dialNumber("992"));
        view.findViewById(R.id.cardView_fire).setOnClickListener(v -> dialNumber("993"));
        view.findViewById(R.id.cardView_police).setOnClickListener(v -> dialNumber("911"));

        return view;
    }

    // Method to initiate a phone call to the specified phone number
    private void dialNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        try {
            startActivity(intent);
        } catch (Exception e) {
            // Show toast message if dialing fails
            Toast.makeText(getContext(), "Failed to dial number: " + phoneNumber, Toast.LENGTH_SHORT).show();
        }
    }
}
