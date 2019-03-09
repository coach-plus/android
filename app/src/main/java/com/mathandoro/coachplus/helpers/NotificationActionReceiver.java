package com.mathandoro.coachplus.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // todo switch between actions
        Log.d("coach", intent.toString());
        // case commit / decline event:
        // make api call to make state change
    }
}
