package com.mathandoro.coachplus.helpers;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.mathandoro.coachplus.R;

import androidx.annotation.StringRes;

public class SnackbarHelper {
    public static void showText(View view, @StringRes() int error){
        Snackbar.make(view, error, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * show a custom error mesage. As fallback a generic error will be used!
     * @param view
     * @param errorKey
     */
    public static void showError(View view, String errorKey){
        int errorId = view.getResources().getIdentifier(errorKey, "string", view.getContext().getPackageName());
        if(errorId == 0){
            errorId = R.string.Error;
        }
        showText(view, errorId);
    }

    public static void showText(View view, String text){
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }
}
