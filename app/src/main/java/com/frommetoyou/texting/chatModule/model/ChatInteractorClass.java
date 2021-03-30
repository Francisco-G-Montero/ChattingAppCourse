package com.frommetoyou.texting.chatModule.model;

import android.app.Activity;
import android.net.Uri;

import com.frommetoyou.texting.chatModule.events.ChatEvent;
import com.frommetoyou.texting.chatModule.model.dataAccess.RealtimeDatabase;
import com.frommetoyou.texting.chatModule.model.dataAccess.Storage;
import com.frommetoyou.texting.common.Constants;
import com.frommetoyou.texting.common.model.StorageUploadImageCallback;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import com.frommetoyou.texting.common.pojo.Message;
import com.frommetoyou.texting.common.pojo.User;

import org.greenrobot.eventbus.EventBus;

public class ChatInteractorClass implements ChatInteractor {
    private RealtimeDatabase mDatabase;
    private FirebaseAuthenticationAPI mAuthenticationAPI;
    private Storage mStorage;

    private User mMyUser;
    private String mFriendUid;
    private String mFriendEmail;

    private long mLastConnectionFriend;
    private String mUidConnectedFriend = "";

    public ChatInteractorClass() {
        mDatabase = new RealtimeDatabase();
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
        mStorage = new Storage();
    }

    private User getCurrentUser() {
        if (mMyUser == null) mMyUser = mAuthenticationAPI.getAuthUser();
        return mMyUser;
    }

    @Override
    public void subscribeToFriend(String friendUid, String friendEmail) {
        mFriendEmail = friendEmail;
        mFriendUid = friendUid;
        mDatabase.subscribeToFriend(friendUid, new LastConectionEventListener() {
            @Override
            public void onSuccess(boolean online, long lastConnection, String friendConnectedUid) {
                postStatusFriend(online, lastConnection);
                mUidConnectedFriend = friendConnectedUid;
                mLastConnectionFriend = lastConnection;
            }
        });
        mDatabase.setMessagesSeen(getCurrentUser().getUid(), friendUid);
    }

    @Override
    public void unsubscribeToFriend(String friendUid) {
        mDatabase.unsubscribeToFriend(friendUid);
    }

    @Override
    public void subscribeToMessages() {
        mDatabase.subscribeToMessages(getCurrentUser().getEmail(), mFriendEmail, new MessageReceivedEventListener() {
            @Override
            public void onMessageReceived(Message message) {
                String messageSender = message.getSender();
                message.setSentByMe(messageSender.equals(getCurrentUser().getEmail()));
                postMessage(message);
            }

            @Override
            public void onError(int resMessage) {
                postEvent(ChatEvent.ERROR_SERVER, resMessage);
            }
        });
        mDatabase.getmDatabaseAPI().updateMyLastConnection(Constants.ONLINE, mFriendUid, getCurrentUser().getUid());
    }

    @Override
    public void unsubscribeToMessages() {
        mDatabase.unsubscribeToMessages(getCurrentUser().getEmail(), mFriendEmail);
        mDatabase.getmDatabaseAPI().updateMyLastConnection(Constants.OFFLINE, getCurrentUser().getUid());
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(message, null);
    }

    @Override
    public void sendImage(Activity activity, Uri imageUri) {
        mStorage.uploadImageChat(activity, imageUri, getCurrentUser().getEmail(), new StorageUploadImageCallback() {
            @Override
            public void onSuccess(Uri newUri) {
                sendMessage(null, newUri.toString());
                postImageUploadSuccess();
            }

            @Override
            public void onError(int resMessage) {
                postEvent(ChatEvent.IMAGE_UPLOAD_FAILED, resMessage);
            }
        });
    }

    private void sendMessage(final String message, String photoUrl) {
        mDatabase.sendMessage(message, photoUrl, mFriendEmail, getCurrentUser(), new SendMessageListener() {
            @Override
            public void onSuccess() {
                if (!mUidConnectedFriend.equals(getCurrentUser().getUid())) {
                    mDatabase.sumUnseenMessaages(getCurrentUser().getUid(), mFriendUid);
                    // TODO: 26/3/2021 notifications
                }
            }
        });
    }

    private void postImageUploadSuccess() {
        postEvent(ChatEvent.IMAGE_UPLOAD_SUCCESS, 0, null, false, 0);
    }

    private void postEvent(int typeEvent, int resMessage) {
        postEvent(typeEvent, resMessage, null, false, 0);
    }

    private void postMessage(Message message) {
        postEvent(ChatEvent.MESSAGE_ADDED, 0, message, false, 0);
    }

    private void postStatusFriend(boolean online, long lastConnection) {
        postEvent(ChatEvent.GET_FRIEND_STATUS, 0, null, online, lastConnection);
    }

    private void postEvent(int typeEvent, int resMessage, Message message, boolean online, long lastConnection) {
        ChatEvent event = new ChatEvent();
        event.setTypeEvent(typeEvent);
        event.setResMessage(resMessage);
        event.setMessage(message);
        event.setConnected(online);
        event.setLastConnection(lastConnection);
        EventBus.getDefault().post(event);
    }
}
