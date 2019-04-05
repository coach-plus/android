package com.mathandoro.coachplus.persistence;

import com.mathandoro.coachplus.models.JWTUser;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by dominik on 02.06.18.
 */



public class AppState {

    public static PublishSubject<JWTUser> myUserChanged$ = PublishSubject.create();


}
