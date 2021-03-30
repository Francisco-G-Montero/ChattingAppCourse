package com.frommetoyou.texting.mainModule;

import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.mainModule.events.MainEvent;
import com.frommetoyou.texting.mainModule.model.MainInteractor;
import com.frommetoyou.texting.mainModule.model.MainInteractorClass;
import com.frommetoyou.texting.mainModule.view.MainView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainPresenterClass implements MainPresenter {
    private MainView mView;
    private MainInteractor mInteractor;

    public MainPresenterClass(MainView mView) {
        this.mView = mView;
        mInteractor = new MainInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView = null;
    }

    @Override
    public void onPause() {
        if (mView != null) {
            mInteractor.unsubscribeToUserList();
        }
    }

    @Override
    public void onResume() {
        if (mView != null) {
            mInteractor.subscribeToUserList();
        }
    }

    @Override
    public void signOff() {
        mInteractor.unsubscribeToUserList();
        mInteractor.signOff();
        onDestroy();
    }

    @Override
    public User getCurrentUser() {
        return mInteractor.getCurrentUser();
    }

    @Override
    public void removeFriend(String friendUid) {
        if (mView != null) {
            mInteractor.removeFriend(friendUid);
        }
    }

    @Override
    public void acceptRequest(User user) {
        if (mView != null) {
            mInteractor.acceptRequest(user);
        }
    }

    @Override
    public void denyRequest(User user) {
        if (mView != null) {
            mInteractor.denyRequest(user);
        }
    }

    @Subscribe
    @Override
    public void onEventListener(MainEvent event) {
        if (mView != null) {
            User user = event.getUser();
            switch (event.getTypeEvent()) {
                case MainEvent.USER_ADDED:
                    mView.friendAdded(user);
                    break;
                case MainEvent.USER_UPDATED:
                    mView.friendUpdated(user);
                    break;
                case MainEvent.USER_REMOVED:
                    if (user != null) {
                        mView.friendRemoved(user);
                    } else {
                        mView.showFriendRemove();
                    }
                    break;
                case MainEvent.REQUEST_ADDED:
                    mView.requestAdded(user);
                    break;
                case MainEvent.REQUEST_UPDATED:
                    mView.requestUpdated(user);
                    break;
                case MainEvent.REQUEST_REMOVED:
                    mView.requestRemoved(user);
                    break;
                case MainEvent.REQUEST_ACCEPTED:
                    mView.showRequestAccepted(user.getUsername());
                    break;
                case MainEvent.REQUEST_DENIED:
                    mView.showRequestDenied();
                    break;
                case MainEvent.ERROR_SERVER:
                    mView.showError(event.getResMessage());
                    break;
            }
        }
    }
}
