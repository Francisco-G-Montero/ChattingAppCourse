package com.frommetoyou.texting.chatModule.model;

import android.app.Activity;
import android.net.Uri;

public interface ChatInteractor {
    void subscribeToFriend(String friendUid, String friendEmail);
    void unsubscribeToFriend(String friendUid);

    void subscribeToMessages();
    void unsubscribeToMessages();

    void sendMessage(String message);
    void sendImage(Activity activity, Uri imageUri);

}
