package com.frommetoyou.texting.loginModule.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.frommetoyou.texting.R;
import com.frommetoyou.texting.databinding.ActivityLoginBinding;
import com.frommetoyou.texting.loginModule.LoginPresenter;
import com.frommetoyou.texting.loginModule.LoginPresenterClass;
import com.frommetoyou.texting.mainModule.view.MainActivity;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements LoginView {
    public static final int RC_SIGN_IN = 21;

    private ActivityLoginBinding binding;
    private LoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mPresenter = new LoginPresenterClass(this);
        mPresenter.onCreate();
        mPresenter.getStatusAuth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        mPresenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data);
    }

    //LoginView
    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void openMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void openUILogin() {
        AuthUI.IdpConfig googleIpd = new AuthUI.IdpConfig.GoogleBuilder().build();
        AuthUI.IdpConfig emailIdp = new AuthUI.IdpConfig.EmailBuilder().build();
        AuthUI.getInstance().signOut(this);
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setTosAndPrivacyPolicyUrls("google.com","google.com")
                .setAvailableProviders(Arrays.asList(
                        emailIdp,
                        googleIpd
                ))
                .setTheme(R.style.BlueTheme)
                .setLogo(R.mipmap.ic_launcher)
                .build(), RC_SIGN_IN);
    }

    @Override
    public void showLoginSuccess(Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        String email = "";
        if (response != null) {
            email = response.getEmail();
        }
        Toast.makeText(getApplicationContext(), getString(R.string.login_message_success, email), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessageStarting() {
        binding.tvMessage.setText(R.string.login_message_loading);
    }

    @Override
    public void showError(int resMessage) {
        Toast.makeText(getApplicationContext(),resMessage,Toast.LENGTH_LONG).show();
    }
}