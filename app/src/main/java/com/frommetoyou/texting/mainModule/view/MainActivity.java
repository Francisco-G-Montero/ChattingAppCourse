package com.frommetoyou.texting.mainModule.view;

import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import com.frommetoyou.texting.addModule.view.AddFragment;
import com.frommetoyou.texting.chatModule.view.ChatActivity;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.frommetoyou.texting.createGroupModule.view.CreateGroupActivity;
import com.frommetoyou.texting.databinding.ActivityMainBinding;
import com.frommetoyou.texting.loginModule.view.LoginActivity;
import com.frommetoyou.texting.mainModule.MainPresenter;
import com.frommetoyou.texting.mainModule.MainPresenterClass;
import com.frommetoyou.texting.mainModule.view.adapters.OnItemUserClickListener;
import com.frommetoyou.texting.mainModule.view.adapters.RequestAdapter;
import com.frommetoyou.texting.mainModule.view.adapters.UserAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.frommetoyou.texting.R;
import com.frommetoyou.texting.profileModule.view.ProfileActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialActionItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnItemUserClickListener, MainView {
    private static final int RC_PROFILE = 23;
    private ActivityMainBinding mBinding;
    private UserAdapter mUserAdapter;
    private RequestAdapter mRequestAdapter;
    private User mUser;
    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mPresenter = new MainPresenterClass(this);
        mPresenter.onCreate();
        mUser = mPresenter.getCurrentUser();
        configToolbar();
        configFab();
        configAdapters();
        configRecyclerView();
        configTutorial();
    }

    private void configFab() {
        mBinding.fab.addActionItem(
                new SpeedDialActionItem.Builder(R.id.person_add, R.drawable.ic_person_add)
                        .setLabel(getResources().getString(R.string.person_add))
                        .create()
        );
        mBinding.fab.addActionItem(
                new SpeedDialActionItem.Builder(R.id.group_create, R.drawable.ic_group)
                        .setLabel(getResources().getString(R.string.group_create))
                        .create()
        );
        mBinding.fab.setOnActionSelectedListener(actionItem -> {
            if (actionItem.getId() == R.id.person_add)
                new AddFragment().show(getSupportFragmentManager(), getString(R.string.addFriend_title));
            else if (actionItem.getId() == R.id.group_create)
                startActivity(new Intent(this, CreateGroupActivity.class));
            return false;
        });
    }

    private void configTutorial() {
        new MaterialShowcaseView.Builder(this)
                .setTarget(mBinding.fab)
                .setTargetTouchable(true)
                .setTitleText(R.string.app_name)
                .setTitleTextColor(R.color.colorAccent)
                .setContentText(R.string.main_tutorial_message)
                .setContentTextColor(ContextCompat.getColor(this, R.color.blue_50))
                .setDismissText(R.string.main_tutorial_ok)
                .setDismissStyle(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC))
                .setDismissTextColor(ContextCompat.getColor(this, android.R.color.white))
                .setMaskColour(ContextCompat.getColor(this, R.color.gray_t900))
                .singleUse(getString(R.string.main_tutorial_fabAdd))
                .setDelay(2000)
                .setFadeDuration(600) //duracion de la animacion
                .setDismissOnTargetTouch(true)
                .setDismissOnTouch(false)
                .show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        clearNotifications();
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
    }

    private void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private void configToolbar() {
        mBinding.toolbar.setTitle(mUser.getUsernameValid());
        UtilsCommon.loadImage(this, mUser.getPhotoUrl(), mBinding.ivProfile);
        setSupportActionBar(mBinding.toolbar);
    }

    private void configAdapters() {
        mUserAdapter = new UserAdapter(new ArrayList<>(), this);
        mRequestAdapter = new RequestAdapter(new ArrayList<>(), this);

    }

    private void configRecyclerView() {
        mBinding.contentMain.rvUser.setLayoutManager(new LinearLayoutManager(this));
        mBinding.contentMain.rvUser.setAdapter(mUserAdapter);
        mBinding.contentMain.rvRequests.setLayoutManager(new LinearLayoutManager(this));
        mBinding.contentMain.rvRequests.setAdapter(mRequestAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                mPresenter.signOff();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.action_profile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                profileIntent.putExtra(User.USERNAME, mUser.getUsername());
                profileIntent.putExtra(User.EMAIL, mUser.getEmail());
                profileIntent.putExtra(User.PHOTO_URL, mUser.getPhotoUrl());
                startActivityForResult(
                        profileIntent,
                        RC_PROFILE,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                );
                break;
            case R.id.action_about:
                openAbout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_PROFILE:
                    if (data != null) {
                        mUser.setUsername(data.getStringExtra(User.USERNAME));
                        mUser.setPhotoUrl(data.getStringExtra(User.PHOTO_URL));
                        configToolbar();
                    }
                    break;
            }
        }
    }

    private void openAbout() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_about, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.DialogFragmentTheme)
                .setTitle(R.string.main_menu_about)
                .setView(view)
                .setPositiveButton(R.string.common_label_ok, null)
                .setNeutralButton(R.string.about_privacy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://policity.test-android.com"));
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    /*
     * OnItemUserClickListener
     * */
    @Override
    public void onItemClick(User user) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(User.UID, user.getUid());
        intent.putExtra(User.EMAIL, user.getEmail());
        intent.putExtra(User.USERNAME, user.getUsername());
        intent.putExtra(User.PHOTO_URL, user.getPhotoUrl());
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

    }

    @Override
    public void onItemLongClick(User user) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.main_dialog_title_confirmDelete))
                .setMessage(getString(R.string.main_dialog_message_confirmDelete, user.getUsernameValid()))
                .setPositiveButton(R.string.main_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.removeFriend(user.getUid());
                    }
                })
                .setNegativeButton(R.string.common_label_cancel, null)
                .show();
    }

    @Override
    public void onAcceptRequest(User user) {
        mPresenter.acceptRequest(user);
    }

    @Override
    public void onDenyRequest(User user) {
        mPresenter.denyRequest(user);
    }
    /*
     * MainView
     * */

    @Override
    public void friendAdded(User user) {
        mUserAdapter.add(user);
    }

    @Override
    public void friendUpdated(User user) {
        mUserAdapter.updateUser(user);
    }

    @Override
    public void friendRemoved(User user) {
        mUserAdapter.remove(user);
    }

    @Override
    public void requestAdded(User user) {
        mRequestAdapter.add(user);
    }

    @Override
    public void requestUpdated(User user) {
        mRequestAdapter.updateUser(user);
    }

    @Override
    public void requestRemoved(User user) {
        mRequestAdapter.remove(user);
    }

    @Override
    public void showRequestAccepted(String username) {
        Snackbar.make(mBinding.getRoot(), getString(R.string.main_message_request_accepted, username), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showRequestDenied() {
        Snackbar.make(mBinding.getRoot(), R.string.main_message_request_denied, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showFriendRemove() {
        Snackbar.make(mBinding.getRoot(), R.string.main_message_user_removed, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showError(int resMessage) {
        Snackbar.make(mBinding.getRoot(), resMessage, Snackbar.LENGTH_LONG).show();

    }
}