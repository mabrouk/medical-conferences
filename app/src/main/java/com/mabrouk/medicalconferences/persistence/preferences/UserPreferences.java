package com.mabrouk.medicalconferences.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by User on 12/2/2016.
 */

public class UserPreferences {
    private static final String PREF_NAME = "user_prefs";
    private static final String PREF_USER_ID = "prefs_id";
    private static final String PREF_USER_ROLE = "prefs_role";

    private SharedPreferences sharedPreferences;

    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getUserId() {
        return sharedPreferences.getInt(PREF_USER_ID, 0);
    }

    public int getUserRole() {
        return sharedPreferences.getInt(PREF_USER_ROLE, 0);
    }

    public void logOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_USER_ID, 0);
        editor.putInt(PREF_USER_ROLE, 0);
        editor.commit();
    }

    public void login(int userId, int userRole) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_USER_ID, userId);
        editor.putInt(PREF_USER_ROLE, userRole);
        editor.apply();
    }
}
