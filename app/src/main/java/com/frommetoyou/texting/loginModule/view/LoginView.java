package com.frommetoyou.texting.loginModule.view;

import android.content.Intent;

public interface LoginView {
    void showProgress();
    void hideProgress();
    void openMainActivity();
    void openUILogin();
    void showLoginSuccess(Intent data);
    void showMessageStarting();
    void showError(int resMessage);
}
