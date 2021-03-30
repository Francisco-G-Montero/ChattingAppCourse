package com.frommetoyou.texting.mainModule;

import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.mainModule.events.MainEvent;

public interface MainPresenter {
    void onCreate();
    void onDestroy();
    void onPause();
    void onResume();

    void signOff();
    User getCurrentUser();
    void removeFriend(String friendUid);
    void acceptRequest(User user);
    void denyRequest(User user);
    void onEventListener(MainEvent event);
}
