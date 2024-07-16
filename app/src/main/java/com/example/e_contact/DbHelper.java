package com.example.e_contact;

import java.util.List;

public class DbHelper {

    static {
        System.loadLibrary("e_contact"); //Load the native library
    }

    // Native methods for initializing the database and creating tables
    public static native int initDB(String dbPath);
    public static native int createTables();

    // Native methods for CRUD operations on contacts
    public static native void deleteContact(int contactId);
    public static native void updateContact(int contactId, String contactName, String contactNumber);
    public static native int insertContact(String contactName, String contactNumber);
    public static native List<Contact> getAllContacts();

    // Native methods for CRUD operations on user data
    public static native int insertData(String firstName, String lastName, String homeAddress, String homePhone, String emergencyContactName, String emergencyContactNumber);
    public static native UserData getUserData();
    public static native int updateUserData(String firstName, String lastName, String homeAddress, String homePhone, String emergencyContactName, String emergencyContactNumber);
}
