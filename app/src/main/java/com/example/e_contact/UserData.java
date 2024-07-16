package com.example.e_contact;

// Class representing user data
public class UserData {
    // Fields to store user information
    private String firstName;
    private String lastName;
    private String homeAddress;
    private String homePhone;
    private String emergencyContactName;
    private String emergencyContactNumber;

    // Constructor to initialize user data
    public UserData(String firstName, String lastName, String homeAddress, String homePhone, String emergencyContactName, String emergencyContactNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.homeAddress = homeAddress;
        this.homePhone = homePhone;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactNumber = emergencyContactNumber;
    }

    // Getter method for retrieving the first name
    public String getFirstName() {
        return firstName;
    }

    // Getter method for retrieving the last name
    public String getLastName() {
        return lastName;
    }

    // Getter method for retrieving the home address
    public String getAddress() {
        return homeAddress;
    }

    // Getter method for retrieving the home phone number
    public String getHomePhone() {
        return homePhone;
    }

    // Getter method for retrieving the emergency contact name
    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    // Getter method for retrieving the emergency contact number
    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }
}
