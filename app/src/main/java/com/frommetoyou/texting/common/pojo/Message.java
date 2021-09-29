package com.frommetoyou.texting.common.pojo;

import com.google.firebase.database.Exclude;

public class Message {
    public final static String PATH_STATUS = "status";
    public final static int SENT = 1;
    public final static int SEEN = 2;

    private String message;
    private String sender;
    private String photoUrl;
    private int status;
    @Exclude
    private boolean sentByMe;
    @Exclude
    private String uid;
    @Exclude
    private boolean loaded;

    public Message() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    @Exclude
    public boolean isSentByMe() {
        return sentByMe;
    }
    @Exclude
    public void setSentByMe(boolean sentByMe) {
        this.sentByMe = sentByMe;
    }
    @Exclude
    public String getUid() {
        return uid;
    }
    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }
    @Exclude
    public boolean isLoaded() {
        return loaded;
    }
    @Exclude
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return uid != null ? uid.equals(message.uid) : message.uid == null;
    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }
}
