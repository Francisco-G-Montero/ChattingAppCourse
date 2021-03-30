package com.frommetoyou.texting.mainModule.events;

import com.frommetoyou.texting.common.pojo.User;

public class MainEvent {
    public static final int USER_ADDED = 0;
    public static final int USER_UPDATED = 1;
    public static final int USER_REMOVED = 2;
    public static final int REQUEST_ADDED = 3;
    public static final int REQUEST_UPDATED = 4;
    public static final int REQUEST_REMOVED = 5;
    public static final int REQUEST_ACCEPTED = 6;
    public static final int REQUEST_DENIED = 7;
    public static final int ERROR_SERVER = 100;

    private User user;
    private int typeEvent;
    private int resMessage;

    public MainEvent() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
