package com.frommetoyou.texting.loginModule;

import android.content.Intent;

import com.frommetoyou.texting.loginModule.events.LoginEvent;

public interface LoginPresenter {
    void onCreate();
    void onResume();
    void onPause();
    void onDestroy();

    void result(int requestCode, int resultCode, Intent data);
    void getStatusAuth();
    void onEventListener(LoginEvent event);

}
