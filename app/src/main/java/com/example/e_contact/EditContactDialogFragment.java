package com.example.e_contact;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class EditContactDialogFragment extends DialogFragment {

    // Keys for arguments
    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_NUMBER = "number";

    // Instance variables to hold contact details
    private int contactId;
    private String contactName;
    private String contactNumber;

    // Method to create a new instance of EditContactDialogFragment with arguments
    public static EditContactDialogFragment newInstance(int id, String name, String number) {
        EditContactDialogFragment fragment = new EditContactDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putString(ARG_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve arguments
        if (getArguments() != null) {
            contactId = getArguments().getInt(ARG_ID);
            contactName = getArguments().getString(ARG_NAME);
            contactNumber = getArguments().getString(ARG_NUMBER);
        }
    }

    // Method to create and return the view hierarchy associated with the dialog fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_item, container, false);

        // Find views by ID
        EditText nameEditText = view.findViewById(R.id.contact_name);
        EditText numberEditText = view.findViewById(R.id.contact_number);
        Button saveButton = view.findViewById(R.id.add_button);

        // Set text for name and number EditText fields
        nameEditText.setText(contactName);
        numberEditText.setText(contactNumber);

        // Set click listener for the save button
        saveButton.setOnClickListener(v -> {
            // Update contact in the database
            DbHelper.updateContact(contactId, nameEditText.getText().toString(), numberEditText.getText().toString());

            // Notify parent fragment to refresh contact list
            if (getTargetFragment() instanceof EContactFragment) {
                ((EContactFragment) getTargetFragment()).refreshContactList();
            }
            dismiss(); // Dismiss the dialog fragment
        });

        return view;
    }
}
