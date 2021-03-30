package com.frommetoyou.texting.addModule;

import com.frommetoyou.texting.addModule.events.AddEvent;

public interface AddPresenter {
    void onShow();
    void onDestroy();
    void addFriend(String email);
    void onEventListener(AddEvent event);
}
