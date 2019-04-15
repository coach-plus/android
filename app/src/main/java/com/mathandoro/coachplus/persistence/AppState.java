package com.mathandoro.coachplus.persistence;

import com.mathandoro.coachplus.models.MyReducedUser;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by dominik on 02.06.18.
 */



public class AppState {

    public static PublishSubject<MyReducedUser> myUserChanged$ = PublishSubject.create();


}
