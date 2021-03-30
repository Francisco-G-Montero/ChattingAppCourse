package com.frommetoyou.texting.profileModule.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import com.frommetoyou.texting.R;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.frommetoyou.texting.common.utils.UtilsImage;
import com.frommetoyou.texting.databinding.ActivityProfileBinding;
import com.frommetoyou.texting.profileModule.ProfilePresenter;
import com.frommetoyou.texting.profileModule.ProfilePresenterClass;
import com.google.android.material.snackbar.Snackbar;

public class ProfileActivity extends AppCompatActivity implements ProfileView, View.OnClickListener {
    public static final int RC_PHOTO_PICKER = 22;
    private ActivityProfileBinding binding;
    private ProfilePresenter mPresenter;
    private MenuItem mCurrentMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mPresenter = new ProfilePresenterClass(this);
        mPresenter.onCreate();
        mPresenter.setupUser(
                getIntent().getStringExtra(User.USERNAME),
                getIntent().getStringExtra(User.EMAIL),
                getIntent().getStringExtra(User.PHOTO_URL)
        );
        binding.ivProfile.setOnClickListener(this);
        binding.btnEditPhoto.setOnClickListener(this);
        configActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        binding = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_profile:
                mCurrentMenuItem = item;
                if (binding.etUsername.getText() != null) {
                    mPresenter.updateUserName(binding.etUsername.getText().toString().trim());
                }
                break;
            case android.R.id.home:
                finishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data);
    }

    private void setImageProfile(String photoUrl) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_timer_sand)
                .error(R.drawable.ic_emoticon_sad)
                .centerCrop();
        Glide.with(this)
                .asBitmap()
                .apply(options)
                .load(photoUrl)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        hideProgressImage();
                        binding.ivProfile.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_upload));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        binding.ivProfile.setImageBitmap(resource);
                        hideProgressImage();
                        return true;
                    }
                })
                .into(binding.ivProfile);
    }

    private void configActionBar() {
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.ivProfile || v == binding.btnEditPhoto) {
            mPresenter.checkMode();
        }
    }
    /*
     * ProfileView
     * */

    @Override
    public void enableUIElements() {
        setInputs(true);
    }

    @Override
    public void disableUIElements() {
        setInputs(false);
    }

    private void setInputs(boolean enable) {
        binding.etUsername.setEnabled(enable);
        binding.btnEditPhoto.setVisibility(enable ? View.VISIBLE : View.GONE);
        if (mCurrentMenuItem != null)
            mCurrentMenuItem.setEnabled(enable);
    }


    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressImage() {
        binding.progressBarImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressImage() {
        binding.progressBarImage.setVisibility(View.GONE);
    }

    @Override
    public void showUserData(String username, String email, String photoUrl) {
        setImageProfile(photoUrl);
        binding.etUsername.setText(username);
        binding.etEmail.setText(email);
    }

    @Override
    public void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_PHOTO_PICKER);
    }

    @Override
    public void openDialogPreview(Intent data) {
        final String urlLocal = data.getDataString();
        final ViewGroup nullParent = null;
        View view = getLayoutInflater().inflate(R.layout.dialog_image_upload_preview, nullParent);
        final ImageView ivDialog = view.findViewById(R.id.ivDialog);
        final TextView tvMessage = view.findViewById(R.id.tvMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(R.string.profile_dialog_title)
                .setPositiveButton(R.string.profile_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.updateImage(Uri.parse(urlLocal));
                        UtilsCommon.showSnackbar(binding.getRoot(), R.string.profile_message_imageUploading, Snackbar.LENGTH_LONG);
                    }
                })
                .setNegativeButton(R.string.common_label_cancel, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                int previewImageSize = getResources().getDimensionPixelSize(R.dimen.chat_size_img_preview);
                Bitmap bitmap = UtilsImage.reduceBitmap(
                        ProfileActivity.this,
                        binding.getRoot(),
                        urlLocal,
                        previewImageSize,
                        previewImageSize
                );
                if (bitmap != null) {
                    ivDialog.setImageBitmap(bitmap);
                }
                tvMessage.setText(R.string.profile_dialog_message);
            }
        });
        alertDialog.show();
    }

    @Override
    public void menuEditMode() {
        mCurrentMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check));
    }

    @Override
    public void menuNormalMode() {
        if (mCurrentMenuItem != null) {
            mCurrentMenuItem.setEnabled(true);
            mCurrentMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pencil));
        }
    }

    @Override
    public void saveUserNameSuccess() {
        UtilsCommon.showSnackbar(binding.getRoot(), R.string.profile_message_userUpdated);
    }

    @Override
    public void updateImageSuccess(String photoUrl) {
        setImageProfile(photoUrl);
        UtilsCommon.showSnackbar(binding.getRoot(), R.string.profile_message_imageUpdated);
    }

    @Override
    public void setResultsOK(String username, String photoUrl) {
        Intent data = new Intent();
        data.putExtra(User.USERNAME, username);
        data.putExtra(User.PHOTO_URL, photoUrl);
        setResult(RESULT_OK, data);
    }

    @Override
    public void onErrorUpload(int resMessage) {
        UtilsCommon.showSnackbar(binding.getRoot(), resMessage);
    }

    @Override
    public void onError(int resMessage) {
        binding.etUsername.requestFocus();
        binding.etUsername.setError(getString(resMessage));
    }


}