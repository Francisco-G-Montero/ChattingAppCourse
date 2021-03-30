package com.frommetoyou.texting.loginModule.events;

import com.google.firebase.auth.FirebaseUser;

public class LoginEvent {
    public static final int STATUS_AUTH_SUCCESS = 0;
    public static final int ERROR_SERVER = 100;
    public static final int STATUS_AUTH_ERROR = 101;
    public static final int USER_NOT_EXISTS=102;

    private FirebaseUser user;
    private int typeEvent;
    private int resMessage;

    public LoginEvent() {
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public int getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(int typeEvent) {
        this.typeEvent = typeEvent;
    }

    public int getResMessage() {
        return resMessage;
    }

    public void setResMessage(int resMessage) {
        this.resMessage = resMessage;
    }
}
