package com.frommetoyou.texting.mainModule.model.dataAccess;

import com.frommetoyou.texting.common.pojo.User;

public interface UserEventListener {
    void onUserAdded(User user);
    void onUserUpdated(User user);
    void onUserRemoved(User user);
    void onError(int resMessage);

}
