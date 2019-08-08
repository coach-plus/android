package com.mathandoro.coachplus.helpers;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String SEPERATOR = "::";

    String TAG = "coach";
    String DEFAULT_CHANNEL_ID = "DEFAULT";

    public static final String ACTION_ATTEND_EVENT = "attend_event";
    public static final String ACTION_DECLINE_EVENT = "decline_event";

    private static final String PAYLOAD_TITLE = "title";
    private static final String PAYLOAD_SUBTITLE = "subtitle";

    private static final String PAYLOAD_CATEGORY = "category";
    private static final String PAYLOAD_EVENT_ID = "eventId";
    private static final String PAYLOAD_TEAM_ID = "teamId";
    private static final String PAYLOAD_TEAM_NAME = "teamName";
    private static final String PAYLOAD_CONTENT = "content";

    public static final String EXTRA_TEAM_ID = "teamId";
    public static final String EXTRA_EVENT_ID = "eventId";
    public static final String EXTRA_NOTIFICATION_ID = "notificationId";



    private static final String NOTIFICATION_EVENT_REMINDER = "EVENT_REMINDER";


    public MyFirebaseMessagingService(){

    }

    @Override
    public void onNewToken(String newToken) {
        DataLayer dataLayer = new DataLayer(this.getApplication());
        Settings settings = new Settings(getApplicationContext());
        Log.d(TAG, "Refreshed token: " + newToken);
        String deviceId = settings.getDeviceId();
        if(deviceId == null){
            // initial registration will be handled by first login
            return;
        }
        dataLayer.registerOrUpdateDevice(deviceId, newToken).subscribe(data -> {}, error -> {});
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> payload = remoteMessage.getData();
        String category = payload.get(PAYLOAD_CATEGORY);
        switch (category){
            case NOTIFICATION_EVENT_REMINDER:
                createReminderNotification(payload.get(PAYLOAD_TEAM_ID), payload.get(PAYLOAD_TEAM_NAME), payload.get(PAYLOAD_EVENT_ID),
                        payload.get(PAYLOAD_TITLE), payload.get(PAYLOAD_SUBTITLE), payload.get(PAYLOAD_CONTENT));
        }
        createNotificationChannel();

    }

    private void createReminderNotification(String teamId, String teamName, String eventId, String eventName, String time, String description){
        int notificationId = createUniqueID();

        Intent acceptIntent = getAttendanceIntent(ACTION_ATTEND_EVENT, teamId, eventId, notificationId);
        Intent declineIntent = getAttendanceIntent(ACTION_DECLINE_EVENT, teamId, eventId, notificationId);

        final int acceptRequestCode = 0;
        final int declineRequestCode = 0;

        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, acceptRequestCode, acceptIntent, 0);
        PendingIntent declinePendingIntent = PendingIntent.getBroadcast(this, declineRequestCode, declineIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_trillerpfeife_links)
                .setContentTitle(eventName)
                .setSubText(teamName)
                .setContentText(time)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                //.setContentIntent(pendingIntent) // todo switch to team and open event?
                .addAction(R.drawable.ic_check_black_24dp, "accept", acceptPendingIntent)
                .addAction(R.drawable.ic_check_black_24dp, "decline", declinePendingIntent); // todo icons

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, builder.build());
    }

    private Intent getAttendanceIntent(String action, String teamId, String eventId, int notificationId){
        Intent intent = new Intent(this, NotificationActionReceiver.class);

        intent.setAction(action + SEPERATOR + notificationId);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        intent.putExtra(EXTRA_TEAM_ID, teamId);
        intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);

        return intent;
    }

    public int createUniqueID(){
        return (int)System.currentTimeMillis();
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
