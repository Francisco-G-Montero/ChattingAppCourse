package com.frommetoyou.texting.profileModule.model;

import android.app.Activity;
import android.net.Uri;

import com.frommetoyou.texting.common.model.EventErrorTypeListener;
import com.frommetoyou.texting.common.model.StorageUploadImageCallback;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.profileModule.events.ProfileEvent;
import com.frommetoyou.texting.profileModule.model.dataAccess.Authentication;
import com.frommetoyou.texting.profileModule.model.dataAccess.RealtimeDatabase;
import com.frommetoyou.texting.profileModule.model.dataAccess.Storage;
import com.frommetoyou.texting.profileModule.model.dataAccess.UpdateUserListener;

import org.greenrobot.eventbus.EventBus;

public class ProfileInteractorClass implements ProfileInteractor {
    private Authentication mAuthentication;
    private RealtimeDatabase mDatabase;
    private Storage mStorage;
    private User mMyUser;

    public ProfileInteractorClass() {
        mAuthentication = new Authentication();
        mDatabase = new RealtimeDatabase();
        mStorage = new Storage();
    }

    private User getCurrentUser() {
        if (mMyUser == null) {
            mMyUser = mAuthentication.getmAuthenticationAPI().getAuthUser();
        }
        return mMyUser;
    }

    @Override
    public void updateUserName(String username) {
        User myUser = getCurrentUser();
        myUser.setUsername(username);
        mDatabase.changeUserName(myUser, new UpdateUserListener() {
            @Override
            public void onSuccess() {
                mAuthentication.updateUsernameFirebaseProfile(myUser, new EventErrorTypeListener() {
                    @Override
                    public void onError(int typeEvent, int resMessage) {
                        postEvent(typeEvent, null, resMessage);
                    }
                });
            }

            @Override
            public void onNotifyContacts() {
                postUsernameSuccess();
            }

            @Override
            public void onError(int resMessage) {
                postEvent(ProfileEvent.ERROR_USERNAME, null, resMessage);
            }
        });
    }

    @Override
    public void updateImage(Uri uri, String oldPhotoUrl) {
        mStorage.uploadProfileImage(uri, getCurrentUser().getEmail(), new StorageUploadImageCallback() {
            @Override
            public void onSuccess(Uri uri) {
                mDatabase.updatePhotoUrl(uri, getCurrentUser().getUid(), new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {
                        postEvent(ProfileEvent.UPLOADED_IMAGE, newUri.toString(), 0);
                    }

                    @Override
                    public void onError(int resMessage) {
                        postEvent(ProfileEvent.ERROR_SERVER, resMessage);
                    }
                });
                mAuthentication.updateImageFirebaseProfile(uri, new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {
                        mStorage.deleteOldProfileImage(oldPhotoUrl, newUri.toString());
                    }

                    @Override
                    public void onError(int resMessage) {
                        postEvent(ProfileEvent.ERROR_PROFILE, resMessage);
                    }
                });
            }

            @Override
            public void onError(int resMessage) {
                postEvent(ProfileEvent.ERROR_IMAGE, resMessage);
            }
        });
    }


    private void postUsernameSuccess() {
        postEvent(ProfileEvent.SAVED_USERNAME, null, 0);
    }

    private void postEvent(int typeEvent, int resMessage) {
        postEvent(typeEvent, null, resMessage);
    }

    private void postEvent(int typeEvent, String photoUrl, int resMessage) {
        ProfileEvent event = new ProfileEvent();
        event.setPhotoUrl(photoUrl);
        event.setResMessage(resMessage);
        event.setTypeEvent(typeEvent);
        EventBus.getDefault().post(event);
    }
}
