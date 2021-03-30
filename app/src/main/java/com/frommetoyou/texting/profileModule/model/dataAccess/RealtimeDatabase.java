package com.frommetoyou.texting.profileModule.model.dataAccess;

import android.net.Uri;

import com.frommetoyou.texting.R;
import com.frommetoyou.texting.common.model.StorageUploadImageCallback;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.frommetoyou.texting.common.pojo.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    public RealtimeDatabase() {
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void changeUserName(User myUser, UpdateUserListener listener) {
        if (mDatabaseAPI.getUserReferenceByUid(myUser.getUid()) != null) {
            Map<String, Object> update = new HashMap<>();
            update.put(User.USERNAME, myUser.getUsername());
            mDatabaseAPI.getUserReferenceByUid(myUser.getUid()).updateChildren(update)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            listener.onSuccess();
                            notifyContactsNewUsername(myUser, listener);
                        }
                    });
        }
    }

    private void notifyContactsNewUsername(User myUser, UpdateUserListener listener) {
        mDatabaseAPI.getContactsReference(myUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String friendUid = child.getKey();
                    DatabaseReference reference = getContactsReference(friendUid, myUser.getUid());
                    Map<String, Object> update = new HashMap<>();
                    update.put(User.USERNAME, myUser.getUsername());
                    reference.updateChildren(update);
                }
                listener.onNotifyContacts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(R.string.profile_error_userUpdated);
            }
        });
    }

    private DatabaseReference getContactsReference(String friendUid, String userUid) {
        return mDatabaseAPI.getUserReferenceByUid(friendUid)
                .child(FirebaseRealtimeDatabaseAPI.PATH_CONTACTS)
                .child(userUid);
    }

    public void updatePhotoUrl(Uri downloadUri, String myUid, StorageUploadImageCallback callback){
        if (mDatabaseAPI.getUserReferenceByUid(myUid) != null){
            Map<String, Object> update = new HashMap<>();
            update.put(User.PHOTO_URL, downloadUri.toString());
            mDatabaseAPI.getUserReferenceByUid(myUid).updateChildren(update)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            callback.onSuccess(downloadUri);
                            notifyContactsNewPhoto(downloadUri.toString(), myUid, callback);
                        }
                    });
        }
    }

    private void notifyContactsNewPhoto(String photoUrl, String myUid, StorageUploadImageCallback callback) {
        mDatabaseAPI.getContactsReference(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String friendUid = child.getKey();
                    DatabaseReference reference = getContactsReference(friendUid, myUid);
                    Map<String, Object> update = new HashMap<>();
                    update.put(User.PHOTO_URL, photoUrl);
                    reference.updateChildren(update);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(R.string.profile_error_imageUpdated);
            }
        });
    }
}
