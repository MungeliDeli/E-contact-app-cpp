package com.example.e_contact;

public class Contact {
    private int id;
    private String contactName;
    private String contactNumber;

    // Constructor to initialize id, contactName, and contactNumber
    public Contact(int id, String contactName, String contactNumber) {
        this.id = id;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    // Getter for id
    public int getId() {
        return id;
    }

    // Setter for id
    public void setId(int id) {
        this.id = id;
    }

    // Getter for contactName
    public String getContactName() {
        return contactName;
    }

    // Setter for contactName
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    // Getter for contactNumber
    public String getContactNumber() {
        return contactNumber;
    }

    // Setter for contactNumber
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
