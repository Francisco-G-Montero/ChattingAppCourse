package com.frommetoyou.texting.chatModule.model;

public interface LastConectionEventListener {
    void onSuccess(boolean online, long lastConnection, String friendConnectedUid);
}
