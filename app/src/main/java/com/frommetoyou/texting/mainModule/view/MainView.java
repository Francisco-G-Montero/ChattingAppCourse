package com.frommetoyou.texting.mainModule.view;

import com.frommetoyou.texting.common.pojo.User;

public interface MainView {
    void friendAdded(User user);
    void friendUpdated(User user);
    void friendRemoved(User user);
    void requestAdded(User user);
    void requestUpdated(User user);
    void requestRemoved(User user);
    void showRequestAccepted(String username);
    void showRequestDenied();
    void showFriendRemove();
    void showError(int resMessage);

}
