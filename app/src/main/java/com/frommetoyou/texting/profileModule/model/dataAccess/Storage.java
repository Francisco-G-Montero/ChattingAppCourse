package com.frommetoyou.texting.profileModule.model.dataAccess;

import android.app.Activity;
import android.net.Uri;

import com.frommetoyou.texting.R;
import com.frommetoyou.texting.common.model.StorageUploadImageCallback;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseStorageAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;

public class Storage {
    private static final String PATH_PROFILE = "profile";
    private FirebaseStorageAPI mStorageAPI;

    public Storage() {
        mStorageAPI = FirebaseStorageAPI.getInstance();
    }

    public void uploadProfileImage(Uri imageUri, String email, StorageUploadImageCallback callback) {
        if (imageUri.getLastPathSegment() != null) {
            final StorageReference photoRef = mStorageAPI.getPhotosReferenceByEmail(email).child(PATH_PROFILE).child(imageUri.getLastPathSegment());
            photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                callback.onSuccess(uri);
                            } else
                                callback.onError(R.string.profile_error_imageUpdated);
                        }
                    });
                }
            });
        } else
            callback.onError(R.string.profile_error_invalid_image);
    }

    public void deleteOldProfileImage(String oldPhotoUrl, String downloadUrl) {
        if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()) {
            StorageReference storageReference = mStorageAPI.getmFirebaseStorage().getReferenceFromUrl(downloadUrl);
            StorageReference oldStorageReference = mStorageAPI.getmFirebaseStorage().getReferenceFromUrl(oldPhotoUrl);
            if (!oldStorageReference.getPath().equals(storageReference.getPath())) {
                oldStorageReference.delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteOldProfileImage(oldPhotoUrl, downloadUrl);
                    }
                });
            }
        }
    }
}
