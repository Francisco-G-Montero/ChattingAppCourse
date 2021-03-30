package com.frommetoyou.texting.loginModule.model.dataAccess;

import com.frommetoyou.texting.R;
import com.frommetoyou.texting.common.model.EventErrorTypeListener;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.loginModule.events.LoginEvent;
import com.google.android.datatransport.cct.internal.LogEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    public RealtimeDatabase() {
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void registerUser(User user){
        Map<String,Object> values= new HashMap<>();
        values.put(User.USERNAME,user.getUsername());
        values.put(User.EMAIL,user.getEmail());
        values.put(User.PHOTO_URL,user.getPhotoUrl());

        mDatabaseAPI.getUserReferenceByUid(user.getUid()).updateChildren(values);
    }
    public void checkUserExists(String uid, EventErrorTypeListener listener){
        mDatabaseAPI.getUserReferenceByUid(uid).child(User.EMAIL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            listener.onError(LoginEvent.USER_NOT_EXISTS, R.string.login_error_user_exist);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError(LoginEvent.ERROR_SERVER,R.string.login_message_error);
                    }
                });
    }
}
