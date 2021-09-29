package com.frommetoyou.texting.chatModule.model.dataAccess;

import com.frommetoyou.texting.R;
import com.frommetoyou.texting.chatModule.model.LastConectionEventListener;
import com.frommetoyou.texting.chatModule.model.MessageReceivedEventListener;
import com.frommetoyou.texting.chatModule.model.SendMessageListener;
import com.frommetoyou.texting.common.Constants;
import com.frommetoyou.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.frommetoyou.texting.common.pojo.Message;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;
    private ChildEventListener mMessageEventListener;
    private ValueEventListener mFriendProfileListener;

    public RealtimeDatabase() {
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public FirebaseRealtimeDatabaseAPI getmDatabaseAPI() {
        return mDatabaseAPI;
    }

    public void subscribeToMessages(String myEmail, String friendEmail, MessageReceivedEventListener listener) {
        if (mMessageEventListener == null) {
            mMessageEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    listener.onMessageReceived(getMessage(snapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    switch (error.getCode()) {
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.chat_error_permission_denied);
                            break;
                        default:
                            listener.onError(R.string.common_error_server);
                            break;
                    }
                }
            };
        }
        mDatabaseAPI.getChatsMessagesReference(myEmail, friendEmail).addChildEventListener(mMessageEventListener);
    }



    public void unsubscribeToMessages(String myEmail, String friendEmail) {
        if (mMessageEventListener != null) {
            mDatabaseAPI.getChatsMessagesReference(myEmail, friendEmail).removeEventListener(mMessageEventListener);
        }
    }

    public void subscribeToFriend(String uid, LastConectionEventListener listener) {
        if (mFriendProfileListener == null) {
            mFriendProfileListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long lastConnectionFriend = 0;
                    String uidConnectedFriend = "";
                    try {
                        Long value = snapshot.getValue(Long.class);
                        if (value != null) {
                            lastConnectionFriend = value;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String lastConnectionWith = snapshot.getValue(String.class);
                        if (lastConnectionWith != null && !lastConnectionWith.isEmpty()) {
                            String[] values = lastConnectionWith.split(FirebaseRealtimeDatabaseAPI.SEPARATOR);
                            if (values.length > 0) {
                                lastConnectionFriend = Long.parseLong(values[0]);
                                if (values.length > 1) {
                                    uidConnectedFriend = values[1];
                                }
                            }
                        }
                    }
                    listener.onSuccess(
                            lastConnectionFriend == Constants.ONLINE_VALUE,
                            lastConnectionFriend,
                            uidConnectedFriend
                    );
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        }
        //offline sync (downloads cloud data)
        mDatabaseAPI.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).keepSynced(true);
        mDatabaseAPI.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).addValueEventListener(mFriendProfileListener);
    }

    public void unsubscribeToFriend(String uid) {
        if (mFriendProfileListener != null) {
            mDatabaseAPI.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).removeEventListener(mFriendProfileListener);
        }
    }

    /*
     * Mensajes leidos/ no leídos
     * */
    public void setMessagesSeen(String myUid, String friendUid) {
        final DatabaseReference userReference = getOneContactReference(myUid, friendUid);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Map<String, Object> update = new HashMap<>();
                    update.put(User.MESSAGES_UNREAD, 0);
                    userReference.updateChildren(update);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void sumUnseenMessaages(String myUid, String friendUid) {
        final DatabaseReference userReference = getOneContactReference(friendUid, myUid);
        userReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User user = currentData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(currentData);
                }
                user.setMessagesUnreaded(user.getMessagesUnreaded()+1);
                currentData.setValue(user);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {  }
        });
    }
    /*
    * send messages
    * */
    public void sendMessage(String newMessage, String photoUrl, String friendEmail, User myUser, SendMessageListener listener){
        Message message = new Message();
        message.setSender(myUser.getEmail());
        message.setMessage(newMessage);
        message.setPhotoUrl(photoUrl);
        DatabaseReference chatReference = mDatabaseAPI.getChatsMessagesReference(myUser.getEmail(), friendEmail);
        chatReference.push().setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null)
                    listener.onSuccess();
            }
        });
    }

    private DatabaseReference getOneContactReference(String mainUid, String childUid) {
        return mDatabaseAPI.getUserReferenceByUid(mainUid).child(FirebaseRealtimeDatabaseAPI.PATH_CONTACTS).child(childUid);
    }

    private Message getMessage(DataSnapshot snapshot) {
        Message message = snapshot.getValue(Message.class);
        if (message != null) {
            message.setUid(snapshot.getKey());
        }
        return message;
    }
}
