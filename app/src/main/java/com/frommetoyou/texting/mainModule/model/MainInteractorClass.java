package com.frommetoyou.texting.mainModule.model;

import com.frommetoyou.texting.common.Constants;
import com.frommetoyou.texting.common.model.BasicEventCallback;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.mainModule.events.MainEvent;
import com.frommetoyou.texting.mainModule.model.dataAccess.Authentication;
import com.frommetoyou.texting.mainModule.model.dataAccess.RealtimeDatabase;
import com.frommetoyou.texting.mainModule.model.dataAccess.UserEventListener;

import org.greenrobot.eventbus.EventBus;

public class MainInteractorClass implements MainInteractor {
    private RealtimeDatabase mDatabase;
    private Authentication mAuthentication;
    private User mMyUser = null;

    public MainInteractorClass() {
        mDatabase = new RealtimeDatabase();
        mAuthentication = new Authentication();
    }

    @Override
    public void subscribeToUserList() {
        mDatabase.subscribeToUserList(getCurrentUser().getUid(), new UserEventListener() {
            @Override
            public void onUserAdded(User user) {
                postEvent(MainEvent.USER_ADDED, user);
            }

            @Override
            public void onUserUpdated(User user) {
                postEvent(MainEvent.USER_UPDATED, user);
            }

            @Override
            public void onUserRemoved(User user) {
                postEvent(MainEvent.USER_REMOVED, user);
            }

            @Override
            public void onError(int resMessage) {
                postError(resMessage);
            }
        });
        mDatabase.subscribeToRequests(getCurrentUser().getEmail(), new UserEventListener() {
            @Override
            public void onUserAdded(User user) {
                postEvent(MainEvent.REQUEST_ADDED, user);
            }

            @Override
            public void onUserUpdated(User user) {
                postEvent(MainEvent.REQUEST_UPDATED, user);
            }

            @Override
            public void onUserRemoved(User user) {
                postEvent(MainEvent.REQUEST_REMOVED, user);
            }

            @Override
            public void onError(int resMessage) {
                postEvent(MainEvent.ERROR_SERVER);
            }
        });
        changeConnectionStatus(Constants.ONLINE);
    }

    private void changeConnectionStatus(boolean online) {
        mDatabase.getmDatabaseAPI().updateMyLastConnection(online, getCurrentUser().getUid());
    }

    @Override
    public void unsubscribeToUserList() {
        mDatabase.unsubscribeToUsers(getCurrentUser().getUid());
        mDatabase.unsubscribeToRequests(getCurrentUser().getEmail());
        changeConnectionStatus(Constants.OFFLINE);
    }

    @Override
    public void signOff() {
        mAuthentication.signOff();
    }

    @Override
    public User getCurrentUser() {
        return mMyUser == null ? mAuthentication.getmAuthenticationAPI().getAuthUser() : mMyUser;
    }

    @Override
    public void removeFriend(String friendUid) {
        mDatabase.removeUser(friendUid, getCurrentUser().getUid(), new BasicEventCallback() {
            @Override
            public void onSuccess() {
                postEvent(MainEvent.USER_REMOVED);
            }

            @Override
            public void onError() {
                postEvent(MainEvent.ERROR_SERVER);
            }
        });
    }

    @Override
    public void acceptRequest(User user) {
        mDatabase.acceptRequest(user, getCurrentUser(), new BasicEventCallback() {
            @Override
            public void onSuccess() {
                postEvent(MainEvent.REQUEST_ACCEPTED, user);
            }

            @Override
            public void onError() {
                postEvent(MainEvent.ERROR_SERVER);
            }
        });
    }

    @Override
    public void denyRequest(User user) {
        mDatabase.denyRequest(user, getCurrentUser().getEmail(), new BasicEventCallback() {
            @Override
            public void onSuccess() {
                postEvent(MainEvent.REQUEST_DENIED);
            }

            @Override
            public void onError() {
                postEvent(MainEvent.ERROR_SERVER);
            }
        });
    }

    private void postError(int resMessage) {
        postEvent(MainEvent.ERROR_SERVER, null, resMessage);
    }

    private void postEvent(int typeEvent) {
        postEvent(typeEvent, null, 0);
    }

    private void postEvent(int typeEvent, User user){
        postEvent(typeEvent, user, 0);
    }

    private void postEvent(int typeEvent, User user, int resMessage) {
        MainEvent event = new MainEvent();
        event.setTypeEvent(typeEvent);
        event.setUser(user);
        event.setResMessage(resMessage);
        EventBus.getDefault().post(event);
    }
}
