package com.frommetoyou.texting.common.model.dataAccess;

import com.frommetoyou.texting.common.Constants;
import com.frommetoyou.texting.common.pojo.User;
import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRealtimeDatabaseAPI {
    public static final String SEPARATOR = "___&___";
    public static final String PATH_USERS = "users";
    public static final String PATH_CONTACTS = "contacts";
    public static final String PATH_REQUESTS = "requests";
    private static final String PATH_CHATS = "chats";
    private static final String PATH_MESSAGES = "messages";

    private DatabaseReference mDatabaseReference;

    private static class SingletonHolder {

        private static final FirebaseRealtimeDatabaseAPI INSTANCE = new FirebaseRealtimeDatabaseAPI();


    }

    public static FirebaseRealtimeDatabaseAPI getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private FirebaseRealtimeDatabaseAPI() {
        this.mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    //References
    public DatabaseReference getRootReference() {
        return mDatabaseReference.getRoot();
    }

    public DatabaseReference getUserReferenceByUid(String uid) {
        return getRootReference().child(PATH_USERS).child(uid);
    }

    public DatabaseReference getContactsReference(String uid) {
        return getUserReferenceByUid(uid).child(PATH_CONTACTS);
    }

    public DatabaseReference getRequestsReference(String encodedEmail) {
        return getRootReference().child(PATH_REQUESTS).child(encodedEmail);
    }

    public DatabaseReference getChatsReference(String myEmail, String friendEmail) {
        String myEmailEncoded = UtilsCommon.getEmailEncoded(myEmail);
        String friendEmailEncoded = UtilsCommon.getEmailEncoded(friendEmail);
        String keyChat = myEmailEncoded + FirebaseRealtimeDatabaseAPI.SEPARATOR + friendEmailEncoded;
        if (myEmailEncoded.compareTo(friendEmailEncoded) > 0) {
            keyChat = friendEmailEncoded + FirebaseRealtimeDatabaseAPI.SEPARATOR + myEmailEncoded;
        }
        return getRootReference().child(PATH_CHATS).child(keyChat);
    }

    public DatabaseReference getChatsMessagesReference(String myEmail, String friendEmail) {
        return getChatsReference(myEmail, friendEmail).child(PATH_MESSAGES);
    }

    public void updateMyLastConnection(boolean online, String uid) {
        updateMyLastConnection(online, "", uid);
    }

    public void updateMyLastConnection(boolean online, String uidFriend, String uid) {
        String lastConnectionWith = Constants.ONLINE_VALUE + SEPARATOR + uidFriend;
        Map<String, Object> values = new HashMap<>();
        values.put(User.LAST_CONNECTION_WITH, online ? lastConnectionWith : ServerValue.TIMESTAMP);
        //offline sync
        getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).keepSynced(true);
        getUserReferenceByUid(uid).updateChildren(values);
        if (online)
            getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).onDisconnect().setValue(ServerValue.TIMESTAMP);
        else
            getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).onDisconnect().cancel();
    }
}
