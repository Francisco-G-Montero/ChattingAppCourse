package com.frommetoyou.texting.profileModule.model.dataAccess;

import android.net.Uri;

import com.frommetoyou.texting.R;
import com.frommetoyou.texting.common.model.EventErrorTypeListener;
import com.frommetoyou.texting.common.model.StorageUploadImageCallback;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.profileModule.events.ProfileEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.NonNull;

public class Authentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public Authentication() {
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public FirebaseAuthenticationAPI getmAuthenticationAPI() {
        return mAuthenticationAPI;
    }

    public void updateUsernameFirebaseProfile(User myUser, EventErrorTypeListener listener){
        FirebaseUser user = mAuthenticationAPI.getCurrentUser();
        if (user!=null){
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(myUser.getUsername())
                    .build();
            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()) listener.onError(ProfileEvent.ERROR_PROFILE, R.string.profile_error_userUpdated);
                }
            });
        }
    }
    public void updateImageFirebaseProfile(Uri downloadUri, StorageUploadImageCallback callback){
        FirebaseUser user = mAuthenticationAPI.getCurrentUser();
        if (user!=null){
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri)
                    .build();
            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()) callback.onError(R.string.profile_error_imageUpdated);
                    else callback.onSuccess(downloadUri);
                }
            });
        }
    }
}
