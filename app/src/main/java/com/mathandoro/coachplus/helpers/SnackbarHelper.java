package com.mathandoro.coachplus.helpers;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.StringRes;

public class SnackbarHelper {
    public static void showError(View view, @StringRes() int error){
        Snackbar.make(view, error, Snackbar.LENGTH_SHORT).show();
    }
}
