package com.mathandoro.coachplus.persistence;

import com.mathandoro.coachplus.helpers.Observable;
import com.mathandoro.coachplus.models.JWTUser;

/**
 * Created by dominik on 02.06.18.
 */

public class AppState {
    public Observable<JWTUser> myUser = new Observable<>(true);

    private static AppState instance;

    private AppState(){
    }

    public static AppState instance(){
        if(instance == null){
            instance = new AppState();
        }
        return instance;
    }


    public void loadMyUser(){
        // todo
        // myUser.publish();
    }
}
