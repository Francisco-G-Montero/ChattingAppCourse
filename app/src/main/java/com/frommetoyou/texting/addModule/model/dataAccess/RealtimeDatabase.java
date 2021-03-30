package com.frommetoyou.texting.addModule.model.dataAccess;

import com.frommetoyou.texting.common.model.BasicEventCallback;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    public RealtimeDatabase() {
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void addFriend(String email, User myUser, BasicEventCallback callback){
        Map<String, Object> myUserMap = new HashMap<>();
        myUserMap.put(User.USERNAME, myUser.getUsername());
        myUserMap.put(User.EMAIL, myUser.getEmail());
        myUserMap.put(User.PHOTO_URL, myUser.getPhotoUrl());
        final String emailEncoded = UtilsCommon.getEmailEncoded(email);
        DatabaseReference userReference = mDatabaseAPI.getRequestsReference(emailEncoded);
        userReference.child(myUser.getUid()).updateChildren(myUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onError();
            }
        });
    }
}
