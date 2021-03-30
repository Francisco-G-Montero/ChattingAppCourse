package com.frommetoyou.texting.profileModule.model.dataAccess;

public interface UpdateUserListener {
    void onSuccess();
    void onNotifyContacts();
    void onError(int resMessage);

}
