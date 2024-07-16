// ContactDialogFragment.java
package com.example.e_contact;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

public class ContactDialogFragment extends DialogFragment {

    private TextInputEditText contactNameEditText;
    private TextInputEditText contactNumberEditText;
    private Button addButton;

    // Static method to create a new instance of the ContactDialogFragment
    public static ContactDialogFragment newInstance() {
        return new ContactDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_item, container, false);

        // Initialize views
        contactNameEditText = view.findViewById(R.id.contact_name);
        contactNumberEditText = view.findViewById(R.id.contact_number);
        addButton = view.findViewById(R.id.add_button);

        // Set click listener for the add button
        addButton.setOnClickListener(v -> {
            String name = contactNameEditText.getText().toString();
            String number = contactNumberEditText.getText().toString();
            if (!name.isEmpty() && !number.isEmpty()) {
                // Insert contact into the database
                int result = DbHelper.insertContact(name, number);
                if (result == 0) {
                    Toast.makeText(getContext(), "Contact added successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else if (result == -1) {
                    Toast.makeText(getContext(), "Contact number already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add contact", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // Refresh the contact list in the parent fragment
        if (getTargetFragment() instanceof EContactFragment) {
            ((EContactFragment) getTargetFragment()).refreshContactList();
        }
    }
}
