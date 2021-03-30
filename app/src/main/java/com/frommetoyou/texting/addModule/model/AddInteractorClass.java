package com.frommetoyou.texting.addModule.model;

import com.frommetoyou.texting.addModule.events.AddEvent;
import com.frommetoyou.texting.addModule.model.dataAccess.RealtimeDatabase;
import com.frommetoyou.texting.common.model.BasicEventCallback;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseAuthenticationAPI;

import org.greenrobot.eventbus.EventBus;

public class AddInteractorClass implements AddInteractor{
    private RealtimeDatabase mDatabase;
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public AddInteractorClass() {
        this.mDatabase = new RealtimeDatabase();
        this.mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    @Override
    public void addFriend(String email) {
        mDatabase.addFriend(email, mAuthenticationAPI.getAuthUser(), new BasicEventCallback() {
            @Override
            public void onSuccess() {
                postEvent(AddEvent.SEND_REQUEST_SUCCESS);
            }

            @Override
            public void onError() {
                postEvent(AddEvent.ERROR_SERVER);
            }
        });
    }

    private void postEvent(int typeEvent) {
        AddEvent event = new AddEvent();
        event.setTypeEvent(typeEvent);
        EventBus.getDefault().post(event);
    }
}
