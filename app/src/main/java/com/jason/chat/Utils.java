package com.jason.chat;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jason on 5/3/2017.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
