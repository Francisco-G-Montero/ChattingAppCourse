package com.frommetoyou.texting.mainModule.model;

import com.frommetoyou.texting.common.pojo.User;

public interface MainInteractor {
    void subscribeToUserList();
    void unsubscribeToUserList();
    void signOff();
    User getCurrentUser();
    void removeFriend(String friendUid);
    void acceptRequest(User user);
    void denyRequest(User user);
}
