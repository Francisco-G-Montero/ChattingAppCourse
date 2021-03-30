package com.frommetoyou.texting.chatModule.model;

import com.frommetoyou.texting.common.pojo.Message;

public interface MessageReceivedEventListener {
    void onMessageReceived(Message message);
    void onError(int resMessage);
}
