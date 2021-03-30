package com.frommetoyou.texting.mainModule.model.dataAccess;

import com.frommetoyou.texting.common.model.dataAccess.FirebaseAuthenticationAPI;

public class Authentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public Authentication() {
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public FirebaseAuthenticationAPI getmAuthenticationAPI() {
        return mAuthenticationAPI;
    }

    public void signOff(){
        mAuthenticationAPI.getmFirebaseAuth().signOut();
    }
}
