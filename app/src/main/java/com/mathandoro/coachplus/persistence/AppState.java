package com.mathandoro.coachplus.persistence;

import android.content.Context;

import com.mathandoro.coachplus.models.JWTUser;

import io.reactivex.Observable;

/**
 * Created by dominik on 02.06.18.
 */

public class AppState {

    private static AppState instance;
    private DataLayer dataLayer;

    private AppState(Context context){
        this.dataLayer = new DataLayer(context);
    }

    public static AppState instance(Context context){
        if(instance == null){
            instance = new AppState(context);

        }
        return instance;
    }

}
