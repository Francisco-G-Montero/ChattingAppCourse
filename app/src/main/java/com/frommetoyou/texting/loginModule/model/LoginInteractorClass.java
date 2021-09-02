package com.frommetoyou.texting.loginModule.model;

import android.util.Log;

import com.frommetoyou.texting.common.model.EventErrorTypeListener;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseCloudMessagingAPI;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.loginModule.events.LoginEvent;
import com.frommetoyou.texting.loginModule.model.dataAccess.Authentication;
import com.frommetoyou.texting.loginModule.model.dataAccess.RealtimeDatabase;
import com.frommetoyou.texting.loginModule.model.dataAccess.StatusAuthCallback;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

public class LoginInteractorClass implements LoginInteractor {
    private Authentication mAuthentication;
    private RealtimeDatabase mDatabase;
    //notifications
    private FirebaseCloudMessagingAPI mCloudMessagingAPI;

    public LoginInteractorClass() {
        mAuthentication = new Authentication();
        mDatabase = new RealtimeDatabase();
        //notifications
        mCloudMessagingAPI = FirebaseCloudMessagingAPI.getInstance();
    }

    @Override
    public void onResume() {
        mAuthentication.onResume();
    }

    @Override
    public void onPause() {
        mAuthentication.onPause();
    }

    @Override
    public void getStatusAuth() {
        mAuthentication.getStatusAuth(new StatusAuthCallback() {
            @Override
            public void onGetUser(FirebaseUser user) {
                postEvent(LoginEvent.STATUS_AUTH_SUCCESS, user);
                mDatabase.checkUserExists(mAuthentication.getCurrentUser().getUid(), new EventErrorTypeListener() {
                    @Override
                    public void onError(int typeEvent, int resMessage) {
                        if (typeEvent == LoginEvent.USER_NOT_EXISTS){
                            registerUser();
                        }else{
                            postEvent(typeEvent);
                        }
                    }
                });
                mCloudMessagingAPI.subscribeToMyTopic(user.getEmail());
            }

            @Override
            public void onLaunchUILogin() {
                postEvent(LoginEvent.STATUS_AUTH_ERROR);
            }
        });
    }

    private void registerUser() {
        User currentUser = mAuthentication.getCurrentUser();
        mDatabase.registerUser(currentUser);

    }

    private void postEvent(int typeEvent) {
        postEvent(typeEvent, null);
    }

    private void postEvent(int typeEvent, FirebaseUser user) {
        LoginEvent event = new LoginEvent();
        event.setTypeEvent(typeEvent);
        event.setUser(user);
        EventBus.getDefault().post(event);
    }
}
