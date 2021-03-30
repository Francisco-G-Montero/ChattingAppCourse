package com.frommetoyou.texting.profileModule.view;

import android.content.Intent;

public interface ProfileView {
    void enableUIElements();
    void disableUIElements();

    void showProgress();
    void hideProgress();

    void showProgressImage();
    void hideProgressImage();

    void showUserData(String username, String email, String photoUrl);
    void launchGallery();
    void openDialogPreview(Intent data);

    void menuEditMode();
    void menuNormalMode();

    void saveUserNameSuccess();
    void updateImageSuccess(String photoUrl);
    void setResultsOK(String username, String photoUrl);
    void onErrorUpload(int resMessage);
    void onError(int resMessage);

}
