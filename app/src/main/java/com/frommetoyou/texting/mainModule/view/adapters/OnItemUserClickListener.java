package com.frommetoyou.texting.mainModule.view.adapters;

import com.frommetoyou.texting.common.pojo.User;

public interface OnItemUserClickListener {
    void onItemClick(User user);
    void onItemLongClick(User user);
    void onAcceptRequest(User user);
    void onDenyRequest(User user);
}
