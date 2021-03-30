package com.frommetoyou.texting.common.model;

import android.net.Uri;

public interface StorageUploadImageCallback {
    void onSuccess(Uri newUri);
    void onError(int resMessage);

}
