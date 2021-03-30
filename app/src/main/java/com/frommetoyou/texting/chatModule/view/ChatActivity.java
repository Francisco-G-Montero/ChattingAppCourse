package com.frommetoyou.texting.chatModule.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.frommetoyou.texting.chatModule.ChatPresenter;
import com.frommetoyou.texting.chatModule.ChatPresenterClass;
import com.frommetoyou.texting.chatModule.view.adapters.ChatAdapter;
import com.frommetoyou.texting.chatModule.view.adapters.OnItemClickListener;
import com.frommetoyou.texting.common.Constants;
import com.frommetoyou.texting.common.pojo.Message;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.frommetoyou.texting.common.utils.UtilsImage;
import com.frommetoyou.texting.databinding.ActivityChatBinding;
import com.frommetoyou.texting.mainModule.view.MainActivity;
import com.frommetoyou.texting.profileModule.view.ProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frommetoyou.texting.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements OnItemClickListener, ChatView, View.OnClickListener, OnImageZoom {
    private ChatAdapter mAdapter;
    private ChatPresenter mPresenter;
    private Message messageSelected;
    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mPresenter = new ChatPresenterClass(this);
        mPresenter.onCreate();
        configAdapter();
        configRecyclerView();
        configToolbar(getIntent());
        binding.contentUser.tvCountUnread.setVisibility(View.GONE);
        binding.content.btnSendMessage.setOnClickListener(this);
        binding.content.btnGallery.setOnClickListener(this);
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
        mPresenter.onDestroy();
        binding = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finishAfterTransition();
    }

    private void configAdapter() {
        mAdapter = new ChatAdapter(new ArrayList<>(), this);
    }

    private void configRecyclerView() {
        binding.content.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.content.recyclerView.setAdapter(mAdapter);
    }

    private void configToolbar(Intent data) {
        String uid = data.getStringExtra(User.UID);
        String email = data.getStringExtra(User.EMAIL);
        mPresenter.setupFriend(uid, email);
        String photoUrl = data.getStringExtra(User.PHOTO_URL);
        binding.contentUser.tvName.setText(data.getStringExtra(User.USERNAME));
        binding.contentUser.tvStatus.setVisibility(View.VISIBLE);
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_emoticon_happy)
                .centerCrop();
        Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        binding.contentUser.ivPhoto.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_emoticon_sad));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        binding.contentUser.ivPhoto.setImageBitmap(resource);
                        return true;
                    }
                })
                .into(binding.contentUser.ivPhoto);
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /*
     * OnItemClick (adapter)
     * */
    @Override
    public void onImageLoaded() {
        binding.content.recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onClickedImage(Message message) {
        new ImageZoomFragment().show(getSupportFragmentManager(), getString(R.string.app_name));
        messageSelected = message;
    }

    /*
     * OnImageZoom
     * */
    @Override
    public Message getMessageSelected() {
        return this.messageSelected;
    }

    /*
     * ChatView
     * */
    @Override
    public void showProgress() {
        binding.content.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.content.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStatusUser(boolean connected, long lastConnection) {
        if (connected)
            binding.contentUser.tvStatus.setText(R.string.chat_status_connected);
        else
            binding.contentUser.tvStatus.setText(getString(
                    R.string.chat_status_last_connection,
                    (new SimpleDateFormat("dd-MM-yyyy - HH:mm", Locale.ROOT).format(new Date(lastConnection)))
            ));
    }

    @Override
    public void onError(int resMessage) {
        UtilsCommon.showSnackbar(binding.getRoot(), resMessage);
    }

    @Override
    public void onMessageReceived(Message message) {
        mAdapter.add(message);
        binding.content.recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void openDialogPreview(Intent data) {
        final String urlLocal = data.getDataString();
        final ViewGroup nullParent = null;
        View view = getLayoutInflater().inflate(R.layout.dialog_image_upload_preview, nullParent);
        final ImageView ivDialog = view.findViewById(R.id.ivDialog);
        final TextView tvMessage = view.findViewById(R.id.tvMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(R.string.chat_dialog_sendImage_title)
                .setPositiveButton(R.string.chat_dialog_sendImage_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.sendImage(ChatActivity.this, Uri.parse(urlLocal));
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
                        ChatActivity.this,
                        binding.getRoot(),
                        urlLocal,
                        previewImageSize,
                        previewImageSize
                );
                if (bitmap != null) {
                    ivDialog.setImageBitmap(bitmap);
                }
                tvMessage.setText(String.format(Locale.ROOT, getString(R.string.chat_dialog_sendImage_message), binding.contentUser.tvName.getText()));
            }
        });
        alertDialog.show();
    }

    /*
     * View CLick
     * */
    @Override
    public void onClick(View v) {
        if (v == binding.content.btnSendMessage) {
            if (UtilsCommon.validateMessage(binding.content.etMessage)) {
                mPresenter.sendMessage(binding.content.etMessage.getText().toString().trim());
                binding.content.etMessage.setText("");
            }
        } else if (v == binding.content.btnGallery) {
            checkPermissionToApp(Manifest.permission.READ_EXTERNAL_STORAGE, Constants.RP_STORAGE);
        }
    }

    private void fromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.RC_PHOTO_PICKER);
    }

    private void checkPermissionToApp(String permissionStr, int requestPermission) {
        if (ContextCompat.checkSelfPermission(this, permissionStr) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permissionStr}, requestPermission);
        } else fromGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constants.RP_STORAGE:
                    fromGallery();
                    break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}