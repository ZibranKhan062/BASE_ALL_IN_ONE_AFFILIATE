package com.affiliate.earning;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "mFirebaseIIDService";
    private static final String SUBSCRIBE_TO = "Appusers";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        // Subscribe to topic with the userId
        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successfully subscribed to topic: " + SUBSCRIBE_TO);
                    } else {
                        Log.e(TAG, "Failed to subscribe to topic: " + SUBSCRIBE_TO);
                    }
                });

        Log.i(TAG, "onTokenRefresh completed with token: " + token);
    }
}