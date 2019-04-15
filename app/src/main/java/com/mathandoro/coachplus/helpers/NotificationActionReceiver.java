package com.mathandoro.coachplus.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.persistence.DataLayer;

import androidx.core.app.NotificationManagerCompat;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DataLayer dataLayer = new DataLayer(context);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Settings settings = new Settings(context);

        switch(intent.getAction()){
            case MyFirebaseMessagingService.ACTION_ATTEND_EVENT:
            case MyFirebaseMessagingService.ACTION_DECLINE_EVENT: {
                String teamId = intent.getStringExtra("teamId");
                String eventId = intent.getStringExtra("eventId");
                int notificationId = intent.getIntExtra(MyFirebaseMessagingService.EXTRA_NOTIFICATION_ID, -1);
                String userId = settings.getUser().get_id();
                boolean willAttend = intent.getAction().equals(MyFirebaseMessagingService.ACTION_ATTEND_EVENT) ? true : false;
                attendEvent(notificationManager, dataLayer, willAttend, notificationId, teamId, eventId, userId);
                break;
            }
        }
    }

    private void attendEvent(NotificationManagerCompat notificationManager, DataLayer dataLayer,
                             boolean willAttend, int notificationId, String teamId, String eventId, String userId){
        dataLayer.setWillAttend(teamId, eventId, userId, willAttend).subscribe(participation -> {
            notificationManager.cancel(notificationId);
        }, error -> {
            // todo
        });
    }
}
