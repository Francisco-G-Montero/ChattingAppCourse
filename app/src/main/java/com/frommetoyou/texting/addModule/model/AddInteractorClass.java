package com.frommetoyou.texting.addModule.model;

import com.frommetoyou.texting.addModule.events.AddEvent;
import com.frommetoyou.texting.addModule.model.dataAccess.RealtimeDatabase;
import com.frommetoyou.texting.common.model.BasicEventCallback;
import com.frommetoyou.texting.common.model.EventCallback;
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
        mDatabase.checkIfUserExists(email, new EventCallback() {
            @Override
            public void onSuccess() {
                mDatabase.checkIfRequestNotExists(email, mAuthenticationAPI.getCurrentUser().getUid(), new EventCallback() {
                    @Override
                    public void onSuccess() {
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

                    @Override
                    public void onError(int typeEvent, int resMessage) {
                        postEvent(typeEvent, resMessage);
                    }
                });
            }

            @Override
            public void onError(int typeEvent, int resMessage) {
                postEvent(typeEvent, resMessage);
            }
        });
    }

    private void postEvent(int typeEvent){
        postEvent(typeEvent, 0);
    }
    private void postEvent(int typeEvent, int resMessage) {
        AddEvent event = new AddEvent();
        event.setTypeEvent(typeEvent);
        event.setResMessage(resMessage);
        EventBus.getDefault().post(event);
    }
}
