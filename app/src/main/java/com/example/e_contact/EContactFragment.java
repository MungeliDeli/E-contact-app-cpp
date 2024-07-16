package com.example.e_contact;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class EContactFragment extends Fragment {

    // RecyclerView to display the list of contacts
    private RecyclerView recyclerView;

    // Adapter to manage contact data and bind it to the RecyclerView
    private ContactAdapter contactAdapter;

    // List to hold the contacts retrieved from the database
    private List<Contact> contactList;

    // Floating action button for adding new contacts
    private FloatingActionButton addButton;

    // Default constructor, required for fragments
    public EContactFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_e_contact, container, false);

        // Initialize the database
        Context context = getContext();
        if (context != null) {
            String dbPath = context.getDatabasePath("mydb.db").getAbsolutePath();
            int resultCode = DbHelper.initDB(dbPath);
            if (resultCode != 0) {
                // Display a toast message if database initialization fails
                Toast.makeText(context, "Failed to initialize database", Toast.LENGTH_SHORT).show();
            }
        }

        // Set up the RecyclerView with a linear layout manager
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load contact data from the database and set it to the RecyclerView
        loadContacts();

        // Set up the floating action button to open a dialog for adding new contacts
        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            // Create and show a new instance of ContactDialogFragment
            ContactDialogFragment dialogFragment = ContactDialogFragment.newInstance();
            dialogFragment.setTargetFragment(EContactFragment.this, 0);
            dialogFragment.show(getParentFragmentManager(), "AddContactDialogFragment");
        });

        return view;
    }

    // Method to load contacts from the database and set up the adapter
    private void loadContacts() {
        // Retrieve all contacts from the database
        contactList = DbHelper.getAllContacts();

        // Initialize the adapter with the context, fragment, and contact list
        contactAdapter = new ContactAdapter(getContext(), this, contactList); // Pass the fragment instance

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(contactAdapter);
    }

    // Method to refresh the contact list, typically called after adding or editing a contact
    public void refreshContactList() {
        loadContacts();
    }
}
