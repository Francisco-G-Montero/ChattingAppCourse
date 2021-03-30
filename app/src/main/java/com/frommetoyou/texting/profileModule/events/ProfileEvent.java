package com.frommetoyou.texting.profileModule.events;

public class ProfileEvent {
    public static final int UPLOADED_IMAGE = 0;
    public static final int SAVED_USERNAME = 1;
    public static final int ERROR_USERNAME = 100;
    public static final int ERROR_IMAGE = 101;
    public static final int ERROR_PROFILE = 102;
    public static final int ERROR_SERVER = 103;

    private int typeEvent;
    private int resMessage;
    private String photoUrl;

    public ProfileEvent() {
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
