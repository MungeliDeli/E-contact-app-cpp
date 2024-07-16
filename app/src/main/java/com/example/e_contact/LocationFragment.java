package com.example.e_contact;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

// LocationFragment displays the user's current location on a map and allows them to share it via SMS
public class LocationFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Request code for location permission

    private GoogleMap gmap; // Google Map object
    private FusedLocationProviderClient fusedLocationClient; // FusedLocationProviderClient for location services
    private TextView locationDisplay; // TextView to display location information
    private TextView locationDiscription; // TextView to display location description
    private TextView find_location_button; // Button to find user's location
    private TextView share_location_button; // Button to share user's location
    private double currentLatitude; // Current latitude
    private double currentLongitude; // Current longitude
    private LocationCallback locationCallback; // LocationCallback to receive location updates

    // Required empty public constructor
    public LocationFragment() {
    }

    // Factory method to create a new instance of LocationFragment
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        // Initialize the location callback to update location when changes occur
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // Update current latitude and longitude
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);
                        // Update UI with current location
                        updateLocationUI(currentLatLng, "My Location");
                    }
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        // Initialize UI elements
        locationDisplay = view.findViewById(R.id.My_location);
        locationDiscription = view.findViewById(R.id.Location_Descripiton);
        find_location_button = view.findViewById(R.id.find_location_button);
        share_location_button = view.findViewById(R.id.share_location_button);

        // Set click listeners for buttons
        Button findLocationButton = view.findViewById(R.id.find_location_button);
        Button shareLocationButton = view.findViewById(R.id.share_location_button);

        findLocationButton.setOnClickListener(v -> fetchLocation());
        shareLocationButton.setOnClickListener(v -> shareLocation());

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.id_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    // Method to fetch user's location
    private void fetchLocation() {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Get last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        // Update current latitude and longitude
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);
                        // Update UI with current location
                        updateLocationUI(currentLatLng, "My Location");
                    } else {
                        // Start location updates if last known location is null
                        startLocationUpdates();
                    }
                });

        // Check location settings
        checkLocationSettings();
    }

    // Method to start location updates
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update interval in milliseconds
        locationRequest.setFastestInterval(5000); // Fastest update interval in milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Set location request priority

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    // Method to update UI with current location
    private void updateLocationUI(LatLng latLng, String title) {
        if (gmap != null) {
            // Clear previous markers and add new marker at current location
            gmap.clear();
            gmap.addMarker(new MarkerOptions().position(latLng).title(title));
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        // Get address from latitude and longitude
        String address = getAddressFromLatLng(latLng);
        // Update location display TextView with latitude, longitude, and address
        locationDisplay.setText("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude + "\n" + address);
        // Set visibility of UI elements
        locationDisplay.setVisibility(View.VISIBLE);
        locationDiscription.setVisibility(View.VISIBLE);
        find_location_button.setVisibility(View.GONE);
        share_location_button.setVisibility(View.VISIBLE);
    }

    // Method to get address from latitude and longitude
    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String addressText = "Unable to get address";
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressBuilder.append(address.getAddressLine(i)).append("\n");
                }
                addressText = addressBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressText;
    }

    // Method to share user's location via SMS
    private void shareLocation() {
        // Check if current latitude and longitude are available
        if (currentLatitude != 0 && currentLongitude != 0) {
            // Get address from latitude and longitude
            String address = getAddressFromLatLng(new LatLng(currentLatitude, currentLongitude));
            // Create location message
            String locationMessage = "My current location: Lat: " + currentLatitude + ", Lng: " + currentLongitude + "\n" + address;

            // Create intent to open SMS app
            Intent smsIntent = new Intent(Intent.ACTION_VIEW , Uri.fromParts("sms", "",null));
            smsIntent.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
            smsIntent.putExtra("sms_body", locationMessage);
            startActivity(smsIntent);
        } else {
            // Show toast message if location is not available
            Toast.makeText(getContext(), "Location not available", Toast.LENGTH_SHORT).show();
        }
    }

    // Callback method called when the map is ready to be used
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;
        // Move camera to default location (University of Zambia)
        LatLng unza = new LatLng(-15.3923, 28.3285);
        gmap.moveCamera((CameraUpdateFactory.newLatLngZoom(unza , 14)) );
        // Add marker at University of Zambia
        MarkerOptions options = new MarkerOptions().position(unza).title("Unza");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        gmap.addMarker(options);
    }

    // Callback method called when permission request is handled
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch location
                fetchLocation();
            } else {
                // Permission denied, show toast message
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Callback method called when the fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove location updates when fragment is destroyed to save resources
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Method to check location settings and handle resolution if necessary
    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location requests here.
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(getActivity(), LOCATION_PERMISSION_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
}
