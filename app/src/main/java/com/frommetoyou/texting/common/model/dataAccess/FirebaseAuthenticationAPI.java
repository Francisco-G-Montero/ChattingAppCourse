package com.frommetoyou.texting.common.model.dataAccess;

import com.frommetoyou.texting.common.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthenticationAPI {
    private FirebaseAuth mFirebaseAuth;

    public FirebaseUser getCurrentUser() {
        return mFirebaseAuth.getCurrentUser();
    }

    private static class SingletonHolder {

        private static final FirebaseAuthenticationAPI INSTANCE = new FirebaseAuthenticationAPI();

    }

    public static FirebaseAuthenticationAPI getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private FirebaseAuthenticationAPI() {
        this.mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getmFirebaseAuth() {
        return this.mFirebaseAuth;
    }

    public User getAuthUser() {
        User user = new User();
        if (mFirebaseAuth != null && mFirebaseAuth.getCurrentUser()!=null) {
            user.setUid(mFirebaseAuth.getCurrentUser().getUid());
            user.setUsername(mFirebaseAuth.getCurrentUser().getDisplayName());
            user.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
            user.setUriProfile(mFirebaseAuth.getCurrentUser().getPhotoUrl());
        }
        return user;
    }
}
