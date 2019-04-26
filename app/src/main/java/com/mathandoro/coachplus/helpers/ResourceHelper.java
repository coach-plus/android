package com.mathandoro.coachplus.helpers;

import android.content.Context;

public class ResourceHelper {

    public static String getStringResourceByName(Context context, String resourceName) {
        String packageName = context.getPackageName();
        int resId = context.getApplicationContext().getResources().getIdentifier(resourceName, "string", packageName);
        if(resId == 0){
            return null;
        }
        return context.getString(resId);
    }
}
