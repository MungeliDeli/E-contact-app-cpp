package com.example.e_contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter class for displaying a list of contacts in a RecyclerView
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Context context;
    private Fragment fragment;

    private List<Contact> contactList;

    // Constructor for initializing the adapter
    public ContactAdapter(Context context, Fragment fragment, List<Contact> contactList) {
        this.context = context;
        this.fragment = fragment;
        this.contactList = contactList;
    }

    // Called when the RecyclerView needs a new ViewHolder to represent an item
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual contact items
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    // Called by RecyclerView to display data at the specified position
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // Get the contact at the given position
        Contact contact = contactList.get(position);

        String name = contact.getContactName();
        // Format `and set the contact name
        holder.contactName.setText(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
        // Set the contact number
        holder.contactNumber.setText(contact.getContactNumber());
        // Set the contact ID
        holder.contactID.setText(String.valueOf(contact.getId()));

        // Set up the options menu for the contact item
        holder.options.setOnClickListener(v -> showPopupMenu(v, position));

        // Set the click listener on the CardView
        holder.cardView.setOnClickListener(v -> {
            String phoneNumber = contact.getContactNumber();
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(dialIntent);
        });
    }

    // Show a popup menu when the options button is clicked
    private void showPopupMenu(View view, int position) {
        // Create a context with a custom style for the popup menu
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, R.style.CustomPopupMenu);
        PopupMenu popupMenu = new PopupMenu(contextThemeWrapper, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        // Inflate the menu resource into the popup menu
        inflater.inflate(R.menu.show_menu, popupMenu.getMenu());
        // Set a listener for menu item clicks
        popupMenu.setOnMenuItemClickListener(new MenuItemClickListener(position));
        // Show the popup menu
        popupMenu.show();
    }

    // Inner class to handle menu item clicks
    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;

        MenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // Handle menu item clicks based on the item ID
            switch (item.getItemId()) {
                case R.id.edit_contact:
                    // Edit contact
                    editContact(position);
                    return true;
                case R.id.delete_contact:
                    // Delete contact
                    deleteContact(position);
                    return true;
                default:
                    return false;
            }
        }
    }

    // Method to edit a contact
    private void editContact(int position) {
        // Check if fragment is not null
        if (fragment == null) {
            // Log error or handle it appropriately
            return;
        }

        // Get the contact at the given position
        Contact contact = contactList.get(position);
        // Create and show the edit contact dialog
        EditContactDialogFragment dialogFragment = EditContactDialogFragment.newInstance(contact.getId(), contact.getContactName(), contact.getContactNumber());
        dialogFragment.show(fragment.getParentFragmentManager(), "EditContactDialogFragment");
    }

    // Method to delete a contact
    private void deleteContact(int position) {
        // Get the contact at the given position
        Contact contact = contactList.get(position);
        // Delete the contact from the database
        DbHelper.deleteContact(contact.getId());
        // Remove the contact from the list
        contactList.remove(position);
        // Notify the adapter that the item has been removed
        notifyItemRemoved(position);
        // Notify the adapter that the range of items has changed
        notifyItemRangeChanged(position, contactList.size());
    }

    // Returns the total number of items in the data set
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // ViewHolder class to hold the views for each contact item
    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactNumber, contactID;
        ImageView options;
        CardView cardView;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            contactName = itemView.findViewById(R.id.contact_name);
            contactNumber = itemView.findViewById(R.id.contact_number);
            contactID = itemView.findViewById(R.id.contact_ID);
            options = itemView.findViewById(R.id.options);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
