package com.frommetoyou.texting.addModule;

import com.frommetoyou.texting.addModule.events.AddEvent;
import com.frommetoyou.texting.addModule.model.AddInteractor;
import com.frommetoyou.texting.addModule.model.AddInteractorClass;
import com.frommetoyou.texting.addModule.view.AddView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AddPresenterClass implements AddPresenter {
    private AddView mView;
    private AddInteractor mInteractor;

    public AddPresenterClass(AddView mView) {
        this.mView = mView;
        mInteractor = new AddInteractorClass();
    }

    @Override
    public void onShow() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView = null;
    }

    @Override
    public void addFriend(String email) {
        if (mView != null) {
            mView.disableUIElements();
            mView.showProgress();
            mInteractor.addFriend(email);
        }
    }

    @Subscribe
    @Override
    public void onEventListener(AddEvent event) {
        if (mView != null) {
            mView.hideProgress();
            mView.enableUIElements();
            switch (event.getTypeEvent()) {
                case AddEvent.SEND_REQUEST_SUCCESS:
                    mView.friendAdded();
                    break;
                case AddEvent.ERROR_SERVER:
                    mView.friendNotAdded();
                    break;
            }
        }
    }
}
