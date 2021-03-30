package com.frommetoyou.texting.profileModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.profileModule.events.ProfileEvent;
import com.frommetoyou.texting.profileModule.model.ProfileInteractor;
import com.frommetoyou.texting.profileModule.model.ProfileInteractorClass;
import com.frommetoyou.texting.profileModule.view.ProfileActivity;
import com.frommetoyou.texting.profileModule.view.ProfileView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ProfilePresenterClass implements ProfilePresenter {
    private ProfileView mView;
    private ProfileInteractor mInteractor;
    private boolean isEditMode = false;
    private User mUser;

    public ProfilePresenterClass(ProfileView mView) {
        this.mView = mView;
        mInteractor = new ProfileInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView = null;
    }

    @Override
    public void setupUser(String username, String email, String photoUrl) {
        mUser = new User();
        mUser.setUsername(username);
        mUser.setEmail(email);
        mUser.setPhotoUrl(photoUrl);
        mView.showUserData(username, email, photoUrl);
    }

    @Override
    public void checkMode() {
        if (isEditMode)
            mView.launchGallery();
    }

    @Override
    public void updateUserName(String username) {
        if (isEditMode) {
            if (setProgress()) {
                mView.showProgress();
                mInteractor.updateUserName(username);
                mUser.setUsername(username);
            }
        } else {
            isEditMode = true;
            mView.menuEditMode();
            mView.enableUIElements();
        }
    }

    @Override
    public void updateImage(Uri uri) {
        if (setProgress()) {
            mView.showProgressImage();
            mInteractor.updateImage(uri, mUser.getPhotoUrl());
        }
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ProfileActivity.RC_PHOTO_PICKER:
                    mView.openDialogPreview(data);
                    break;
            }
        }
    }

    @Subscribe
    @Override
    public void onEventListener(ProfileEvent event) {
        if (mView != null) {
            mView.hideProgress();
            switch (event.getTypeEvent()) {
                case ProfileEvent.ERROR_USERNAME:
                    mView.enableUIElements();
                    mView.onError(event.getResMessage());
                    break;
                case ProfileEvent.ERROR_PROFILE:
                case ProfileEvent.ERROR_SERVER:
                case ProfileEvent.ERROR_IMAGE:
                    mView.enableUIElements();
                    mView.onErrorUpload(event.getResMessage());
                    break;
                case ProfileEvent.SAVED_USERNAME:
                    mView.saveUserNameSuccess();
                    saveSuccess();
                    break;
                case ProfileEvent.UPLOADED_IMAGE:
                    mView.updateImageSuccess(event.getPhotoUrl());
                    mUser.setPhotoUrl(event.getPhotoUrl());
                    saveSuccess();
                    break;
            }
        }
    }

    private void saveSuccess() {
        mView.menuNormalMode();
        mView.setResultsOK(mUser.getUsername(), mUser.getPhotoUrl());
        isEditMode = false;
    }

    private boolean setProgress() {
        if (mView != null) {
            mView.disableUIElements();
            return true;
        }
        return false;
    }
}
