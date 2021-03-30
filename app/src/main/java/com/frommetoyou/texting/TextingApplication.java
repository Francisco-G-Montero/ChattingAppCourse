package com.frommetoyou.texting;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class TextingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        configFirebase();
    }

    private void configFirebase(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
