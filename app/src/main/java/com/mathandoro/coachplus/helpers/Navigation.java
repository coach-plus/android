package com.mathandoro.coachplus.helpers;

import android.app.Activity;
import android.content.Intent;

import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.views.TeamView.TeamViewActivity;

public class Navigation {

    // pass null if you want to switch to any other team
    public static void navigateToMembership(Activity activity, Membership membership) {
        Settings settings = new Settings(activity.getApplicationContext());
        if(membership == null){
            settings.setActiveTeamId(null);
        }
        Intent intent = new Intent(activity, TeamViewActivity.class);
        intent.putExtra(TeamViewActivity.PARAM_MEMBERSHIP, membership);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}
