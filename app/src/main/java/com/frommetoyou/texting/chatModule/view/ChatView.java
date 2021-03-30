package com.frommetoyou.texting.chatModule.view;

import android.content.Intent;

import com.frommetoyou.texting.common.pojo.Message;

public interface ChatView {
    void showProgress();
    void hideProgress();

    void onStatusUser(boolean connected, long lastConnection);
    void onError(int resMessage);
    void onMessageReceived(Message message);
    void openDialogPreview(Intent data);
}
