package br.ufc.tele_diabetes.utils;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by robertcabral on 8/26/17.
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
