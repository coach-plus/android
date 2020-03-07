package com.mathandoro.coachplus.helpers;

import android.content.Context;
import androidx.browser.customtabs.CustomTabsIntent;
import com.mathandoro.coachplus.R;
import saschpe.android.customtabs.CustomTabsHelper;

public class MyCustomTabsHelper {

    public static CustomTabsIntent newIntent(Context context){
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(context.getResources().getColor(R.color.colorPrimary))
                .setShowTitle(true)
                .build();
        CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent);
        return customTabsIntent;
    }
}
