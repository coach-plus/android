package com.mathandoro.coachplus.persistence;

import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.models.TeamMember;

import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by dominik on 02.06.18.
 */



public class AppState {

    private static AppState instance = null;

    public final PublishSubject<MyReducedUser> myUserChanged$ = PublishSubject.create();
    public final PublishSubject<List<Event>> events$ = PublishSubject.create();
    public final PublishSubject<Event> selectedEvent$ = PublishSubject.create();
    public final PublishSubject<List<TeamMember>> members$ = PublishSubject.create();

    private List<Event> events;
    private List<TeamMember> members;

    private AppState(){
    }

    public static AppState instance(){
        if(instance == null){
            instance = new AppState();
        }
        return instance;
    }

    public void setEvents(List<Event> events){
        this.events = events;
        this.events$.onNext(events);
    }

    public void setMembers(List<TeamMember> members){
        this.members = members;
        this.members$.onNext(members);
    }

    public void setEvent(Event event){
        for (int i=0; i < events.size(); i++) {
            Event event1 = events.get(i);
            if (event1.get_id().equals(event.get_id())) {
                events.set(i, event);
                break;
            }
        }
        this.setEvents(events);
    }

}
