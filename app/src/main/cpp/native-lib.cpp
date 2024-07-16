#include <jni.h>
#include <string>
#include <android/log.h>
#include "sqlite3.h"

// Logging tag for log messages
#define LOG_TAG "SQLiteDatabase"

// Macros for logging information and errors
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// SQLite database object
sqlite3 *db;

// Function to initialize and open the SQLite database
extern "C" JNIEXPORT jint JNICALL
Java_com_example_e_1contact_DbHelper_initDB(JNIEnv *env, jobject, jstring dbPath) {
    // Convert the Java string (jstring) to a C-style string (const char*)
    const char *dbPathCStr = env->GetStringUTFChars(dbPath, nullptr);

    // Open the SQLite database at the specified path
    int rc = sqlite3_open(dbPathCStr, &db);
    if (rc) {
        // If the database failed to open, log an error message and return the error code
        LOGE("Can't open database: %s", sqlite3_errmsg(db));
        return rc;
    } else {
        // If the database opened successfully, log an info message
        LOGI("Opened database successfully");
    }

    // Release the memory allocated for the database path string
    env->ReleaseStringUTFChars(dbPath, dbPathCStr);

    // Return SQLITE_OK to indicate success
    return SQLITE_OK;
}

// Function to create the necessary tables in the database
extern "C" JNIEXPORT jint JNICALL
Java_com_example_e_1contact_DbHelper_createTables(JNIEnv *env, jobject /* this */) {
    // SQL statement to create the USERDATA table if it doesn't exist
    const char *sqlCreateTableUser = "CREATE TABLE IF NOT EXISTS USERDATA("
                                     "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                                     "FIRST_NAME TEXT NOT NULL, "
                                     "LAST_NAME TEXT NOT NULL, "
                                     "HOME_ADDRESS TEXT NOT NULL, "
                                     "HOME_PHONE TEXT NOT NULL CHECK(LENGTH(HOME_PHONE) = 10), "
                                     "EMERGENCY_CONTACT_NAME TEXT NOT NULL, "
                                     "EMERGENCY_CONTACT_NUMBER TEXT NOT NULL CHECK(LENGTH(EMERGENCY_CONTACT_NUMBER) = 10));";

    // Pointer to store error messages from SQLite
    char *errMsg = 0;

    // Execute the SQL statement to create the USERDATA table
    int returnCode = sqlite3_exec(db, sqlCreateTableUser, 0, 0, &errMsg);
    if (returnCode != SQLITE_OK) {
        // If there is an error, log the error message and free the error message memory
        LOGE("SQL error: %s", errMsg);
        sqlite3_free(errMsg);
        return returnCode;
    } else {
        // If the table was created successfully, log an info message
        LOGI("Table created successfully");
    }

    // SQL statement to create the CONTACT table if it doesn't exist
    const char *sqlCreateTableContact = "CREATE TABLE IF NOT EXISTS CONTACT("
                                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                                        "CONTACT_NAME TEXT NOT NULL,"
                                        "CONTACT_NUMBER TEXT NOT NULL CHECK(LENGTH(CONTACT_NUMBER) = 10));";

    // Pointer to store error messages from SQLite
    char *errMsg1 = 0;

    // Execute the SQL statement to create the CONTACT table
    int returnCode1 = sqlite3_exec(db, sqlCreateTableContact, 0, 0, &errMsg1);
    if (returnCode1 != SQLITE_OK) {
        // If there is an error, log the error message and free the error message memory
        LOGE("SQL error: %s", errMsg1);
        sqlite3_free(errMsg1);
        return returnCode1;
    } else {
        // If the table was created successfully, log an info message
        LOGI("Table created successfully");
    }

    // Return SQLITE_OK to indicate success
    return SQLITE_OK;
}



// Function to delete a contact from the CONTACT table
extern "C" JNIEXPORT jint JNICALL
Java_com_example_e_1contact_DbHelper_deleteContact(JNIEnv *env, jobject, jint contactId) {
    const char *sqlDelete = "DELETE FROM CONTACT WHERE ID = ?;";
    sqlite3_stmt *stmt;
    int rc = sqlite3_prepare_v2(db, sqlDelete, -1, &stmt, 0);
    if (rc != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return rc;
    }

    sqlite3_bind_int(stmt, 1, contactId);

    rc = sqlite3_step(stmt);
    if (rc != SQLITE_DONE) {
        LOGE("Execution failed: %s", sqlite3_errmsg(db));
    } else {
        LOGI("Record deleted successfully");
    }

    sqlite3_finalize(stmt);
    return rc == SQLITE_DONE ? SQLITE_OK : rc;
}


// Function to get user data from the USERDATA table
extern "C" JNIEXPORT jobject JNICALL
Java_com_example_e_1contact_DbHelper_getUserData(JNIEnv *env, jobject /* this */) {
    // SQL query to select all columns from the USERDATA table
    const char *sqlQuery = "SELECT ID, FIRST_NAME, LAST_NAME, HOME_ADDRESS, HOME_PHONE, EMERGENCY_CONTACT_NAME, EMERGENCY_CONTACT_NUMBER FROM USERDATA;";
    sqlite3_stmt *stmt;

    // Prepare the SQL statement
    LOGI("Preparing SQL statement");
    int rc = sqlite3_prepare_v2(db, sqlQuery, -1, &stmt, 0);
    if (rc != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return nullptr;
    } else {
        LOGI("Statement prepared successfully");
    }

    // Find the Java UserData class
    jclass userDataClass = env->FindClass("com/example/e_contact/UserData");
    if (userDataClass == nullptr) {
        LOGE("Failed to find UserData class");
        return nullptr;
    }

    // Get the constructor method ID for the UserData class
    jmethodID userDataConstructor = env->GetMethodID(userDataClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    if (userDataConstructor == nullptr) {
        LOGE("Failed to find UserData constructor");
        return nullptr;
    }

    jobject userDataObj = nullptr;

    // Step through the result set
    LOGI("Stepping through the result set");
    if (sqlite3_step(stmt) == SQLITE_ROW) {
        // Create Java strings from the SQLite row data
        jstring firstName = env->NewStringUTF((const char *) sqlite3_column_text(stmt, 1));
        jstring lastName = env->NewStringUTF((const char *) sqlite3_column_text(stmt, 2));
        jstring homeAddress = env->NewStringUTF((const char *) sqlite3_column_text(stmt, 3));
        jstring homePhone = env->NewStringUTF((const char *) sqlite3_column_text(stmt, 4));
        jstring emergencyContactName = env->NewStringUTF((const char *) sqlite3_column_text(stmt, 5));
        jstring emergencyContactNumber = env->NewStringUTF((const char *) sqlite3_column_text(stmt, 6));

        // Create a new UserData object using the constructor
        userDataObj = env->NewObject(userDataClass, userDataConstructor, firstName, lastName, homeAddress, homePhone, emergencyContactName, emergencyContactNumber);

        // Release local references to the Java strings
        env->DeleteLocalRef(firstName);
        env->DeleteLocalRef(lastName);
        env->DeleteLocalRef(homeAddress);
        env->DeleteLocalRef(homePhone);
        env->DeleteLocalRef(emergencyContactName);
        env->DeleteLocalRef(emergencyContactNumber);

        if (userDataObj != nullptr) {
            LOGI("UserData object created successfully");
        } else {
            LOGE("Failed to create UserData object");
        }
    } else {
        LOGI("No data found in USERDATA table");
    }

    // Finalize the SQL statement
    sqlite3_finalize(stmt);

    // Return the created UserData object or nullptr if no data was found
    return userDataObj;
}

// Function to insert data into the USERDATA table
extern "C" JNIEXPORT jint JNICALL
Java_com_example_e_1contact_DbHelper_insertData(JNIEnv *env, jobject, jstring firstName, jstring lastName, jstring homeAddress, jstring homePhone, jstring emergencyContact, jstring emergencyContactNumber) {
    // SQL statement to insert data into USERDATA table
    const char *sqlInsert = "INSERT INTO USERDATA (FIRST_NAME, LAST_NAME, HOME_ADDRESS, HOME_PHONE, EMERGENCY_CONTACT_NAME, EMERGENCY_CONTACT_NUMBER) VALUES (?, ?, ?, ?, ?, ?);";
    sqlite3_stmt *stmt;
    int rc = sqlite3_prepare_v2(db, sqlInsert, -1, &stmt, 0);
    if (rc != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return rc;
    }

    // SQL  tstatemento insert home phone into CONTACT table
    const char *sqlInsert1 = "INSERT INTO CONTACT (CONTACT_NAME, CONTACT_NUMBER) VALUES ('Home Phone', ?);";
    sqlite3_stmt *stmt1;
    int rc1 = sqlite3_prepare_v2(db, sqlInsert1, -1, &stmt1, 0);
    if (rc1 != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return rc1;
    }

    // SQL statement to insert emergency contact details into CONTACT table
    const char *sqlInsert2 = "INSERT INTO CONTACT (CONTACT_NAME, CONTACT_NUMBER) VALUES (?, ?);";
    sqlite3_stmt *stmt2;
    int rc2 = sqlite3_prepare_v2(db, sqlInsert2, -1, &stmt2, 0);
    if (rc2 != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return rc2;
    }

    // Convert Java strings to C-style strings
    const char *firstNameCStr = env->GetStringUTFChars(firstName, nullptr);
    const char *lastNameCStr = env->GetStringUTFChars(lastName, nullptr);

    const char *homeAddressCStr = env->GetStringUTFChars(homeAddress, nullptr);
    const char *homePhoneCStr = env->GetStringUTFChars(homePhone, nullptr);
    const char *emergencyContactCStr = env->GetStringUTFChars(emergencyContact, nullptr);
    const char *emergencyContactNumberCStr = env->GetStringUTFChars(emergencyContactNumber, nullptr);

    // Bind data to the prepared SQL statements
    sqlite3_bind_text(stmt1, 1, homePhoneCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt2, 1, emergencyContactCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt2, 2, emergencyContactNumberCStr, -1, SQLITE_TRANSIENT);

    sqlite3_bind_text(stmt, 1, firstNameCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 2, lastNameCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 3, homeAddressCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 4, homePhoneCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 5, emergencyContactCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 6, emergencyContactNumberCStr, -1, SQLITE_TRANSIENT);

    // Execute the SQL statements
    rc = sqlite3_step(stmt);
    if (rc != SQLITE_DONE) {
        LOGE("Execution failed: %s", sqlite3_errmsg(db));
    } else {
        LOGI("Record inserted successfully");
    }
    sqlite3_finalize(stmt);

    rc1 = sqlite3_step(stmt1);
    if (rc1 != SQLITE_DONE) {
        LOGE("Execution failed: %s", sqlite3_errmsg(db));
    } else {
        LOGI("Record inserted successfully");
    }
    sqlite3_finalize(stmt1);

    rc2 = sqlite3_step(stmt2);
    if (rc2 != SQLITE_DONE) {
        LOGE("Execution failed: %s", sqlite3_errmsg(db));
    } else {
        LOGI("Record inserted successfully");
    }
    sqlite3_finalize(stmt2);

    // Release memory allocated for the C-style strings
    env->ReleaseStringUTFChars(firstName, firstNameCStr);
    env->ReleaseStringUTFChars(lastName, lastNameCStr);
    env->ReleaseStringUTFChars(homeAddress, homeAddressCStr);
    env->ReleaseStringUTFChars(homePhone, homePhoneCStr);
    env->ReleaseStringUTFChars(emergencyContact, emergencyContactCStr);
    env->ReleaseStringUTFChars(emergencyContactNumber, emergencyContactNumberCStr);

    // Return SQLITE_OK if all insertions were successful, otherwise return the error code
    return rc == SQLITE_DONE ? SQLITE_OK : rc;
}


// Function to insert a contact into CONTACT table
extern "C" JNIEXPORT jint JNICALL
Java_com_example_e_1contact_DbHelper_insertContact(JNIEnv *env, jobject, jstring contact_name, jstring contact_number) {
    const char *sqlInsert = "INSERT INTO CONTACT(CONTACT_NAME, CONTACT_NUMBER) VALUES (?, ?);";
    sqlite3_stmt *stmt;

    const char *contactNumberCStr = env->GetStringUTFChars(contact_number, nullptr);

    // Check if the contact already exists
    const char *sqlCheckExistence = "SELECT COUNT(*) FROM CONTACT WHERE CONTACT_NUMBER = ?";
    sqlite3_stmt *existenceStmt;

    int contactCount = 0;
    int rc = sqlite3_prepare_v2(db, sqlCheckExistence, -1, &existenceStmt, 0);
    if (rc != SQLITE_OK) {
        LOGE("Failed to prepare existence check statement: %s", sqlite3_errmsg(db));
        return rc;
    }

    sqlite3_bind_text(existenceStmt, 1, contactNumberCStr, -1, SQLITE_TRANSIENT);

    if (sqlite3_step(existenceStmt) == SQLITE_ROW) {
        contactCount = sqlite3_column_int(existenceStmt, 0);
    }

    sqlite3_finalize(existenceStmt);

    if (contactCount > 0) {
        env->ReleaseStringUTFChars(contact_number, contactNumberCStr);
        return SQLITE_CONSTRAINT;
    }

    rc = sqlite3_prepare_v2(db, sqlInsert, -1, &stmt, 0);
    if (rc != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return rc;
    }

    const char *contactNameCStr = env->GetStringUTFChars(contact_name, nullptr);

    sqlite3_bind_text(stmt, 1, contactNameCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 2, contactNumberCStr, -1, SQLITE_TRANSIENT);

    rc = sqlite3_step(stmt);
    if (rc != SQLITE_DONE) {
        LOGE("Execution failed: %s", sqlite3_errmsg(db));
    } else {
        LOGI("Record inserted successfully");
    }

    sqlite3_finalize(stmt);

    env->ReleaseStringUTFChars(contact_name, contactNameCStr);
    env->ReleaseStringUTFChars(contact_number, contactNumberCStr);

    return rc == SQLITE_DONE ? SQLITE_OK : rc;
}

// Function to get all contacts from CONTACT table
extern "C" JNIEXPORT jobject JNICALL
Java_com_example_e_1contact_DbHelper_getAllContacts(JNIEnv *env, jobject /* this */) {
    const char *sqlQuery = "SELECT * FROM CONTACT;";
    sqlite3_stmt *stmt;

    int rc = sqlite3_prepare_v2(db, sqlQuery, -1, &stmt, 0);
    if (rc != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return nullptr;
    }

    jclass arrayListClass = env->FindClass("java/util/ArrayList");
    jobject arrayList = env->NewObject(arrayListClass, env->GetMethodID(arrayListClass, "<init>", "()V"));
    jmethodID arrayListAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

    jclass contactClass = env->FindClass("com/example/e_contact/Contact");
    jmethodID contactConstructor = env->GetMethodID(contactClass, "<init>", "(ILjava/lang/String;Ljava/lang/String;)V");

    while (sqlite3_step(stmt) == SQLITE_ROW) {
        int contactID = sqlite3_column_int(stmt, 0);
        const char *contactName = (const char *) sqlite3_column_text(stmt, 1);
        const char *contactNumber = (const char *) sqlite3_column_text(stmt, 2);

        jstring contactNameStr = env->NewStringUTF(contactName);
        jstring contactNumberStr = env->NewStringUTF(contactNumber);

        jobject contactObj = env->NewObject(contactClass, contactConstructor, contactID, contactNameStr, contactNumberStr);
        env->CallBooleanMethod(arrayList, arrayListAdd, contactObj);

        env->DeleteLocalRef(contactNameStr);
        env->DeleteLocalRef(contactNumberStr);
        env->DeleteLocalRef(contactObj);
    }

    sqlite3_finalize(stmt);

    return arrayList;
}




// Function to update user data in USERDATA table
extern "C" JNIEXPORT jint JNICALL
Java_com_example_e_1contact_DbHelper_updateUserData(JNIEnv *env, jobject /* this */,
                                                    jstring firstName, jstring lastName, jstring homeAddress,
                                                    jstring homePhone, jstring emergencyContact, jstring emergencyContactNumber) {

//    updating the userdata
    const char *sqlUpdate = "UPDATE USERDATA SET FIRST_NAME = ?, LAST_NAME = ?, HOME_ADDRESS = ?, HOME_PHONE = ?, EMERGENCY_CONTACT_NAME = ?, EMERGENCY_CONTACT_NUMBER = ? WHERE ID = 1;";
    sqlite3_stmt *stmt;

    int rc = sqlite3_prepare_v2(db, sqlUpdate, -1, &stmt, 0);
    if (rc != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return rc;
    }


//    updatin the contact data home phone
    const char *sqlUpdate1 = "UPDATE CONTACT SET  CONTACT_NUMBER = ? WHERE  CONTACT_NAME = 'Home Phone';";
    sqlite3_stmt *stmt1;

    int rc1 = sqlite3_prepare_v2(db, sqlUpdate1, -1, &stmt1, 0);
    if (rc1 != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return rc1;
    }

//    updatin the contact data Emergency

    const char *sqlUpdate2 = "UPDATE CONTACT SET  CONTACT_NAME = ? , CONTACT_NUMBER = ? WHERE ID = 2;";
    sqlite3_stmt *stmt2;

    int rc2 = sqlite3_prepare_v2(db, sqlUpdate2, -1, &stmt2, 0);
    if (rc2 != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return rc2;
    }



    const char *firstNameCStr = env->GetStringUTFChars(firstName, nullptr);
    const char *lastNameCStr = env->GetStringUTFChars(lastName, nullptr);
    const char *homeAddressCStr = env->GetStringUTFChars(homeAddress, nullptr);
    const char *homePhoneCStr = env->GetStringUTFChars(homePhone, nullptr);
    const char *emergencyContactCStr = env->GetStringUTFChars(emergencyContact, nullptr);
    const char *emergencyContactNumberCStr = env->GetStringUTFChars(emergencyContactNumber, nullptr);

//    binding the homephone and emergency contact
    sqlite3_bind_text(stmt1, 1, homePhoneCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt2, 1, emergencyContactCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt2, 2, emergencyContactNumberCStr, -1, SQLITE_TRANSIENT);



    sqlite3_bind_text(stmt, 1, firstNameCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 2, lastNameCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 3, homeAddressCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 4, homePhoneCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 5, emergencyContactCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 6, emergencyContactNumberCStr, -1, SQLITE_TRANSIENT);

//    evaluating the first stamet stmt
    rc = sqlite3_step(stmt);
    if (rc != SQLITE_DONE) {
        LOGE("Execution failed: %s", sqlite3_errmsg(db));
    } else {
        LOGI("Record updated successfully");
    }

    sqlite3_finalize(stmt);

    //    evaluating the second stamet stmt1
    rc = sqlite3_step(stmt1);
    if (rc1 != SQLITE_DONE) {
        LOGE("Execution failed: %s", sqlite3_errmsg(db));
    } else {
        LOGI("Record updated successfully");
    }

    sqlite3_finalize(stmt1);

    //    evaluating the third stamet stmt2
    rc = sqlite3_step(stmt2);
    if (rc2 != SQLITE_DONE) {
        LOGE("Execution failed: %s", sqlite3_errmsg(db));
    } else {
        LOGI("Record updated successfully");
    }

    sqlite3_finalize(stmt2);


    env->ReleaseStringUTFChars(firstName, firstNameCStr);
    env->ReleaseStringUTFChars(lastName, lastNameCStr);
    env->ReleaseStringUTFChars(homeAddress, homeAddressCStr);
    env->ReleaseStringUTFChars(homePhone, homePhoneCStr);
    env->ReleaseStringUTFChars(emergencyContact, emergencyContactCStr);
    env->ReleaseStringUTFChars(emergencyContactNumber, emergencyContactNumberCStr);

    return rc;
}




// Function to update a contact in the CONTACT table
extern "C" JNIEXPORT jint JNICALL
Java_com_example_e_1contact_DbHelper_updateContact(JNIEnv *env, jobject, jint contactId, jstring contactName, jstring contactNumber) {
    const char *sqlUpdate = "UPDATE CONTACT SET CONTACT_NAME = ?, CONTACT_NUMBER = ? WHERE ID = ?;";
    sqlite3_stmt *stmt;
    int rc = sqlite3_prepare_v2(db, sqlUpdate, -1, &stmt, 0);
    if (rc != SQLITE_OK) {
        LOGE("Failed to prepare statement: %s", sqlite3_errmsg(db));
        return rc;
    }

    const char *contactNameCStr = env->GetStringUTFChars(contactName, nullptr);
    const char *contactNumberCStr = env->GetStringUTFChars(contactNumber, nullptr);

    sqlite3_bind_text(stmt, 1, contactNameCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 2, contactNumberCStr, -1, SQLITE_TRANSIENT);
    sqlite3_bind_int(stmt, 3, contactId);

    rc = sqlite3_step(stmt);
    if (rc != SQLITE_DONE) {
        LOGE("Execution failed: %s", sqlite3_errmsg(db));
    } else {
        LOGI("Record updated successfully");
    }

    sqlite3_finalize(stmt);

    env->ReleaseStringUTFChars(contactName, contactNameCStr);
    env->ReleaseStringUTFChars(contactNumber, contactNumberCStr);

    return rc == SQLITE_DONE ? SQLITE_OK : rc;
}


