package com.mathandoro.coachplus.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.persistence.DataLayer;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "coach";

    String DEFAULT_CHANNEL_ID = "DEFAULT";


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        Settings settings = new Settings(getApplicationContext());
        String s  = settings.getToken();
        DataLayer dataLayer = new DataLayer(getApplicationContext());
        dataLayer.getMyUserV2(false).subscribe(user -> {
            Log.d(TAG, "Refreshed token: " + token);
        });

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // todo sendRegistrationToServer(token);  /users/[suerid]/devices { deviceId, pushId, system, ...}
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        createNotificationChannel();

        Intent acceptIntent = new Intent(this, NotificationActionReceiver.class);
        acceptIntent.setAction("action_name");
       // snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent acceptPendingIntent =
                PendingIntent.getBroadcast(this, 0, acceptIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID) // todo
                .setSmallIcon(R.drawable.ic_check_black_24dp)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_check_black_24dp, "decline", acceptPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define. needed to update message?
        int notificationId = 0;

        // todo pass notificationId inside intent so it can be deleted

        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if(notificationManager.getNotificationChannel(DEFAULT_CHANNEL_ID) != null){
                return;
            }
            String name = "default";
            String description = "default description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }

}
