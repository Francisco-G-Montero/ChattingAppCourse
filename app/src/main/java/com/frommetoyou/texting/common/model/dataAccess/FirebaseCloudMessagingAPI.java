package com.frommetoyou.texting.common.model.dataAccess;

import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;

public class FirebaseCloudMessagingAPI {
    private FirebaseMessaging mFirebaseMessaging;

    private static class SingletonHolder{
        private static final FirebaseCloudMessagingAPI INSTANCE = new FirebaseCloudMessagingAPI();
    }

    public static FirebaseCloudMessagingAPI getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public FirebaseCloudMessagingAPI() {
        mFirebaseMessaging = FirebaseMessaging.getInstance();
    }

    public void subscribeToMyTopic(String myEmail){
        mFirebaseMessaging.subscribeToTopic(UtilsCommon.getEmailToTopic(myEmail))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            //reintentar y notificar
                        }
                    }
                });
    }

    public void unsubscribeToMyTopic(String myEmail){
        mFirebaseMessaging.unsubscribeFromTopic(UtilsCommon.getEmailToTopic(myEmail))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            //reintentar (no hace falta notificar al usuario)
                        }
                    }
                });
    }
}
