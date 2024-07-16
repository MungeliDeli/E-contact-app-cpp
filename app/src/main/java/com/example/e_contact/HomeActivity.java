package com.example.e_contact;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;



import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.e_contact.databinding.ActivityHomeBinding;


public class HomeActivity extends AppCompatActivity {

    // This is used to bind the views from the XML layout to Java code.
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This line sets up view binding for this activity.
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // This listener adjusts the padding of the main view to avoid overlapping with system bars (status bar, navigation bar).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load the default fragment (EContactFragment) when the activity starts.
        replaceFragment(new EContactFragment());

        // Setting up the bottom navigation view to switch between fragments.
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.E_contact:
                    replaceFragment(new EContactFragment());
                    break;
                case R.id.Emergency:
                    replaceFragment(new EmergencyFragment());
                    break;
                case R.id.geo_tag:
                    replaceFragment(new LocationFragment());
                    break;
                default:
                    return false;
            }
            return true;
        });

        // Change the status bar color to match the app theme.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.greenLite));
        }

        // Changing the color of the action bar at the top of the screen.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#32a852")));
        }

      
    }

    // Method to replace the current fragment with a new one.
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment); // Replace the frame layout with the new fragment.
        fragmentTransaction.commit(); // Commit the transaction.
    }
}
