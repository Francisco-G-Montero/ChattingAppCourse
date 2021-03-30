package com.frommetoyou.texting.profileModule.model;

import android.app.Activity;
import android.net.Uri;

public interface ProfileInteractor {
    void updateUserName(String username);
    void updateImage(Uri uri, String oldPhotoUrl);
}
