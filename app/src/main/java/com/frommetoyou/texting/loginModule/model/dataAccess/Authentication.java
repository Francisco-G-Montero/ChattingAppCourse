package com.frommetoyou.texting.loginModule.model.dataAccess;

import com.frommetoyou.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import com.frommetoyou.texting.common.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

public class Authentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public Authentication() {
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public void onResume() {
        mAuthenticationAPI.getmFirebaseAuth().addAuthStateListener(mAuthStateListener);
    }

    public void onPause() {
        if (mAuthStateListener != null) {
            mAuthenticationAPI.getmFirebaseAuth().removeAuthStateListener(mAuthStateListener);
        }
    }

    public void getStatusAuth(StatusAuthCallback callback) {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    callback.onGetUser(user);
                }else callback.onLaunchUILogin();
            }
        };
    }
    public User getCurrentUser(){
        return mAuthenticationAPI.getAuthUser();
    }
}
