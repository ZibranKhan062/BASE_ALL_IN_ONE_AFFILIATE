package com.devapps.affiliateadmin;

import android.app.Service;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String SUBSCRIBE_TO = "userABC";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.i(TAG, "onNewToken completed with token: " + token);

        // Subscribe to topic
        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successfully subscribed to topic: " + SUBSCRIBE_TO);
                    } else {
                        Log.e(TAG, "Failed to subscribe to topic: " + SUBSCRIBE_TO, task.getException());
                    }
                });

        // Here you can send the token to your server if needed
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server
    }
}