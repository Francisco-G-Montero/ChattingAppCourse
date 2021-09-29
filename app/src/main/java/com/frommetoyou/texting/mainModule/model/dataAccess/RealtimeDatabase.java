package com.frommetoyou.texting.mainModule.model.dataAccess;

import android.util.Log;

import com.frommetoyou.texting.R;
import com.frommetoyou.texting.common.model.BasicEventCallback;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.frommetoyou.texting.common.pojo.Message;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;
    private FirebaseAuthenticationAPI mAuthenticationAPI;
    private ChildEventListener mUserEventListener;
    private ChildEventListener mRequestEventListener;

    public RealtimeDatabase() {
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    /*
     * references
     * */

    public FirebaseRealtimeDatabaseAPI getmDatabaseAPI() {
        return mDatabaseAPI;
    }

    private DatabaseReference getUsersReference() {
        return mDatabaseAPI.getRootReference().child(FirebaseRealtimeDatabaseAPI.PATH_USERS);
    }

    /*
     * public methods
     * */
    public void subscribeToUserList(String myUid, final UserEventListener listener) {
        if (mUserEventListener == null) {
            mUserEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    getUser(snapshot, listener::onUserAdded);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    getUser(snapshot, user -> listener.onUserUpdated(user));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    getUser(snapshot, user -> listener.onUserRemoved(user));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    switch (error.getCode()) {
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.main_error_permission_denied);
                            break;
                        default:
                            listener.onError(R.string.common_error_server);
                            break;
                    }
                }
            };
        }
        mDatabaseAPI.getContactsReference(myUid).addChildEventListener(mUserEventListener);
    }

    private User getUser(DataSnapshot snapshot, final GetUserEventListener listener) {
        User user = snapshot.getValue(User.class);
        if (user != null) {
            user.setUid(snapshot.getKey());
            mDatabaseAPI.getChatsMessagesReference(mAuthenticationAPI.getAuthUser().getEmail(), user.getEmail())
                    .limitToLast(1)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String lastMessage = "";
                                Message message = new Message();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                 message = dataSnapshot.getValue(Message.class);
                                if ( message.getPhotoUrl() != null)
                                    lastMessage = "Foto \uD83D\uDCF7";
                                else lastMessage = message.getMessage();
                                user.setLastMessage(lastMessage);
                            }
                            listener.onGetUser(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //TODO
                        }
                    });
        }
        return user;
    }

    public void subscribeToRequests(String email, final UserEventListener listener) {
        if (mRequestEventListener == null) {
            mRequestEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    getUser(snapshot, user -> listener.onUserAdded(user));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                   getUser(snapshot, user -> listener.onUserUpdated(user));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    getUser(snapshot, user -> listener.onUserRemoved(user));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    listener.onError(R.string.common_error_server);
                }
            };
        }
        final String emailEndoded = UtilsCommon.getEmailEncoded(email);
        mDatabaseAPI.getRequestsReference(emailEndoded).addChildEventListener(mRequestEventListener);
    }

    public void unsubscribeToUsers(String uid) {
        if (mUserEventListener != null) {
            mDatabaseAPI.getContactsReference(uid).removeEventListener(mUserEventListener);
        }
    }

    public void unsubscribeToRequests(String email) {
        if (mRequestEventListener != null) {
            final String emailEncoded = UtilsCommon.getEmailEncoded(email);
            mDatabaseAPI.getRequestsReference(emailEncoded).removeEventListener(mRequestEventListener);
        }
    }

    public void removeUser(String friendUid, String myUid, final BasicEventCallback callback) {
        Map<String, Object> removeUserMap = new HashMap<>();
        removeUserMap.put(myUid + "/" + FirebaseRealtimeDatabaseAPI.PATH_CONTACTS + "/" + friendUid, null);
        removeUserMap.put(friendUid + "/" + FirebaseRealtimeDatabaseAPI.PATH_CONTACTS + "/" + myUid, null);
        getUsersReference().updateChildren(removeUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null)
                    callback.onSuccess();
                else
                    callback.onError();
            }
        });
    }

    public void acceptRequest(User user, User myUser, final BasicEventCallback callback) {
        Map<String, String> userRequestMap = new HashMap<>();
        userRequestMap.put(User.USERNAME, user.getUsername());
        userRequestMap.put(User.EMAIL, user.getEmail());
        userRequestMap.put(User.PHOTO_URL, user.getPhotoUrl());

        Map<String, String> myUserMap = new HashMap<>();
        myUserMap.put(User.USERNAME, myUser.getUsername());
        myUserMap.put(User.EMAIL, myUser.getEmail());
        myUserMap.put(User.PHOTO_URL, myUser.getPhotoUrl());

        final String myEmailEncoded = UtilsCommon.getEmailEncoded(myUser.getEmail());
        Map<String, Object> acceptRequest = new HashMap<>();
        acceptRequest.put(FirebaseRealtimeDatabaseAPI.PATH_USERS + "/" + user.getUid() + "/" +
                FirebaseRealtimeDatabaseAPI.PATH_CONTACTS + "/" + myUser.getUid(), myUserMap);
        acceptRequest.put(FirebaseRealtimeDatabaseAPI.PATH_USERS + "/" + myUser.getUid() + "/" +
                FirebaseRealtimeDatabaseAPI.PATH_CONTACTS + "/" + user.getUid(), userRequestMap);
        acceptRequest.put(FirebaseRealtimeDatabaseAPI.PATH_REQUESTS + "/" + myEmailEncoded + "/" + user.getUid(), null);

        mDatabaseAPI.getRootReference().updateChildren(acceptRequest, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null)
                    callback.onSuccess();
                else
                    callback.onError();
            }
        });
    }

    public void denyRequest(User user, String myEmail, final BasicEventCallback callback) {
        final String myEmailEncoded = UtilsCommon.getEmailEncoded(myEmail);
        mDatabaseAPI.getRequestsReference(myEmailEncoded).child(user.getUid()).removeValue(
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null)
                            callback.onSuccess();
                        else
                            callback.onError();
                    }
                }
        );
    }
}
