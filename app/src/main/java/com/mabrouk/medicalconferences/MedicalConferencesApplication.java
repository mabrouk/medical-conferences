package com.mabrouk.medicalconferences;

import android.app.Application;

import com.mabrouk.medicalconferences.persistance.sqlite.DBWrapper;

/**
 * Created by User on 12/2/2016.
 */

public class MedicalConferencesApplication extends Application {
    private static MedicalConferencesApplication instance;

    public static MedicalConferencesApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DBWrapper.init(this);
    }
}
