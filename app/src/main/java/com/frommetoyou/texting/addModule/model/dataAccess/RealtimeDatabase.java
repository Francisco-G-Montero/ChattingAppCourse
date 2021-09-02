package com.frommetoyou.texting.addModule.model.dataAccess;

import com.frommetoyou.texting.R;
import com.frommetoyou.texting.addModule.events.AddEvent;
import com.frommetoyou.texting.common.model.BasicEventCallback;
import com.frommetoyou.texting.common.model.EventCallback;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    public RealtimeDatabase() {
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void checkIfUserExists(String email, EventCallback callback) {
        DatabaseReference usersReference = mDatabaseAPI.getRootReference().child(FirebaseRealtimeDatabaseAPI.PATH_USERS);
        Query userByEmailQuery = usersReference.orderByChild(User.EMAIL).equalTo(email).limitToFirst(1); //limit es un limite de datos recibidos por la database
        userByEmailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) callback.onSuccess();
                else callback.onError(AddEvent.ERROR_DOESNT_EXISTS, R.string.addFriend_error_doesnt_exists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(AddEvent.ERROR_SERVER, R.string.addFriend_error_message);
            }
        });
    }

    public void checkIfRequestNotExists(String email, String myUid, EventCallback callback){
        String emailEncoded = UtilsCommon.getEmailEncoded(email);
        DatabaseReference myRequestReference = mDatabaseAPI.getRequestsReference(emailEncoded).child(myUid);
        myRequestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    callback.onError(AddEvent.ERROR_DOESNT_EXISTS, R.string.addFriend_message_request_exists);
                }else
                    callback.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(AddEvent.ERROR_SERVER, R.string.addFriend_error_message);
            }
        });
    }

    public void addFriend(String email, User myUser, BasicEventCallback callback) {
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
