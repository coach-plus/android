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

        // actions need to be unique so the pending intents are unique
        // remove the timestamp to dispatch by the action name
        String action = intent.getAction();
        if(action.contains(MyFirebaseMessagingService.SEPERATOR)){
            action = action.split(MyFirebaseMessagingService.SEPERATOR)[0];
        }

        switch(action){
            case MyFirebaseMessagingService.ACTION_ATTEND_EVENT:
            case MyFirebaseMessagingService.ACTION_DECLINE_EVENT: {
                String teamId = intent.getStringExtra(MyFirebaseMessagingService.EXTRA_TEAM_ID);
                String eventId = intent.getStringExtra(MyFirebaseMessagingService.EXTRA_EVENT_ID);
                int notificationId = intent.getIntExtra(MyFirebaseMessagingService.EXTRA_NOTIFICATION_ID, -1);
                String userId = settings.getUser().get_id();
                boolean willAttend = action.equals(MyFirebaseMessagingService.ACTION_ATTEND_EVENT) ? true : false;
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
